package com.compute;

import beans.VehicleMsg;
import lombok.val;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import utils.clickhouseutil.ClickHouseUtil;

import java.util.Properties;

import static org.apache.flink.table.api.Expressions.$;
public class Vehmsg_Kafka2Ck2 {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.92.151:9092");
        //    properties.setProperty("zookeeper.connect", zk_connect)
        properties.setProperty("group.id", "flnkk01");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");

        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<String>("vehiclemsg", new SimpleStringSchema(), properties);
        DataStreamSource<String> ds = env.addSource(myConsumer);

        SingleOutputStreamOperator<VehicleMsg> veh_ds = ds.map(line -> {
                    String[] strings = line.toString().split(",");
                    VehicleMsg vehicleMsg = new VehicleMsg(strings[0].split("=")[1],
                            strings[1].split("=")[1],
                            strings[2].split("=")[1],
                            strings[3].split("=")[1],
                            strings[4].split("=")[1]);
                    return vehicleMsg;
                }
        );

        Table table = tableEnv.fromDataStream(veh_ds,
                $("vin"),
                $("milage"),
                $("sendtime"),
                $("speed"),
                $("veh_moudle"));
        Table res = tableEnv.sqlQuery("select vin,milage as type,speed as item from " + table);
        tableEnv.toAppendStream(res, Car.class).addSink(ClickHouseUtil.getJdbcSink("insert into vcar (vin,type,item) values(?,?,?)"));
//        tableEnv.toAppendStream(table,VehicleMsg.class).print();
        env.execute();
    }
}

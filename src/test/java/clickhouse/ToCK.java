package clickhouse;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import static org.apache.flink.table.api.Expressions.rowInterval;

import java.net.CacheRequest;

import static org.apache.flink.table.api.Expressions.$;

public class ToCK {
    public static void main(String[] args) throws Exception {
        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
//        env.setParallelism(8);
        //2.使用连接器的方式读取Kafka的数据
        SingleOutputStreamOperator<Car> car_ds = env.readTextFile("F:\\bitnei\\myself\\src\\main\\resources\\input_dir")
                .map(line -> {
                    String[] split = line.split(",");
                    System.out.println(split[0]);
                    Car car = new Car(split[0], split[1], split[2]);
                    return car;
                });

        //3.使用连接器创建表
        Table car_table = tableEnv.fromDataStream(car_ds);
        tableEnv.registerTable("car_table", car_table);



        Table res = tableEnv.sqlQuery("select * from car_table");
        DataStream<Car> carDataStream = tableEnv.toAppendStream(res, Car.class);

        tableEnv.toAppendStream(res, Car.class).addSink(ClickHouseUtil.getJdbcSink("insert into vcar (vin,type,item) values(?,?,?)"));
        //4.查询数据
//        Table resultTable = sensor.groupBy($("SUBTYPE"))
//                .select($("SUBTYPE"), $("SUBTYPE").count());

        //6.将表转换为流进行输出
//        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEnv.toRetractStream(resultTable, Row.class);
//        DataStream<DataTest2> rowDataStream = tableEnv.toAppendStream(sensor, DataTest2.class);
//        tuple2DataStream.print();
//        rowDataStream.print();
//        tableEnv.toAppendStream(sensor, Row.class).print();
//        rowDataStream.addSink(ClickHouseUtil.getJdbcSink("insert into vcar (SUBTYPE,TIME,RECVTIME) values(?,?,?)"));


        //7.执行任务
        env.execute();
    }
}

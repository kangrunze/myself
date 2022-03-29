import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.*;

import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import static org.apache.flink.table.api.Expressions.$;

public class FlinkSqlKafka {


    public static void main(String[] args) throws Exception {

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
//        env.setParallelism(8);
        //2.使用连接器的方式读取Kafka的数据
        tableEnv.connect(new Kafka()
                .version("universal")
                .topic("pay")
                .startFromLatest()
                .property(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092")
                .property(ConsumerConfig.GROUP_ID_CONFIG, "BigData0821"))//消费者组
                .withSchema(new Schema()
                        .field("SUBTYPE", DataTypes.STRING())
                        .field("TIME", DataTypes.STRING())
                        .field("RECVTIME", DataTypes.STRING())
                )
//                .withFormat(new Csv().fieldDelimiter(' ').lineDelimiter("\n"))
//                .withFormat(new Csv())
                .withFormat(new Json())
                .createTemporaryTable("sensor");

        //3.使用连接器创建表
        Table sensor = tableEnv.from("sensor");

        tableEnv.registerTable("sensorTable", sensor);
        //4.查询数据
        Table resultTable = sensor.groupBy($("SUBTYPE"))
                .select($("SUBTYPE"), $("SUBTYPE").count());

        //6.将表转换为流进行输出
//        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEnv.toRetractStream(resultTable, Row.class);
        DataStream<DataTest2> rowDataStream = tableEnv.toAppendStream(sensor, DataTest2.class);
//        tuple2DataStream.print();
//        rowDataStream.print();
//        tableEnv.toAppendStream(sensor, Row.class).print();
        rowDataStream.addSink(ClickHouseUtil.getJdbcSink("insert into vcar (SUBTYPE,TIME,RECVTIME) values(?,?,?)"));


        //7.执行任务
        env.execute();

    }

}

package autoguigu;


import clickhouse.Car;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.Slide;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Properties;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.lit;

public class FlinkSQL06_TableAPI_GroupWindow_SlidlingWindow_ProcessTime {

    public static void main(String[] args) throws Exception {

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2.读取端口数据创建流并转换每一行数据为JavaBean对象
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.92.151:9092");
        //    properties.setProperty("zookeeper.connect", zk_connect)
        properties.setProperty("group.id", "flnkk01");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        FlinkKafkaConsumer myConsumer = new FlinkKafkaConsumer("test", new SimpleStringSchema(), properties);
        DataStreamSource ds = env.addSource(myConsumer);
        SingleOutputStreamOperator  waterSensorDS = ds .map(line -> {
            String[] split = line.toString().split(",");
            return new WaterSensor(split[0],
                    (split[1]),
                    (split[2]));
        });


        //3.将流转换为表并指定处理时间
        Table table = tableEnv.fromDataStream(waterSensorDS,
                $("id"),
//                $("ts"),
//                $("vc"),
                $("pt").proctime());

        //4.开滑动窗口计算WordCount
        Table result = table.window(Slide.over(lit(10).seconds())
                .every(lit(10).seconds())
                .on($("pt"))
                .as("sw"))
                .groupBy($("id"), $("sw"))
                .select($("id"), $("id").count());

        //5.将结果表转换为流进行输出
        tableEnv.toAppendStream(result, Row.class).print();

        //6.执行任务
        env.execute();

    }

}
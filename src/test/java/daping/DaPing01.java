package daping;

import clickhouse.Car;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.GroupWindowedTable;
import org.apache.flink.table.api.Slide;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import static org.apache.flink.table.api.Expressions.lit;

import java.util.Properties;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.rowInterval;

public class DaPing01 {
    public static void main(String[] args) throws Exception {

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamExecutionEnvironment env2 = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
//        env.setParallelism(8);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.92.151:9092");
        //    properties.setProperty("zookeeper.connect", zk_connect)
        properties.setProperty("group.id", "flnkk01");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        FlinkKafkaConsumer myConsumer = new FlinkKafkaConsumer("test", new SimpleStringSchema(), properties);
        DataStreamSource ds = env.addSource(myConsumer);
        SingleOutputStreamOperator<Car>  car_ds = ds.map(line -> {
            String[] split = line.toString().split(",");
            System.out.println(split[0]);
            Car car = new Car(split[0], split[1], split[2]);
            return car;
        });

        Table car_table = tableEnv.fromDataStream(car_ds,
                $("vin"),
                $("type"),
                $("pt").proctime());
        Table result = car_table.window(Slide.over(lit(6).seconds())
                .every(lit(2).seconds())
                .on($("pt"))
                .as("sw"))
                .groupBy($("vin"), $("sw"))
                .select($("vin"), $("vin").count());

        //5.将结果表转换为流进行输出
        tableEnv.toAppendStream(result, Row.class).print();

        //5.将结果表转换为流进行输出
        tableEnv.toRetractStream(result, Row.class).print();


        env.execute();

    }
}

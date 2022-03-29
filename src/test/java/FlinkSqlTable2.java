import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

public class FlinkSqlTable2 {

        public static void main(String[] args) throws Exception {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(1);
            DataStreamSource<DataTest> waterSensorStream =
                    env.fromElements(
                            new DataTest("sensor_1", "1000L, 10"),
                            new DataTest("sensor_1", "2000L, 20"),
                            new DataTest("sensor_2", "3000L, 30"),
                            new DataTest("sensor_1", "4000L, 40"),
                            new DataTest("sensor_1", "5000L, 50"),
                            new DataTest("sensor_2", "6000L, 60"));

            // 1. 创建表的执行环境
            DataStreamSource<String> stringDataStreamSource = env.readTextFile("F:\\bitnei\\myself\\src\\main\\resources\\input_dir");
            StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
            // 2. 创建表: 将流转换成动态表. 表的字段名从pojo的属性名自动抽取
//            Table table = tableEnv.fromDataStream(stringDataStreamSource);
            // 3. 对动态表进行查询
//            Table resultTable = table
//                    .where($("id").isEqual("sensor_1"))
//                    .select($("id"), $("ts"), $("vc"));
//            // 4. 把动态表转换成流
//            DataStream<Row> resultStream = tableEnv.toAppendStream(table, Row.class);
//            resultStream.print();
//            try {
//                env.execute();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


            Schema schema = new Schema()
                    .field("id", DataTypes.STRING())
                    .field("ts", DataTypes.STRING());

// 2.2 连接文件, 并创建一个临时表, 其实就是一个动态表
            tableEnv
                    .connect(new FileSystem().path("F:\\bitnei\\myself\\src\\main\\resources\\input_dir"))
                    .withFormat(new Csv().fieldDelimiter(',').lineDelimiter("\n"))
//                    .withFormat(new Json())
                    .withSchema(schema)
                    .createTemporaryTable("sensor");
            // 3. 做成表对象, 然后对动态表进行查询
            Table sensorTable = tableEnv.from("sensor");
            Table resultTable = sensorTable
                    .groupBy($("id"))
                    .select($("id"), $("id").count().as("cnt"));
            // 4. 把动态表转换成流. 如果涉及到数据的更新, 要用到撤回流. 多个了一个boolean标记
//            tableEnv.toRetractStream(resultTable, Row.class).print();

            tableEnv.registerTable("sensorTable",sensorTable);

            Table table = tableEnv.sqlQuery("select id,count(*) from sensorTable group by id");

            tableEnv.toRetractStream(table, Row.class).print();
            tableEnv.toAppendStream(table, Row.class).print();
            resultTable.printSchema();
//            resultStream.print();
            env.execute();
        }




}

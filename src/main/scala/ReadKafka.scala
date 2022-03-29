import java.util.Properties

import beans.WaterSensor
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.types.Row


object ReadKafka {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(1)
    val tableEnv: StreamTableEnvironment = StreamTableEnvironment.create(env)

    //2.读取端口数据创建流并转换每一行数据为JavaBean对象
    val properties = new Properties
    properties.setProperty("bootstrap.servers", "192.168.92.151:9092")
    //    properties.setProperty("zookeeper.connect", zk_connect)
    properties.setProperty("group.id", "flnkk01")
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("auto.offset.reset", "latest")
    val myConsumer = new FlinkKafkaConsumer("test", new SimpleStringSchema, properties)
    val ds: DataStream[String] = env.addSource(myConsumer)

    val waterSensor_ds: DataStream[WaterSensor] = ds.map(line => {
      val strings: Array[String] = line.toString.split(",")
      new WaterSensor(strings(0), strings(1), strings(2))
    })

    //3.将流转换为表并指定处理时间
    val table: Table = tableEnv.fromDataStream(waterSensor_ds,
      $("id"), //                $("ts"),
      $("vc"),
      $("ts"),
      $("pt").proctime())

    //4.开滑动窗口计算WordCount
    //    val result: Table = table.window(Slide.over(lit(10).seconds).every(lit(10).seconds).on($("pt")).as("sw")).groupBy($("id"), $("sw")).select($("id"), $("id").count)

    /*
    *  tableEnv.sqlQuery("select " +
                 "id," +
                 "count(id)," +
                 "tumble_start(pt, INTERVAL '5' second) as windowStart from " +
                 table +
                 " group by id,tumble(pt, INTERVAL '5' second)");
    * */

    val table1 = tableEnv.sqlQuery("select id ,count(*) ," +
      "hop_start(pt,INTERVAL '2' second, INTERVAL '6' second) as windowStart from " +
      table +
      " group by id,hop(pt,INTERVAL '2' second, INTERVAL '6' second)")
    //5.将结果表转换为流进行输出
    //    tableEnv.toAppendStream(result,classOf[Row]).print
    //    tableEnv.toRetractStream(result).print()
    table.printSchema()
    val value: DataStream[(Boolean, Row)] = tableEnv.toRetractStream(table1)
    value.print()
    //    tableEnv.toRetractStream(table1).p
    //6.执行任务
    env.execute()
  }
}

package com.computer

import java.util.Properties

import beans.VehicleMsg
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.jdbc.JdbcSink
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import utils.clickhouseutil.ClickHouseUtil

class Vehmsg_Kafka2Ck {

//  def docompute(): Unit = {

//  }
}

object Vehmsg_Kafka2Ck {
  def main(args: Array[String]): Unit = {
    val ck = new Vehmsg_Kafka2Ck
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
    val myConsumer = new FlinkKafkaConsumer("vehiclemsg", new SimpleStringSchema, properties)
    val ds: DataStream[String] = env.addSource(myConsumer)

    val veh_ds: DataStream[VehicleMsg] = ds.map(line => {
      val strings: Array[String] = line.toString.split(",")
      new VehicleMsg(strings(0).split("=")(1),
        strings(1).split("=")(1),
        strings(2).split("=")(1),
        strings(3).split("=")(1),
        strings(4).split("=")(1))
    })

    //3.将流转换为表并指定处理时间
    val table: Table = tableEnv.fromDataStream(veh_ds,
      $("vin"),
      $("milage"),
      $("sendtime"),
      $("speed"),
      $("veh_moudle"),
      $("pt").proctime())

    //4.开滑动窗口计算
    val table1 = tableEnv.sqlQuery("select vin ,count(*) ," +
      "hop_start(pt,INTERVAL '2' second, INTERVAL '6' second) as windowStart from " +
      table +
      " group by vin,hop(pt,INTERVAL '2' second, INTERVAL '6' second)")


    //5.将结果表转换为流进行输出
    //    table.printSchema()
    //    val res: DataStream[(Boolean, Row)] = tableEnv.toRetractStream(table1)
    //    res.print()
    val tableres: Table = tableEnv.sqlQuery("select vin,milage,speed from " + table)
    val res: DataStream[VehicleMsg] = tableEnv.toAppendStream(tableres)
    val item: DataStream[String] = res.map(_.toString)

//res.addSink(new FlinkKafkaProducer[VehicleMsg]("","",new SimpleStringSchema()))
//    item.addSink(new FlinkKafkaProducer[String]("brokerList", "topic_out", new SimpleStringSchema()))

    //6.执行任务
    env.execute()
  }
}

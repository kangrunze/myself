package com.computer

import com.alibaba.fastjson.JSON
import org.apache.flink.api.common.serialization.SimpleStringSchema
//import org.apache.flink.streaming.api.datastream.{DataStream, SingleOutputStreamOperator}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import java.util.Properties

import beans.VehicleMsg
import org.apache.flink.streaming.api.scala.DataStream

object Readkafka {
  def main(args: Array[String]): Unit = {
    //1.获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    import org.apache.flink.api.scala._
    //2.从Kafka读取数据
    val properties = new Properties
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.40.18:9092")
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test1")
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    val kafkaDS = env.addSource(new FlinkKafkaConsumer[String]("qtsec-log", new SimpleStringSchema, properties))

    //jsonformat
    val ds: DataStream[String] = kafkaDS

ds.map(a=>{})

    //3.将数据打印


    //4.执行任务
    env.execute
  }
}

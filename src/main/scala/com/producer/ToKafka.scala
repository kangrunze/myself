package com.producer

import java.text.SimpleDateFormat
import java.util.Properties

import beans.VehicleMsg
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.util.Random

class ToKafka extends Runnable{


  override def run(): Unit = {

    ToKafka.sendMsg2Kafka(1000,"")
  }
}
object ToKafka{

  val prop = new Properties
  //添加配置
  prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.92.151:9092")

  def sendMsg2Kafka(loop:Int,msg:String): Unit ={

    // {"SUBTYPE":"2315","TIME":"202202,28213229","RECVTIME":"20220228213239",}
    val kafkaProducer = new KafkaProducer[String,String](prop)
    for (i <- 0 until loop) {
      var msg_1 = new VehicleMsg("LNPHDNAG4xxx"+ (100 + Random.nextInt(5)),
        Random.nextInt(150) + 20000 + "",
       "20220326",
        Random.nextInt(150) + "",
        "car"
      ).toString
      val record = new ProducerRecord[String,String]("vehiclemsg", msg_1)
      System.out.println(i + "======>")
      val in: Integer = i
      val i1: Int = in.toString.hashCode
      val i1str: Integer = i1
      System.out.println("打印数据" + record)
      //            println("打印数据" + payData)
      Thread.sleep(Random.nextInt(100))
      kafkaProducer.send(record)
    }
  }

  def main(args: Array[String]): Unit = {
    val tokafka = new ToKafka
    val thread = new Thread(tokafka)
    thread.run()

  }
}

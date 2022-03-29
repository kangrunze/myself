import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;


public class ToKafka {
    public static void main(String[] args) throws InterruptedException {
//        new ToKafka.writeToKafkaTopic(10,"");
        ToKafka toKafka = new ToKafka();
        toKafka.writeToKafkaTopic(1000,"");

    }
    public void writeToKafkaTopic(int loop_len,String delay) throws InterruptedException {

        Properties prop = new Properties();
        //添加配置
//        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, LocalConfig.KAFKA_BOOTSTRAP_SERVERS)
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.92.151:9092");
//        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        ProducerRecord record = new ProducerRecord("test","{\"SUBTYPE\":\"2315\",\"TIME\":\"202202,28213229\",\"RECVTIME\":\"20220228213239\",}");
        // {"SUBTYPE":"2315","TIME":"202202,28213229","RECVTIME":"20220228213239",}
        KafkaProducer kafkaProducer = new KafkaProducer(prop);
        for (int i = 0; i < loop_len ; i++) {
            System.out.println(i + "======>");
            Integer in = i;
            int  i1 = in.toString().hashCode();
            Integer i1str = i1;
            System.out.println("打印数据" + record);
//            println("打印数据" + payData)
            Thread.sleep(100L);
            kafkaProducer.send(record);
        }

//        for (i <- 0 to loop_len) {
//
//            print(i + "======>")
//
//            val flag = rand.nextInt(2)
//            flag match {
//                case 0 =>
//                    val record = new ProducerRecord[String, String]("login", loginData.toString())
//                    producer.send(record)
//                    println("打印数据" + loginData)
//                case 1 =>
//                    val record = new ProducerRecord[String, String]("pay", payData.toString())
//                    println("打印数据" + payData)
//                    producer.send(record)
//            }
//
//        }
    }
}

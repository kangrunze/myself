import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object Readflie {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration())
    val value: DataStream[String] = env.readTextFile("F:\\bitnei\\myself\\src\\main\\resources\\input_dir")
    value.print()
    env.execute()


  }

}

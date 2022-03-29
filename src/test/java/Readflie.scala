import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{DataTypes, Table}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.descriptors.{Csv, FileSystem, Schema}
import org.apache.flink.types.Row

/**
 * @Package
 * @author 大数据老哥
 * @date 2020/12/12 21:22
 * @version V1.0
 *          第一个Flinksql测试案例
 */

object FlinkSqlTable {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val environment: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val schema: Schema = new Schema()
      .field("id", DataTypes.STRING())
      .field("ts", DataTypes.STRING())
    import org.apache.flink.api.scala._
    val tableEnv :StreamTableEnvironment  = StreamTableEnvironment.create(env)

    tableEnv.connect(new FileSystem().path("F:\\bitnei\\myself\\src\\main\\resources\\input_dir"))
      .withFormat(new Csv().fieldDelimiter(',').lineDelimiter("\n"))
      .withSchema(schema)
      .createTemporaryTable("sensor");

    val table: Table = tableEnv.from("sensor")
    table.printSchema()
//    tableEnv.toRetractStream(table,classOf[Row]).print()

  }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClickHouseTest3 {


    public static void main(String[] args)  {
        System.out.println("test click house");
        try {
            Class.forName("com.github.housepower.jdbc.ClickHouseDriver");//第三方jar包
//            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");//官方

            //如果官方jar包引入,则下面不能执行，如果包官方jar包去掉，则可以执行
            Connection connection = DriverManager.getConnection("jdbc:clickhouse://nn1.hadoop:8123/default?socket_timeout=300000");//第三方
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select count(*) from vcar");

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
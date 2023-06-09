import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static DbConnection instance;
    private Connection connection;

    private DbConnection() {
        String url="jdbc:mysql://localhost:3306/company_account?useTimeZone=true&serverTimezone=EET";
        String user="root";
        String password="Px707244@PH925477";
        try {
            this.connection= DriverManager.getConnection(url,user,password);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DbConnection getInstance() throws SQLException {
        if (instance==null){
            instance=new DbConnection();
        } else if (instance.getConnection().isClosed()){
            instance=new DbConnection();
        }
        return instance;
    }
}



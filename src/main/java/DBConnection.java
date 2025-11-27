import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/luxury_drives";
    private static final String USER = "root";
    private static final String PASSWORD = "33333333";

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver"; 
 
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // load the driver so java can talk to mysql
        Class.forName(DRIVER_CLASS); 
        
        // return a live connection object
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void closeConnection(Connection conn) {
        // safely close the connection if it exists
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // print sql errors if closing fails
                e.printStackTrace();
            }
        }
    }
}

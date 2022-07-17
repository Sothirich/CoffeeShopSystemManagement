package Admin;
import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    Connection linkDatabase;
    String databaseName = "coffeeshop";
    String user = "coffeeAdmin";
    String password = "test123";
    String URL = "jdbc:mysql://localhost/" + databaseName; 

    public Database(){}

    public Connection connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            linkDatabase = DriverManager.getConnection(URL, user, password);
        } catch (Exception e) {
            System.out.println("Failed to connect to the database!");
        }
        
        return linkDatabase;
    }
}   

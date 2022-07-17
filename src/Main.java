import java.sql.*;

import Admin.Database;
import Admin.admin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;  
import javafx.scene.image.Image;    
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    void QuerycheckCancelPending () {
        String checkquery = "SELECT COUNT(1) FROM `history` WHERE status = 'Accepted' and accepted_by = '" + admin.getCurrentAdmin() + "'";
        
        Database db = new Database();
        Connection connectDb = db.connect();

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkquery);

            while (queryResult.next()) {
                if (queryResult.getInt(1) != 0){
                    String changequery = "UPDATE history SET status = 'Pending', accepted_by = NULL WHERE status = 'Accepted' and accepted_by = '" + admin.getCurrentAdmin() + "'";
                    try {
                        Statement statement1 = connectDb.createStatement();
                        statement1.executeUpdate(changequery);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
        window.setTitle("Coffee Shop System Management");
        window.getIcons().add(new Image("/resources/Background/icon.png"));
        window.setResizable(false);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DefaultInterface.fxml"));
        Scene scene = new Scene(loader.load());
        window.setScene(scene);
        window.centerOnScreen();
        window.show();

        window.setOnCloseRequest(e -> {
            admin.OfflineStatus();
            QuerycheckCancelPending();
            window.close();
        });         
    }   
}
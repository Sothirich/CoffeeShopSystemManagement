package Admin;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;

public class AdminInterfaceController implements Initializable{

    Stage window;
    Scene scene;
    Parent root;

    TimerTask task;
    Timer timer;

    Database db = new Database();
    Connection connectDb = db.connect();

    LocalDate currentDate;

    int currentData;
    boolean newData= false;
    boolean isStillAcceptedOrder = false;

    @FXML private BorderPane SceneBorderPane;
    @FXML private Button overviewBtn;
    @FXML private Button acceptOrderBtn; 
    @FXML private Button pendingOrderBtn;
    @FXML private Button updateBtn;
    @FXML private Button historyBtn;
    @FXML private Button logoutBtn;
    @FXML private Button addUserBtn;
    @FXML private Button changePWBtn;

    @FXML private Label currentAdminLabel;

    @FXML private ImageView orderAlert;
    @FXML private ImageView pendingAlert;

    @FXML private HBox overviewBox;
    @FXML private HBox acceptOrderBox;
    @FXML private HBox pendingOrderBox;
    @FXML private HBox updateDrinkBox;
    @FXML private HBox checkHistoryBox;
    @FXML private HBox addUserBox;
    @FXML private HBox changePasswordBox;

    private void QuerycheckCancelPending () {
        String checkquery = "SELECT COUNT(1) FROM `history` WHERE status = 'Accepted' and accepted_by = '" + admin.getCurrentAdmin() + "'";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkquery);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 0){
                    isStillAcceptedOrder = false;
                } else {
                    isStillAcceptedOrder = true;
                }
            }
        } catch (Exception e) {
        }
    }

    private void alertOrder() {
        task = new TimerTask() {
            @Override
            public void run() {
                if (getOrderAlert() != 0) {
                    orderAlert.setVisible(true);
                } else {
                    orderAlert.setVisible(false);
                }

                if (getPendingAlert() != 0) {
                    pendingAlert.setVisible(true);
                } else {
                    pendingAlert.setVisible(false);
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    private int getOrderAlert() {
        String checkquery = "SELECT COUNt(1) as counter FROM history where status = 'Ordered' and date = '" + currentDate + "'";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkquery);

            while (queryResult.next()) {
                currentData = queryResult.getInt("counter");
            }
        } catch (Exception e) {
        }
        return currentData;
    }

    private int getPendingAlert() {
        String checkquery = "SELECT COUNt(1) as counter FROM history where status = 'Pending' and date = '" + currentDate + "'";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkquery);

            while (queryResult.next()) {
                currentData = queryResult.getInt("counter");
            }
        } catch (Exception e) {
        }
        return currentData;
    }

    private void checkAdminRole() {
        if (!admin.getCurrentAdminRole().equals("Admin")) {
            updateDrinkBox.setVisible(false);
            updateDrinkBox.managedProperty().bind(updateDrinkBox.visibleProperty());

            checkHistoryBox.setVisible(false);
            checkHistoryBox.managedProperty().bind(checkHistoryBox.visibleProperty());

            addUserBox.setVisible(false);
            addUserBox.managedProperty().bind(addUserBox.visibleProperty());

            if (admin.getCurrentAdminRole().equals("Cashier")) {
                pendingOrderBox.setDisable(true);
            } else if (admin.getCurrentAdminRole().equals("Barista")) {
                acceptOrderBox.setDisable(true);
            }
            checkRoleActive();
            
        } 
    }

    private void checkRoleActive() {
        task = new TimerTask() {
            @Override
            public void run() {
                if (!admin.getCurrentAdminRole().equals("Admin")) {
                    if (!isAvailable("Cashier")) {
                        acceptOrderBox.setDisable(false);
                    } else if (!isAvailable("Barista")) {
                        pendingOrderBox.setDisable(false);
                    }
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    private boolean isAvailable(String role) {
        String checkQuery = "SELECT count(1) FROM admin_account WHERE role = '" + role + "' and status = 'Active'";
                
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkQuery);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 0) {
                    return false;
                }
            }
        } catch (SQLException e) { 
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkAdminRole();

        currentAdminLabel.setText(admin.getCurrentAdmin());
        currentDate = LocalDate.now();
        alertOrder();
        loadScene("OverviewInterface");
    }
    
    @FXML
    private void HandleOverviewClicked(ActionEvent event) {
        loadScene("OverviewInterface");
    }

    @FXML
    private void HandleOrderClicked(ActionEvent event) {
        loadScene("ConfirmOrderInterface");
    }

    @FXML
    private void HandlePendingClicked(ActionEvent event) {
        loadScene("PendingOrderInterface");
    }

    
    @FXML
    private void HandleUpdateClicked(ActionEvent event) {
        loadScene("UpdateDrink");
    }

    @FXML
    private void HandleHistoryClicked(ActionEvent event) {
        loadScene("HistoryInterface");
    }

    @FXML
    private void HandleAddUserClicked(ActionEvent event) {
        loadScene("CreateUser");
    }

    @FXML
    private void HandleChangePasswordClicked(ActionEvent event) {
        loadScene("ChangePassword");
    }

    @FXML
    private void HandleLogOutClicked(ActionEvent event) throws IOException {
        admin.OfflineStatus();
        if (isStillAcceptedOrder) {
            String changequery = "UPDATE history SET status = 'Pending', accepted_by = NULL WHERE status = 'Accepted' and accepted_by = '" + admin.getCurrentAdmin() + "'";
            try {
                Statement statement1 = connectDb.createStatement();
                statement1.executeUpdate(changequery);
            } catch (Exception e) {
            }
        }
        window = (Stage)((Node)event.getSource()).getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../AdminLoginInterface.fxml"));
        scene = new Scene(root);
        window.setScene(scene);
        window.centerOnScreen();
        window.show();  
    }
    
    @FXML
    private void loadScene(String UI){
        QuerycheckCancelPending();
        if (isStillAcceptedOrder) {
            String changequery = "UPDATE history SET status = 'Pending', accepted_by = NULL WHERE status = 'Accepted' and accepted_by = '" + admin.getCurrentAdmin() + "'";
            try {
                Statement statement1 = connectDb.createStatement();
                statement1.executeUpdate(changequery);
            } catch (Exception e) {
            }
        }

        try {
            root = FXMLLoader.load(getClass().getResource(UI +".fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SceneBorderPane.setCenter(root);  
    }
}

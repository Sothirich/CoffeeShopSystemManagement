package Admin;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class CreateUserController implements Initializable {
    @FXML private Button CancelUpdateBtn;
    @FXML private Button ResetBtn;
    @FXML private Button SaveBtn;
    @FXML private Button addUserBtn;
    @FXML private Button clearTextBtn;
    @FXML private Button dropUserBtn;
    @FXML private Button updateUserBtn;

    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_role;
    @FXML private TableColumn<?, ?> col_username;
    @FXML private TableView<AdminTable> tableview;

    @FXML private TextField usernameTxt;
    @FXML private ChoiceBox<String> roleChoice;

    ObservableList<AdminTable> TableData;

    ObservableList<String> roleTypeList = FXCollections.observableArrayList("Cashier", "Barista", "Admin");

    Database db = new Database();
    Connection connectDb = db.connect();

    TimerTask task;
    Timer timer;

    String selectedUsername;
    int numCounter, adminCounter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copyDataFromAdminListtoTemp();
        EnableAddDisableUpdate();

        //table controller
        TableData = FXCollections.observableArrayList();
        setCellTable();
        displayDataFromTempAdminDatabase();

        roleChoice.setItems(roleTypeList);
        roleChoice.setValue("Cashier");
        checkBlankFill();
    }

    private void setCellTable() {
        col_num.setCellValueFactory(new PropertyValueFactory<>("num"));
        col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    //thread check if all textfield fill -> enable add button
    private void checkBlankFill() {
        task = new TimerTask() {
            @Override
            public void run() {
                addUserBtn.setDisable(true);
                if (!usernameTxt.getText().isBlank()) {
                    clearTextBtn.setDisable(false);
                    addUserBtn.setDisable(false);

                    if (istheSame()) {
                        addUserBtn.setDisable(true);
                    }
                } else {
                    clearTextBtn.setDisable(true);
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    //thread check whether there's any change when want to update
    private void checkUpdateChanged() {
        task = new TimerTask() {
            @Override
            public void run() {
                String checkQuery = "SELECT admin_username, role FROM temp_admin WHERE admin_username = '" + selectedUsername + "'";

                try {
                    Statement statement = connectDb.createStatement();
                    ResultSet queryResult = statement.executeQuery(checkQuery);

                    while (queryResult.next()) {
                        if (!queryResult.getString("admin_username").equals(usernameTxt.getText().trim()) || !queryResult.getString("role").equals(roleChoice.getValue())){
                            dropUserBtn.setDisable(true);
                            updateUserBtn.setDisable(false);
                        } else if (queryResult.getString("admin_username").equals(usernameTxt.getText().trim()) || queryResult.getString("role").equals(roleChoice.getValue())){
                            dropUserBtn.setDisable(false);
                            updateUserBtn.setDisable(true);
                        }
                    }
                } catch (SQLException e) {
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    private boolean istheSame() {
        String checkQuery = "SELECT count(1) FROM temp_admin WHERE admin_username = '" + usernameTxt.getText().trim() + "'";
                
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkQuery);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    return true;
                }
            }
        } catch (SQLException e) { 
        }

        return false;
    }

    private void copyDataFromAdminListtoTemp() {
        clearAdminTempHistory();

        String uploadTemp = "SELECT * FROM admin_account ORDER BY admin_username ASC";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);
            int i = 1;
            while (queryResult.next()) {
                String addData = "INSERT INTO temp_admin(Nº, admin_username, admin_password, role, status) VALUES ("
                            + i
                            + ", '" + queryResult.getString("admin_username")
                            + "', '" + queryResult.getString("admin_password")
                            + "', '" + queryResult.getString("role")
                            + "', '" + queryResult.getString("status") + "')";

                try {
                    Statement statement1 = connectDb.createStatement();
                    statement1.executeUpdate(addData);
                } catch (SQLException e) {
                }
                i++;
            }
        } catch (SQLException e) {
        }
    }

    private void displayDataFromTempAdminDatabase() {
        TableData.clear();
        String uploadTableView = "SELECT admin_username, role FROM temp_admin ORDER BY admin_username ASC";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            adminCounter = 1;
            while (queryResult.next()) { 
                TableData.add(new AdminTable( 
                    adminCounter,
                    queryResult.getString("admin_username"),
                    queryResult.getString("role"))
                );
                adminCounter++;
            }
        } catch (SQLException e) {
        }

        tableview.setItems(TableData);
    }

    private void REORDERTempAdminList() {
        String uploadTemp = "SELECT admin_username FROM temp_admin";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            int i=1;
            while (queryResult.next()) {
                String updateData = "UPDATE temp_admin SET Nº = " + i + " WHERE admin_username = '" + queryResult.getString("admin_username") + "'";
                try {
                    Statement statement1 = connectDb.createStatement();
                    statement1.executeUpdate(updateData);
                } catch (SQLException e) {
                }
                i++;
            }
        } catch (SQLException e) {
        }
    }

    private void loadDataIntoAdminDatabase() {
        clearAdminDatabase();
        String uploadTemp = "SELECT admin_username, admin_password, role, status FROM temp_admin";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            while (queryResult.next()) {
                String addData = "INSERT INTO admin_account(admin_username, admin_password, role, status) VALUES ('"
                            + queryResult.getString("admin_username") 
                            + "', '" + queryResult.getString("admin_password")
                            + "', '" + queryResult.getString("role")
                            + "', '" + queryResult.getString("status") + "')";

                try {
                    Statement statement1 = connectDb.createStatement();
                    statement1.executeUpdate(addData);
                } catch (SQLException e) {
                }
            }
        } catch (SQLException e) {
        }
    }

    private void clearAdminTempHistory() {
        String clearLog = "DELETE FROM temp_admin";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(clearLog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAdminDatabase() {
        String clearLog = "DELETE FROM admin_account";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(clearLog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DisableAddEnableUpdate() {
        addUserBtn.setVisible(false);
        clearTextBtn.setVisible(false);
        updateUserBtn.setVisible(true);
        dropUserBtn.setVisible(true);
        CancelUpdateBtn.setVisible(true);
    }

    private void EnableAddDisableUpdate() {
        addUserBtn.setVisible(true);
        clearTextBtn.setVisible(true);
        updateUserBtn.setVisible(false);
        dropUserBtn.setVisible(false);
        CancelUpdateBtn.setVisible(false);
    }

    private void clearTxt() {
        usernameTxt.setText("");
        roleChoice.setValue("Cashier");
    }

    @FXML
    private void HandleAddAdminClicked(ActionEvent event) throws IOException, SQLException {
        addUserBtn.setDisable(true);
        clearTextBtn.setDisable(true);
        
        String addDrinkQuery = "INSERT INTO temp_admin(Nº, admin_username, role) VALUES ("
                        + adminCounter
                        + ", '" + usernameTxt.getText().trim()
                        + "', '" + roleChoice.getValue() + "')";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(addDrinkQuery);
        } catch (SQLException e) {
        }

        clearTxt();
        setCellTable();
        displayDataFromTempAdminDatabase();
        ResetBtn.setDisable(false);
        SaveBtn.setDisable(false);
        
    }

    @FXML
    private void HandleClearClicked(ActionEvent event) throws IOException, SQLException {
        addUserBtn.setDisable(true);
        clearTextBtn.setDisable(true);
        clearTxt();
    }

    @FXML
    private void HandleUpdateUserClicked(ActionEvent event) throws IOException, SQLException {
        String updateQuery = "UPDATE temp_admin SET admin_username = '" + usernameTxt.getText().trim() + "', role = '" + roleChoice.getValue() + "' WHERE Nº = " + numCounter;
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
        }

        addUserBtn.setDisable(true);
        clearTextBtn.setDisable(true);
        clearTxt();
        setCellTable();
        displayDataFromTempAdminDatabase();
        EnableAddDisableUpdate();
        SaveBtn.setDisable(false);
        ResetBtn.setDisable(false);
    }

    @FXML
    private void HandleDropUserClicked(ActionEvent event) throws IOException, SQLException {
        String deleteQuery = "DELETE FROM temp_admin WHERE admin_username = '" + selectedUsername + "'";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
        }

        clearTxt();
        
        setCellTable();
        REORDERTempAdminList();
        displayDataFromTempAdminDatabase();
        EnableAddDisableUpdate();
        addUserBtn.setDisable(true);
        clearTextBtn.setDisable(true);
        SaveBtn.setDisable(false);
        ResetBtn.setDisable(false);
    }

    @FXML
    private void HandleCancelUpdate(ActionEvent event) {
        clearTxt();
        EnableAddDisableUpdate();
        tableview.getSelectionModel().clearSelection();
        addUserBtn.setDisable(true);
        clearTextBtn.setDisable(true);
    }
    
    @FXML
    private void HandleSaveClicked(ActionEvent event) {
        loadDataIntoAdminDatabase();

        clearTxt();
        setCellTable();
        displayDataFromTempAdminDatabase();
        EnableAddDisableUpdate();
        ResetBtn.setDisable(true);
        SaveBtn.setDisable(true);
    }

    @FXML 
    private void HandleResetClicked(ActionEvent event) {
        clearTxt();
        EnableAddDisableUpdate();
        SaveBtn.setDisable(true);
        ResetBtn.setDisable(true);

        copyDataFromAdminListtoTemp();
        setCellTable();
        displayDataFromTempAdminDatabase();
    }

    @FXML
    void HandleTableSelected(MouseEvent event) {
        if (!tableview.getSelectionModel().isEmpty()) {
            AdminTable row = tableview.getSelectionModel().getSelectedItem();
            numCounter = row.getNum();
            selectedUsername = row.getUsername();
            usernameTxt.setText(row.getUsername());
            roleChoice.setValue(row.getRole());

            checkUpdateChanged();
            DisableAddEnableUpdate();
        }
    }
}

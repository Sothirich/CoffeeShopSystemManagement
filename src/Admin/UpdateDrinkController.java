package Admin;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UpdateDrinkController implements Initializable {
    @FXML private TableView<DrinkTable> tableview;
    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_drink;
    @FXML private TableColumn<?, ?> col_hotType;
    @FXML private TableColumn<?, ?> col_icedType;
    @FXML private TableColumn<?, ?> col_frappeeType;
    
    @FXML private Button updateDrinkBtn;
    @FXML private Button addDrinkBtn;
    @FXML private Button clearTextBtn;
    @FXML private Button ResetBtn;
    @FXML private Button SaveBtn;
    @FXML private Button dropDrinkBtn;
    @FXML private Button CancelUpdateBtn;

    @FXML private TextField DrinkNameTxt;
    @FXML private TextField FrappeePricetxt;
    @FXML private TextField HotPricetxt;
    @FXML private TextField IcedPricetxt;
    int drinkNum;

    Database db = new Database();
    Connection connectDb = db.connect();

    TimerTask task;
    Timer timer;
    ObservableList<DrinkTable> TableDrink;
    int drinkCounter;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copyDataFromDrinkListtoTemp();
        EnableAddDisableUpdate();

        TableDrink = FXCollections.observableArrayList();

        setCellTable();
        displayDataFromTempDrinkDatabase();

        checkBlankFill();
    }

    //thread check if all textfield fill -> enable add button
    private void checkBlankFill() {
        task = new TimerTask() {
            @Override
            public void run() {
                addDrinkBtn.setDisable(true);
                if (!(DrinkNameTxt.getText().isBlank() || HotPricetxt.getText().isBlank() || IcedPricetxt.getText().isBlank() || FrappeePricetxt.getText().isBlank())) {
                    addDrinkBtn.setDisable(false);

                    if (istheSame()) {
                        addDrinkBtn.setDisable(true);
                    }
                } else if (!(DrinkNameTxt.getText().isBlank()) || !(HotPricetxt.getText().isBlank()) || !(IcedPricetxt.getText().isBlank()) || !(FrappeePricetxt.getText().isBlank())) {
                    clearTextBtn.setDisable(false);
                } else if ((DrinkNameTxt.getText().isBlank() || HotPricetxt.getText().isBlank() || IcedPricetxt.getText().isBlank() || FrappeePricetxt.getText().isBlank())){
                    clearTextBtn.setDisable(true);
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    private boolean istheSame() {
        String checkQuery = "SELECT count(1) FROM temp_drinklist WHERE drink_name = '" + DrinkNameTxt.getText().trim() + "'";
                
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

    //thread check whether there's any change when want to update
    private void checkUpdateChanged() {
        task = new TimerTask() {
            @Override
            public void run() {
                String checkQuery = "SELECT drink_name, drink_hot_price, drink_iced_price, drink_frappee_price FROM temp_drinklist WHERE drink_id =" + drinkNum;

                try {
                    Statement statement = connectDb.createStatement();
                    ResultSet queryResult = statement.executeQuery(checkQuery);

                    while (queryResult.next()) {
                        if (!queryResult.getString("drink_name").equals(DrinkNameTxt.getText().trim()) || !queryResult.getString("drink_hot_price").equals(HotPricetxt.getText().trim()) || !queryResult.getString("drink_iced_price").equals(IcedPricetxt.getText().trim()) || !queryResult.getString("drink_frappee_price").equals(FrappeePricetxt.getText().trim())){
                            dropDrinkBtn.setDisable(true);
                            updateDrinkBtn.setDisable(false);
                        } else if (queryResult.getString("drink_name").equals(DrinkNameTxt.getText().trim()) || queryResult.getString("drink_hot_price").equals(HotPricetxt.getText().trim()) || queryResult.getString("drink_iced_price").equals(IcedPricetxt.getText().trim()) || queryResult.getString("drink_frappee_price").equals(FrappeePricetxt.getText().trim())){
                            dropDrinkBtn.setDisable(false);
                            updateDrinkBtn.setDisable(true);
                        }
                    }
                } catch (SQLException e) {
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    private void setCellTable() {
        col_num.setCellValueFactory(new PropertyValueFactory<>("num"));
        col_drink.setCellValueFactory(new PropertyValueFactory<>("drinkName"));
        col_hotType.setCellValueFactory(new PropertyValueFactory<>("hotPrice"));
        col_icedType.setCellValueFactory(new PropertyValueFactory<>("icedPrice"));
        col_frappeeType.setCellValueFactory(new PropertyValueFactory<>("frappeePrice"));
    }

    private void displayDataFromTempDrinkDatabase() {
        TableDrink.clear();
        String uploadTableView = "SELECT drink_name, drink_hot_price, drink_iced_price, drink_frappee_price FROM temp_drinklist";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            drinkCounter = 1;
            while (queryResult.next()) { 
                TableDrink.add(new DrinkTable( 
                    drinkCounter,
                    queryResult.getString("drink_name"),
                    queryResult.getString("drink_hot_price"),
                    queryResult.getString("drink_iced_price"),
                    queryResult.getString("drink_frappee_price"))
                );
                drinkCounter++;
            }
        } catch (SQLException e) {
        }

        tableview.setItems(TableDrink);
    }

    private void copyDataFromDrinkListtoTemp() {
        clearDrinkTempHistory();

        String uploadTemp = "SELECT * FROM drink_list";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            while (queryResult.next()) {
                String addData = "INSERT INTO temp_drinklist(drink_id, drink_name, drink_hot_price, drink_iced_price, drink_frappee_price, drink_img) VALUES ("
                            + queryResult.getInt("drink_id") 
                            + ", '" + queryResult.getString("drink_name")
                            + "', " + queryResult.getDouble("drink_hot_price")
                            + ", " + queryResult.getDouble("drink_iced_price")
                            + ", " + queryResult.getDouble("drink_frappee_price")
                            + ", '" + queryResult.getString("drink_img") + "')";

                try {
                    Statement statement1 = connectDb.createStatement();
                    statement1.executeUpdate(addData);
                } catch (SQLException e) {
                }
            }
        } catch (SQLException e) {
        }
    }

    private void REORDERTempDrinkList() {
        String uploadTemp = "SELECT drink_name FROM temp_drinklist";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            int i=1;
            while (queryResult.next()) {
                String updateData = "UPDATE temp_drinklist SET drink_id = " + i + " WHERE drink_name = '" + queryResult.getString("drink_name") + "'";
                i++;
                try {
                    Statement statement1 = connectDb.createStatement();
                    statement1.executeUpdate(updateData);
                } catch (SQLException e) {
                }
            }
        } catch (SQLException e) {
        }
    }

    private void clearDrinkTempHistory() {
        String clearLog = "DELETE FROM temp_drinklist";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(clearLog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearDrinkList() {
        String clearLog = "DELETE FROM drink_list";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(clearLog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTxt() {
        DrinkNameTxt.setText("");
        HotPricetxt.setText("");
        IcedPricetxt.setText("");
        FrappeePricetxt.setText("");
    }

    
    private void DisableAddEnableUpdate() {
        addDrinkBtn.setVisible(false);
        clearTextBtn.setVisible(false);
        updateDrinkBtn.setVisible(true);
        dropDrinkBtn.setVisible(true);
        CancelUpdateBtn.setVisible(true);
    }

    private void EnableAddDisableUpdate() {
        addDrinkBtn.setVisible(true);
        clearTextBtn.setVisible(true);
        updateDrinkBtn.setVisible(false);
        dropDrinkBtn.setVisible(false);
        CancelUpdateBtn.setVisible(false);
    }

    private void loadDataIntoDrinkList() {
        clearDrinkList();
        String uploadTemp = "SELECT * FROM temp_drinklist";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            while (queryResult.next()) {
                String addData = "INSERT INTO drink_list(drink_id, drink_name, drink_hot_price, drink_iced_price, drink_frappee_price, drink_img) VALUES ("
                            + queryResult.getInt("drink_id") 
                            + ", '" + queryResult.getString("drink_name")
                            + "', " + queryResult.getDouble("drink_hot_price")
                            + ", " + queryResult.getDouble("drink_iced_price")
                            + "," + queryResult.getDouble("drink_frappee_price")
                            + ",'" + queryResult.getString("drink_img") + "')";

                try {
                    Statement statement1 = connectDb.createStatement();
                    statement1.executeUpdate(addData);
                } catch (SQLException e) {
                }
            }
        } catch (SQLException e) {
        }
    }

    @FXML
    private void HandleAddDrinkClicked(ActionEvent event) throws IOException, SQLException {
        String addDrinkQuery = "INSERT INTO temp_drinklist(drink_id, drink_name, drink_hot_price, drink_iced_price, drink_frappee_price) VALUES ("
                            + drinkCounter
                            + ", '" + DrinkNameTxt.getText().trim()
                            + "', " + HotPricetxt.getText().trim()
                            + ", " + IcedPricetxt.getText().trim()
                            + ", " + FrappeePricetxt.getText().trim() + ")";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(addDrinkQuery);
        } catch (SQLException e) {
        }

        clearTxt();
        setCellTable();
        displayDataFromTempDrinkDatabase();
        ResetBtn.setDisable(false);
        SaveBtn.setDisable(false);
    }

    @FXML
    private void HandleClearClicked(ActionEvent event) throws IOException, SQLException {
        addDrinkBtn.setDisable(true);
        clearTextBtn.setDisable(true);
        clearTxt();
    }

    @FXML
    private void HandleUpdateDrinkClicked(ActionEvent event) throws IOException, SQLException {
        String updateQuery = "UPDATE temp_drinklist SET drink_name = '" + DrinkNameTxt.getText().trim() +  "', drink_hot_price = " + HotPricetxt.getText().trim() + ", drink_iced_price = " + IcedPricetxt.getText().trim() + ", drink_frappee_price = " + FrappeePricetxt.getText().trim() + " WHERE drink_id = " + drinkNum;
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
        }

        clearTxt();
        setCellTable();
        displayDataFromTempDrinkDatabase();
        EnableAddDisableUpdate();
        SaveBtn.setDisable(false);
        ResetBtn.setDisable(false);
    }

    @FXML
    private void HandleDropDrinkClicked(ActionEvent event) throws IOException, SQLException {
        String deleteQuery = "DELETE FROM temp_drinklist WHERE drink_id = " + drinkNum;
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
        }

        clearTxt();
        
        setCellTable();
        REORDERTempDrinkList();
        displayDataFromTempDrinkDatabase();
        EnableAddDisableUpdate();
        SaveBtn.setDisable(false);
        ResetBtn.setDisable(false);
    }

    @FXML
    private void HandleCancelUpdate(ActionEvent event) {
        clearTxt();
        EnableAddDisableUpdate();
        tableview.getSelectionModel().clearSelection();
    }

    @FXML
    private void HandleSaveClicked(ActionEvent event) {
        loadDataIntoDrinkList();

        clearTxt();
        setCellTable();
        displayDataFromTempDrinkDatabase();
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

        copyDataFromDrinkListtoTemp();
        setCellTable();
        displayDataFromTempDrinkDatabase();
    }

    @FXML
    private void HandleTableSelected() {
        if (!tableview.getSelectionModel().isEmpty()) {
            DrinkTable row = tableview.getSelectionModel().getSelectedItem();

            drinkNum = row.getNum();
            DrinkNameTxt.setText(row.getDrinkName());
            HotPricetxt.setText(row.getHotPrice());
            IcedPricetxt.setText(row.getIcedPrice());
            FrappeePricetxt.setText(row.getFrappeePrice());

            checkUpdateChanged();
            DisableAddEnableUpdate();
        }
    }
}



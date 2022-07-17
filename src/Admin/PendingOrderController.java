package Admin;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import User.Coffee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PendingOrderController implements Initializable {
    @FXML private VBox DetailTable;
    @FXML private TableView<OrderTable> tableview;
    @FXML private TableColumn<?, ?> col_customerID;
    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_status;
    @FXML private TableColumn<?, ?> col_time;
    @FXML private TableColumn<?, ?> col_totalDrink;
    @FXML private TableColumn<?, ?> col_totalPrice;
    @FXML private TableColumn<?, ?> col_accepter;
    ObservableList<OrderTable> OrderTable;

    @FXML private TableView<OrderTable> tableview1;
    @FXML private TableColumn<?, ?> col_Dnum;
    @FXML private TableColumn<?, ?> col_DPrice;
    @FXML private TableColumn<?, ?> col_DQuantity;
    @FXML private TableColumn<?, ?> col_dDrink;
    @FXML private TableColumn<?, ?> col_DTotalPrice;
    ObservableList<OrderTable> OrderDetailTable;

    @FXML private Button acceptBtn;
    @FXML private Button cancelBtn;
    @FXML private Button doneBtn;
    @FXML private Label orderDetailLabel;

    Database db = new Database();
    Connection connectDb = db.connect();

    LocalDate currentDate;
    Integer selectedCustomerID;

    Stage window;
    Scene scene;

    boolean isAccepted;

    TimerTask task;
    Timer timer;

    String messageQuery;
    Integer index;
    int currentData, newValue, temp;

    //thread show alert when there is still ORdered
    private void alertOrder() {
        task = new TimerTask() {
            @Override
            public void run() {
                newValue = getData();

                if (newValue != temp) {
                    setCellTable();
                    displayDataFromHistory();
                    tableview.getSelectionModel().select(index-1);
                    temp = newValue;
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0l, 1000l);
    }

    private int getData() {
        String checkquery = "SELECT COUNt(1) as counter FROM history where status = 'Pending' and date = '" + currentDate.toString() + "'";
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDate = LocalDate.now();
        temp = getData();
        alertOrder();
        OrderTable = FXCollections.observableArrayList();
        DetailTable.setVisible(false);
        cancelBtn.setVisible(false);
        doneBtn.setVisible(false);
        setCellTable();
        displayDataFromHistory();
        //keepOrderUptoDate();
    }

    private void setCellTable() {
        col_num.setCellValueFactory(new PropertyValueFactory<>("num"));
        col_customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        col_totalDrink.setCellValueFactory(new PropertyValueFactory<>("totalDrink"));
        col_totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        col_time.setCellValueFactory(new PropertyValueFactory<>("time"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        col_accepter.setCellValueFactory(new PropertyValueFactory<>("accepter"));
    }

    private void displayDataFromHistory() {
        OrderTable.clear();
        String uploadTableView = "SELECT customer_id, sum(drink_quantity) AS total_drink, sum(total_price) AS total_price, time, status, accepted_by FROM history WHERE status = 'Accepted' or status = 'Pending' and date = '" + currentDate.toString() + "' GROUP BY customer_id;";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            int i = 1;
            while (queryResult.next()) { 
                OrderTable.add(new OrderTable( 
                    i,
                    queryResult.getInt("customer_id"),
                    queryResult.getInt("total_drink"),
                    Coffee.CURRENCY + queryResult.getDouble("total_price"),
                    queryResult.getTime("time"),
                    queryResult.getString("status"),
                    queryResult.getString("accepted_by"))
                );
                i++;
            }
        } catch (SQLException e) {
        }

        tableview.setItems(OrderTable);
    }

    private void setCellDetailTable() {
        col_Dnum.setCellValueFactory(new PropertyValueFactory<>("drinkNum"));
        col_dDrink.setCellValueFactory(new PropertyValueFactory<>("drinkDetail"));
        col_DPrice.setCellValueFactory(new PropertyValueFactory<>("drinkPrice"));
        col_DQuantity.setCellValueFactory(new PropertyValueFactory<>("drinkQty"));
        col_DTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }
    
    private void displayDetailDataFromHistory() {
        OrderDetailTable.clear();
        String uploadTableView = "SELECT CONCAT(drink_type, ' ', drink_name) AS drink, drink_price as price, SUM(drink_quantity) as qty, sum(total_price) as total_price FROM history WHERE customer_id = " + selectedCustomerID + " group by CONCAT(drink_type, ' ', drink_name);";
    
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            int i = 1;
            while (queryResult.next()) { 
                OrderDetailTable.add(new OrderTable( 
                    i,
                    queryResult.getString("drink"),
                    Coffee.CURRENCY + queryResult.getDouble("price"),
                    queryResult.getInt("qty"),
                    Coffee.CURRENCY + queryResult.getDouble("total_price")
                    )
                );
                i++;
            }
        } catch (SQLException e) {
        }

        tableview1.setItems(OrderDetailTable);
    }

    private void changeOrderStatus() {
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(messageQuery);
        } catch (Exception e) {
        }
    }

    private void isAccepted() {
        String checkQuery = "SELECT Count(1) as counter from history where accepted_by <> '" + admin.getCurrentAdmin() + "' and customer_id = " + selectedCustomerID;
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkQuery);

            while (queryResult.next()) {
                if (queryResult.getInt("counter") == 0){
                    isAccepted = false;
                } else {
                    isAccepted = true;
                }
            }
        } catch (Exception e) {
        }
    }

    @FXML
    private void HandleTableSelected() {
        if (!tableview.getSelectionModel().isEmpty()) {

            DetailTable.setVisible(true);
            OrderDetailTable = FXCollections.observableArrayList();

            OrderTable row = tableview.getSelectionModel().getSelectedItem();
            selectedCustomerID = row.getCustomerID();
            index = row.getNum();
            isAccepted();
            if (isAccepted) {
                acceptBtn.setVisible(false);
            } else {
                acceptBtn.setVisible(true);
            }
           
            cancelBtn.setVisible(false);
            doneBtn.setVisible(false);

            orderDetailLabel.setText("Order Details ~ Customer ID " + selectedCustomerID);

            setCellDetailTable();
            displayDetailDataFromHistory();
        }
    } 

    @FXML
    private void HandleAcceptOrderClicked (ActionEvent event) {
        messageQuery = "UPDATE history SET status = 'Accepted', accepted_by = '" + admin.getCurrentAdmin() + "' WHERE customer_id = " + selectedCustomerID;
        changeOrderStatus();

        setCellTable();
        displayDataFromHistory();
        tableview.getSelectionModel().select(index-1);
        tableview.setDisable(true);
        acceptBtn.setVisible(false);
        cancelBtn.setVisible(true);
        doneBtn.setVisible(true);
    }

    @FXML
    private void HandleDoneClicked (ActionEvent event) {
        messageQuery = "UPDATE history SET status = 'Completed' WHERE customer_id = " + selectedCustomerID;
        changeOrderStatus();

        setCellTable();
        displayDataFromHistory();

        tableview.setDisable(false);
        DetailTable.setVisible(false);
    }

    @FXML
    private void HandleCancelClicked (ActionEvent event) {
        messageQuery = "UPDATE history SET status = 'Pending', accepted_by = NULL WHERE customer_id = " + selectedCustomerID;
        changeOrderStatus();

        //Enable List 
        tableview.setDisable(false);
        setCellTable();
        displayDataFromHistory();

        DetailTable.setVisible(false);
    }

}

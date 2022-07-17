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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ConfirmOrderController implements Initializable{
    @FXML private VBox DetailTable;
    @FXML private TableView<OrderTable> tableview;
    @FXML private TableColumn<?, ?> col_customerID;
    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_status;
    @FXML private TableColumn<?, ?> col_time;
    @FXML private TableColumn<?, ?> col_totalDrink;
    @FXML private TableColumn<?, ?> col_totalPrice;
    ObservableList<OrderTable> OrderTable;

    @FXML private TableView<OrderTable> tableview1;
    @FXML private TableColumn<?, ?> col_Dnum;
    @FXML private TableColumn<?, ?> col_DPrice;
    @FXML private TableColumn<?, ?> col_DQuantity;
    @FXML private TableColumn<?, ?> col_dDrink;
    @FXML private TableColumn<?, ?> col_DTotalPrice;
    ObservableList<OrderTable> OrderDetailTable;

    @FXML private Button cancelOrderBtn;
    @FXML private Button confirmOrderBtn;

    Database db = new Database();
    Connection connectDb = db.connect();

    LocalDate currentDate;
    Integer selectedCustomerID;
    Integer index;

    TimerTask task;
    Timer timer;

    int currentData, newValue, temp;

    String messageQuery;

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
        String checkquery = "SELECT COUNt(1) as counter FROM history where status = 'Ordered' and date = '" + currentDate.toString() + "'";
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
    }

    private void displayDataFromHistory() {
        OrderTable.clear();
        String uploadTableView = "SELECT customer_id, sum(drink_quantity) AS total_drink, sum(total_price) AS total_price, time, status FROM history WHERE status = 'Ordered' and date = '" + currentDate.toString() + "' GROUP BY customer_id;";

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
                    queryResult.getString("status"))
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
        String uploadTableView = "SELECT CONCAT(drink_type, ' ', drink_name) AS drink, drink_price as price, SUM(drink_quantity) as qty, sum(total_price) as total_price FROM history WHERE customer_id = " + selectedCustomerID + " and status = 'Ordered' group by CONCAT(drink_type, ' ', drink_name);";
    
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
        setCellDetailTable();
        displayDetailDataFromHistory();
        DetailTable.setVisible(false);
        setCellTable();
        displayDataFromHistory();
    }

    @FXML
    private void HandleTableSelected() {
        if (!tableview.getSelectionModel().isEmpty()) {
            DetailTable.setVisible(true);
            OrderDetailTable = FXCollections.observableArrayList();

            OrderTable row = tableview.getSelectionModel().getSelectedItem();
            selectedCustomerID = row.getCustomerID();
            index = row.getNum();

            setCellDetailTable();
            displayDetailDataFromHistory();
        }
        
    } 

    @FXML
    private void HandleConfirmOrderClicked (ActionEvent event) {
        messageQuery = "UPDATE history SET status = 'Pending', confirmed_by = '" + admin.getCurrentAdmin() + "' WHERE customer_id = " + selectedCustomerID;
        changeOrderStatus();
    }

    @FXML
    private void HandleCancelOrderClicked (ActionEvent event) {
        messageQuery = "UPDATE history SET status = 'Canceled', confirmed_by = '" + admin.getCurrentAdmin() + "' WHERE customer_id = " + selectedCustomerID;
        changeOrderStatus();
    }
}

package Admin;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class HistoryInterfaceController implements Initializable{
    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_customerID;
    @FXML private TableColumn<?, ?> col_totalDrink;
    @FXML private TableColumn<?, ?> col_totalPrice;
    @FXML private TableColumn<?, ?> col_date;
    @FXML private TableColumn<?, ?> col_status;
    @FXML private TableColumn<?, ?> col_confirmer;
    @FXML private TableColumn<?, ?> col_accepter;
    @FXML private TableView<OrderTable> tableview;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Button resetBtn;
    ObservableList<OrderTable> HistoryTable;

    @FXML private TableView<OrderTable> tableview1;
    @FXML private TableColumn<?, ?> col_Dnum;
    @FXML private TableColumn<?, ?> col_dDrink;
    @FXML private TableColumn<?, ?> col_DQuantity;
    @FXML private TableColumn<?, ?> col_DPrice;
    @FXML private TableColumn<?, ?> col_DTotalPrice;
    @FXML private VBox DetailTable;
    ObservableList<OrderTable> HistoryDetailTable;
    
    @FXML private Label totalOrderLabel;
    @FXML private Label completedOrderLabel;
    @FXML private Label pendingOrderLabel;
    @FXML private Label canceledOrderLabel;
    @FXML private Label newOrderLabel;

    Database db = new Database();
    Connection connectDb = db.connect();

    Date minDate;
    LocalDate fromDate;    
    LocalDate toDate;

    String query;
    int getData=0;

    TimerTask task;
    Timer timer;

    
    Integer index=1;
    int currentData, newValue, temp;

    Integer selectedCustomerID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(getFirstDate().toLocalDate());
        toDatePicker.setValue(LocalDate.now());

        HistoryTable = FXCollections.observableArrayList();
        DetailTable.setVisible(false);

        setLabel();
        setCellTable();
        displayDataFromHistory();
    }

    private void setCellTable() {
        col_num.setCellValueFactory(new PropertyValueFactory<>("num"));
        col_customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        col_totalDrink.setCellValueFactory(new PropertyValueFactory<>("totalDrink"));
        col_totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        col_confirmer.setCellValueFactory(new PropertyValueFactory<>("confirmer"));
        col_accepter.setCellValueFactory(new PropertyValueFactory<>("accepter"));
    }

    private void displayDataFromHistory() {
        HistoryTable.clear();
        String uploadTableView;

        if (fromDatePicker.getValue().isBefore(toDatePicker.getValue())) {
            uploadTableView = "SELECT customer_id, sum(drink_quantity) as total_drink, sum(total_price) as total_price, date, status, confirmed_by, accepted_by FROM history WHERE (date BETWEEN '" + fromDatePicker.getValue().toString() +  "' and '" + toDatePicker.getValue().toString() + "') GROUP BY customer_id";
        } else {
            uploadTableView = "SELECT customer_id, sum(drink_quantity) as total_drink, sum(total_price) as total_price, date, status, confirmed_by, accepted_by FROM history WHERE (date BETWEEN '" + toDatePicker.getValue().toString() +  "' and '" + fromDatePicker.getValue().toString() + "') GROUP BY customer_id";
        }

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            int i = 1;
            while (queryResult.next()) { 
                HistoryTable.add(new OrderTable( 
                    i,
                    queryResult.getInt("customer_id"),
                    queryResult.getInt("total_drink"),
                    Coffee.CURRENCY + queryResult.getDouble("total_price"),
                    queryResult.getDate("date"),
                    queryResult.getString("status"),
                    queryResult.getString("confirmed_by"),
                    queryResult.getString("accepted_by"))
                );
                i++;
            }
        } catch (SQLException e) {
        }

        tableview.setItems(HistoryTable);
    }

    private void setCellDetailTable() {
        col_Dnum.setCellValueFactory(new PropertyValueFactory<>("drinkNum"));
        col_dDrink.setCellValueFactory(new PropertyValueFactory<>("drinkDetail"));
        col_DQuantity.setCellValueFactory(new PropertyValueFactory<>("drinkQty"));
        col_DPrice.setCellValueFactory(new PropertyValueFactory<>("drinkPrice"));
        col_DTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    private void displayDetailDataFromHistory() {
        HistoryDetailTable.clear();
        String uploadTableView = "SELECT CONCAT(drink_type, ' ', drink_name) AS drink, drink_price as price, SUM(drink_quantity) as qty, sum(total_price) as total_price FROM history WHERE customer_id = " + selectedCustomerID + " group by CONCAT(drink_type, ' ', drink_name)";
    
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            int i = 1;
            while (queryResult.next()) { 
                HistoryDetailTable.add(new OrderTable( 
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

        tableview1.setItems(HistoryDetailTable);
    }

    private Date getFirstDate() {
        String getDate = "SELECT MIN(date) FROM history";
    
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(getDate);

            while (queryResult.next()) { 
                minDate = queryResult.getDate(1);
            }
        } catch (SQLException e) {
        }

        return minDate;
    }

    private void setLabel() {
        if (fromDatePicker.getValue().isBefore(toDatePicker.getValue())) {
            query = "SELECT COUNT(*) FROM history WHERE (date BETWEEN '" + fromDatePicker.getValue().toString() +  "' and '" + toDatePicker.getValue().toString() + "') GROUP by customer_id";
            totalOrderLabel.setText(query()+"");
            
            query = "SELECT COUNT(*) FROM history WHERE status = 'Completed' and (date BETWEEN '" + fromDatePicker.getValue().toString() +  "' and '" + toDatePicker.getValue().toString() + "') GROUP by customer_id";
            completedOrderLabel.setText(query()+"");

            query = "SELECT COUNT(*) FROM history WHERE status = 'Pending' and (date BETWEEN '" + fromDatePicker.getValue().toString() +  "' and '" + toDatePicker.getValue().toString() + "') GROUP by customer_id";
            pendingOrderLabel.setText(query()+"");

            query = "SELECT COUNT(*) FROM history WHERE status = 'Canceled' and (date BETWEEN '" + fromDatePicker.getValue().toString() +  "' and '" + toDatePicker.getValue().toString() + "') GROUP by customer_id";
            canceledOrderLabel.setText(query()+"");

            query = "SELECT COUNT(*) FROM history WHERE status = 'Ordered' and (date BETWEEN '" + fromDatePicker.getValue().toString() +  "' and '" + toDatePicker.getValue().toString() + "') GROUP by customer_id";
            newOrderLabel.setText(query()+"");
        } else {
            query = "SELECT COUNT(*) FROM history WHERE (date BETWEEN '" + toDatePicker.getValue().toString() +  "' and '" + fromDatePicker.getValue().toString() + "') GROUP by customer_id";
            totalOrderLabel.setText(query()+"");
            
            query = "SELECT COUNT(*) FROM history WHERE status = 'Completed' and (date BETWEEN '" + toDatePicker.getValue().toString() +  "' and '" + fromDatePicker.getValue().toString() + "') GROUP by customer_id";
            completedOrderLabel.setText(query()+"");

            query = "SELECT COUNT(*) FROM history WHERE status = 'Pending' and (date BETWEEN '" + toDatePicker.getValue().toString() +  "' and '" + fromDatePicker.getValue().toString() + "') GROUP by customer_id";
            pendingOrderLabel.setText(query()+"");

            query = "SELECT COUNT(*) FROM history WHERE status = 'Canceled' and (date BETWEEN '" + toDatePicker.getValue().toString() +  "' and '" + fromDatePicker.getValue().toString() + "') GROUP by customer_id";
            canceledOrderLabel.setText(query()+"");

            query = "SELECT COUNT(*) FROM history WHERE status = 'Ordered' and (date BETWEEN '" + toDatePicker.getValue().toString() +  "' and '" + fromDatePicker.getValue().toString() + "') GROUP by customer_id";
            newOrderLabel.setText(query()+"");
        }
    }

    private int query() {
        getData = 0;
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            while (queryResult.next()) { 
                getData++;
            }
        } catch (SQLException e) {
        }

        return getData;
    }

    @FXML
    private void HandlefromDateSelect (ActionEvent event) {
        fromDate = fromDatePicker.getValue();
        setLabel();
        setCellTable();
        displayDataFromHistory();
    }

    @FXML
    private void HandletoDateSelect (ActionEvent event) {
        toDate = toDatePicker.getValue();
        setLabel();
        setCellTable();
        displayDataFromHistory();
    }

    @FXML
    private void HandleResetBtnClicked (ActionEvent event) {
        fromDatePicker.setValue(getFirstDate().toLocalDate());
        toDatePicker.setValue(LocalDate.now());
        setLabel();
        setCellTable();
        displayDataFromHistory();
        DetailTable.setVisible(false);
    }

    @FXML
    private void HandleTableSelected() {
        if (!tableview.getSelectionModel().isEmpty()) {
            OrderTable row = tableview.getSelectionModel().getSelectedItem();
            selectedCustomerID = row.getCustomerID();
            index = row.getNum();

            DetailTable.setVisible(true);
            HistoryDetailTable = FXCollections.observableArrayList();
            setCellDetailTable();
            displayDetailDataFromHistory();
        }
    }
}

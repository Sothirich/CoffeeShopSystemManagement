package User;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.stage.*;

public class ConfirmBoxController implements Initializable{
    @FXML private TableView<Table> tableview;
    @FXML private TableColumn<?, ?> col_drink;
    @FXML private TableColumn<?, ?> col_drinkType;
    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_price;
    @FXML private TableColumn<?, ?> col_quantity;
    @FXML private TableColumn<?, ?> col_totalPrice;
    @FXML private Label totalLabel;
    @FXML private Button billCancel;
    @FXML private Button billConfirm;

    ObservableList<Table> TableData;

    Stage window;
    Parent root;
    Scene scene;

    double overallTotal;
    int historyNumCounter;

    static boolean confirmOrder = false;

    Database db = new Database();
    Connection connectDb = db.connect();

    void loadScene() throws IOException{
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Coffee Shop System Management");
        window.getIcons().add(new Image(getClass().getResourceAsStream("resources/img/temp_icon.png")));
        
        root = FXMLLoader.load(getClass().getResource("ConfirmOrderBox.fxml"));
        scene = new Scene(root);
        
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
    }

    static boolean display() throws IOException {
        ConfirmBoxController load = new ConfirmBoxController();
        load.loadScene();

        return confirmOrder;
    }   

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //table controller
        TableData = FXCollections.observableArrayList();
        setCellTable();
        displayDataFromTempDatabase();
        totalLabel.setText(Coffee.CURRENCY + tempTotalPrice());
    }

    private void setCellTable() {
        col_num.setCellValueFactory(new PropertyValueFactory<>("num"));
        col_drink.setCellValueFactory(new PropertyValueFactory<>("drinkName"));
        col_drinkType.setCellValueFactory(new PropertyValueFactory<>("coffeeType"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    private void displayDataFromTempDatabase() {
        TableData.clear();
        String uploadTableView = "SELECT Nº, drink_name, drink_type, drink_price, drink_quantity, total_price FROM temp_customer";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            while (queryResult.next()) {
                TableData.add(new Table( 
                    queryResult.getInt("Nº"), 
                    queryResult.getString("drink_name"),
                    queryResult.getString("drink_type"),
                    Coffee.CURRENCY + queryResult.getString("drink_price"),
                    queryResult.getInt("drink_quantity"),
                    Coffee.CURRENCY + queryResult.getString("total_price")));
                }
        } catch (SQLException e) {
        }

        tableview.setItems(TableData);
    }

    private Double tempTotalPrice() {
        String sumTotalinCart = "SELECT SUM(total_price) AS Total FROM temp_customer";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(sumTotalinCart);

            while (queryResult.next()) {
                overallTotal = queryResult.getDouble("Total");
                }
        } catch (SQLException e) {
        }

        return overallTotal;
    }

    //funtion for getting the current num in history table
    private void CountNumHistory() throws SQLException {
        String count = "SELECT COUNT(Nº) AS LastNumber FROM history; ";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(count);

            while (queryResult.next()) {
                historyNumCounter = queryResult.getInt("LastNumber");
            }
        } catch (SQLException e) {
        }
    }

    private void loadDataIntoHistory() throws SQLException {
        String uploadTemp = "SELECT customer_id, drink_name, drink_type, drink_price, drink_quantity, total_price FROM temp_customer";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            while (queryResult.next()) {
                CountNumHistory();
                historyNumCounter++;

                String addData = "INSERT INTO history(Nº, customer_id, drink_name, drink_type, drink_price, drink_quantity, total_price) VALUES ("
                            + historyNumCounter 
                            + "," + queryResult.getInt("customer_id")
                            + ", '" + queryResult.getString("drink_name")
                            + "', '" + queryResult.getString("drink_type")
                            + "'," + queryResult.getDouble("drink_price")
                            + "," + queryResult.getInt("drink_quantity")
                            + "," + queryResult.getDouble("total_price") + ")";

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
    private void HandleBillCancelButton(ActionEvent event) throws IOException, SQLException {
        Stage stage = (Stage) billCancel.getScene().getWindow();
        confirmOrder = false;
        stage.close();
    }

    @FXML
    private void HandleBillConfirmButton(ActionEvent event) throws IOException, SQLException {
        confirmOrder = true;
        loadDataIntoHistory();
        
        Stage stage = (Stage) billConfirm.getScene().getWindow();
        stage.close();
        ThankYouController.display();
    }
}

package User;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserInterfaceController implements Initializable {
    @FXML private VBox chosenCoffeeCard;
    @FXML private Label coffeeNameLable;
    @FXML private Label coffeeHotPriceLabel;
    @FXML private Label coffeeIcedPriceLabel;
    @FXML private Label coffeeFrappeePriceLabel;
    @FXML private Label totalLabel;
    @FXML private ImageView coffeeImg;
    @FXML private ScrollPane scroll;
    @FXML private ImageView backIcon;
    @FXML private Button addToCardBtn;
    @FXML private Button purchaseBtn;
    @FXML private Button deleteBtn;
    @FXML private Button resetBtn;
    @FXML private GridPane grid;
    @FXML private ComboBox<String> coffeeTypeBox;
    @FXML private ComboBox<String> coffeeQuantityBox;

    @FXML private TableView<Table> tableview;
    @FXML private TableColumn<?, ?> col_drink;
    @FXML private TableColumn<?, ?> col_drinkType;
    @FXML private TableColumn<?, ?> col_num;
    @FXML private TableColumn<?, ?> col_price;
    @FXML private TableColumn<?, ?> col_quantity;
    @FXML private TableColumn<?, ?> col_totalPrice;

    ObservableList<Table> TableData;

    ObservableList<String> coffeeTypeList = FXCollections.observableArrayList("Hot", "Iced", "Frappee");
    ObservableList<String> coffeeQuantityList = FXCollections.observableArrayList("1", "2", "3", "4", "5");

    private List<Coffee> coffees = new ArrayList<>();
    private MyListener myListener;

    Database db = new Database();
    Connection connectDb = db.connect();

    int lastCustomerIDCounter;

    int drinkNum, numCounter;

    double price;
    double overallTotal;
    
    private List<Coffee> getData() throws SQLException {
        List<Coffee> coffees = new ArrayList<>();

        String uploadCoffee = "SELECT drink_name, drink_hot_price, drink_iced_price, drink_frappee_price, drink_img FROM drink_list";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadCoffee);
            
            while (queryResult.next()) {
                coffees.add(new Coffee( 
                    queryResult.getString("drink_name"), 
                    queryResult.getDouble("drink_hot_price"),
                    queryResult.getDouble("drink_iced_price"),
                    queryResult.getDouble("drink_frappee_price"),
                    queryResult.getString("drink_img")));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coffees;
    }

    private void setChosenCoffee(Coffee coffee) {
        coffeeNameLable.setText(coffee.getName());
        coffeeHotPriceLabel.setText(Coffee.CURRENCY + coffee.getHotPrice());
        coffeeIcedPriceLabel.setText(Coffee.CURRENCY + coffee.getIcedPrice());
        coffeeFrappeePriceLabel.setText(Coffee.CURRENCY + coffee.getFrappeePrice());
        coffeeImg.setImage(new Image(getClass().getResourceAsStream(coffee.getImgSrc())));
        chosenCoffeeCard.setStyle("-fx-background-radius: 30;");
    }

    //funtion for getting the the last customer id and last date
    private void LastCustomerID() throws SQLException {
        String count = "SELECT customer_id AS lastCustomerID FROM history ORDER BY Nº DESC LIMIT 1";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(count);

            while (queryResult.next()) {
                lastCustomerIDCounter = queryResult.getInt("lastCustomerID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        numCounter = 1;
        String uploadTableView = "SELECT drink_name, drink_type, drink_price, drink_quantity, total_price FROM temp_customer";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTableView);

            while (queryResult.next()) {
                TableData.add(new Table( 
                    numCounter, 
                    queryResult.getString("drink_name"),
                    queryResult.getString("drink_type"),
                    Coffee.CURRENCY + queryResult.getString("drink_price"),
                    queryResult.getInt("drink_quantity"),
                    Coffee.CURRENCY + queryResult.getString("total_price"))
                );
                numCounter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return overallTotal;
    }

    private void clearTempHistory() {
        String clearLog = "DELETE FROM temp_customer";
        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(clearLog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void REORDERList() {
        String uploadTemp = "SELECT drink_name FROM temp_customer";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(uploadTemp);

            int i=1;
            while (queryResult.next()) {
                String updateData = "UPDATE temp_customer SET Nº = " + i + " WHERE drink_name = '" + queryResult.getString("drink_name") + "'";
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //clear temp history list
        clearTempHistory();

        //get drink data
        try {
            coffees.addAll(getData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //upload drink to card
        if (coffees.size() > 0) {
            setChosenCoffee(coffees.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Coffee coffee) {
                    setChosenCoffee(coffee);
                    coffeeTypeBox.setValue("Hot");
                    coffeeQuantityBox.setValue("1");
                }
            };
        }

        //Combobox setting
        coffeeTypeBox.setItems(coffeeTypeList);
        coffeeTypeBox.setValue("Hot");
        coffeeQuantityBox.setItems(coffeeQuantityList);
        coffeeQuantityBox.setValue("1");
        
        int column = 0;
        int row = 1;

        //itemcontroller into grid
        try {
            for (int i = 0; i < coffees.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(coffees.get(i),myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //table controller
        TableData = FXCollections.observableArrayList();
        setCellTable();
        displayDataFromTempDatabase();
        totalLabel.setText(Coffee.CURRENCY + tempTotalPrice());
    }

    //handle when user clicked back icon
    @FXML
    private void HandleGoBackClicked() throws IOException, SQLException {
        Stage window = (Stage) backIcon.getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("../DefaultInterface.fxml"));
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }

    //handle when add to cart button clicked
    @FXML
    private void HandleAddToCardClicked(ActionEvent event) throws IOException, SQLException {
        String drinkName = coffeeNameLable.getText().trim();
        String coffeeType = coffeeTypeBox.getSelectionModel().getSelectedItem();
        String quantity = coffeeQuantityBox.getSelectionModel().getSelectedItem();

        //get drink price 
        String coffeeTypePrizeConcat = "drink_" + coffeeType.toLowerCase() + "_price";
        String getCoffeePrize = "SELECT " + coffeeTypePrizeConcat + " AS Price FROM `drink_list` WHERE drink_name = '" + drinkName + "'";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(getCoffeePrize);

            while (queryResult.next()) {
                price = queryResult.getDouble("Price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //check if the order already exist in order
        String checkQuery = "SELECT COUNT(1), drink_quantity, total_price FROM temp_customer WHERE drink_name = '" + coffeeNameLable.getText().trim() + "' and drink_type = '" + coffeeTypeBox.getSelectionModel().getSelectedItem()+ "'";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkQuery);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    String tempQty = queryResult.getString("drink_quantity");
                    Double tempTotalPrice = queryResult.getDouble("total_price");

                    String updateQuery = "UPDATE temp_customer SET drink_quantity = " + (Integer.parseInt(quantity) + Integer.parseInt(tempQty)) + ", total_price = " + (tempTotalPrice + ((Double.parseDouble(quantity)*price))) + " WHERE drink_name = '" + drinkName + "' and drink_type = '" + coffeeType + "'";
                    try {
                        Statement statement1 = connectDb.createStatement();
                        statement1.executeUpdate(updateQuery);
                    } catch (SQLException e) { 
                    }
                } else {
                    LastCustomerID();
                    lastCustomerIDCounter++;

                    String addToCart = "INSERT INTO `temp_customer`(`Nº`, `customer_id`, `drink_name`, `drink_type`,`drink_price`, `drink_quantity`, `total_price`) VALUES (" + 
                            numCounter + ", " + lastCustomerIDCounter + ", '" + drinkName + "', '" + coffeeType + "', " + price + ", "+ quantity + ", " + ((Double.parseDouble(quantity)*price)) + ")";
                    try {
                        Statement statement2 = connectDb.createStatement();
                        statement2.executeUpdate(addToCart);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (SQLException e) { 
        }

        //show list into table view
        setCellTable();
        displayDataFromTempDatabase();

        //show total
        totalLabel.setText(Coffee.CURRENCY + tempTotalPrice());

        deleteBtn.setDisable(true);

        //unlock all button back
        resetBtn.setDisable(false);
        purchaseBtn.setDisable(false);
    }

    @FXML
    private void HandleDeleteButtonClicked(ActionEvent event) throws IOException, SQLException {
        deleteBtn.setDisable(true);
        String deleteLastCardOrder = "DELETE FROM temp_customer WHERE Nº = " + drinkNum;

        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(deleteLastCardOrder);
        } catch (SQLException e) {
        }

        //show list into table view
        setCellTable();
        REORDERList();
        displayDataFromTempDatabase(); 
        
        //show total
        totalLabel.setText(Coffee.CURRENCY + tempTotalPrice());
    }

    @FXML
    private void HandleResetButtonClicked(ActionEvent event) throws IOException, SQLException {
        clearTempHistory();

        //show table
        setCellTable();
        displayDataFromTempDatabase();

        resetBtn.setDisable(true);
        purchaseBtn.setDisable(true);

        totalLabel.setText(Coffee.CURRENCY + tempTotalPrice());
    }

    @FXML
    private void HandlePurchaseButtonClicked(ActionEvent event) throws IOException, SQLException {
        Stage stage = (Stage) purchaseBtn.getScene().getWindow();
        stage.setFullScreen(false);
        
        if (ConfirmBoxController.display()) {
            stage.close();
        } else {
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void HandleTableSelected() {
        if (!tableview.getSelectionModel().isEmpty()) {
            deleteBtn.setDisable(false);
            Table row = tableview.getSelectionModel().getSelectedItem();
            drinkNum = row.getNum();
        }
    }
}

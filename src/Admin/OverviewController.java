package Admin;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

import User.Coffee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

public class OverviewController implements Initializable{
    @FXML private AreaChart<String, Number> areaChart;
    @FXML private CategoryAxis xAxis;

    @FXML private RadioButton radioDay;
    @FXML private RadioButton radioMonth;
    @FXML private RadioButton radioYear;

    @FXML private Label totalSaleLabel;
    @FXML private Label totalCustomerLabel;
    @FXML private Label totalEarnLabel;

    int date, month, year;

    Database db = new Database();
    Connection connectDb = db.connect();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getTotalData();
        radioDay.setSelected(true);
        getDataByDay();
    }

    private void getTotalData() {
        String getQuery = "SELECT sum(drink_quantity) as total_sale, COUNT(DISTINCT(customer_id)) as total_customer, SUM(total_price) as total_earn FROM history WHERE status != 'Canceled';";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(getQuery);
        
            while (queryResult.next()) {
                totalSaleLabel.setText(queryResult.getString("total_sale"));
                totalCustomerLabel.setText(queryResult.getString("total_customer"));
                totalEarnLabel.setText(Coffee.CURRENCY + queryResult.getString("total_earn"));
            }
        } catch (Exception e) {
        }
    }

    private void getDataByDay() {
        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        areaChart.getData().clear();
        areaChart.setTitle("Sales in " + LocalDate.now().getMonth());
        series1.setName("Earning Per Day");
        series.setName("Sales Per Day");
        xAxis.setLabel("Day of the Month");
        xAxis.autosize();
        date = 1;
        while (date <= LocalDate.now().getDayOfMonth() ) {
            String getQuery;

            if (date < 10) {
                getQuery = "SELECT COUNT(1) as count, SUM(total_price) as sale, sum(drink_quantity) as cups FROM history WHERE date LIKE '%-0" + date + "' and status != 'Canceled'";
            } else {
                getQuery = "SELECT COUNT(1) as count, SUM(total_price) as sale, sum(drink_quantity) as cups FROM history WHERE date LIKE '%-" + date + "' and status != 'Canceled'";
            }
            
            try {
                Statement statement = connectDb.createStatement();
                ResultSet queryResult = statement.executeQuery(getQuery);

                while (queryResult.next()) {
                    if (queryResult.getInt("count") == 0) {
                        series1.getData().add(new XYChart.Data<String, Number>(date + "", 0));
                        series.getData().add(new XYChart.Data<String, Number>(date + "", 0));
                    } else {
                        series1.getData().add(new XYChart.Data<String, Number>(date + "", queryResult.getDouble("sale")));
                        series.getData().add(new XYChart.Data<String, Number>(date + "", queryResult.getInt("cups")));
                    }
                }
            } catch (Exception e) {
            }
        
            date++;
        }
        areaChart.getData().add(series1);
        areaChart.getData().add(series);
    }

    private void getDataByMonth() {
        XYChart.Series<String, Number> series2 = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> series2_1 = new XYChart.Series<String, Number>();
        areaChart.getData().clear();
        areaChart.setTitle("Sales in " + LocalDate.now().getYear());
        series2.setName("Earning Per Month");
        series2_1.setName("Sales Per Month");
        xAxis.setLabel("Month of the Year");
        month = 1;
        while (month <= LocalDate.now().getMonthValue() ) {
            String getQuery;

            if (month < 10) {
                getQuery = "SELECT COUNT(1) as count, SUM(total_price) as sale, sum(drink_quantity) as cups FROM history WHERE date LIKE '%-0" + month + "-%' and status != 'Canceled'";
            } else {
                getQuery = "SELECT COUNT(1) as count, SUM(total_price) as sale, sum(drink_quantity) as cups FROM history WHERE date LIKE '%-" + month + "-%' and status != 'Canceled'";
            }
            
            try {
                Statement statement = connectDb.createStatement();
                ResultSet queryResult = statement.executeQuery(getQuery);

                while (queryResult.next()) {
                    if (queryResult.getInt("count") == 0) {
                        series2.getData().add(new XYChart.Data<String, Number>(Month.of(month).name(), 0));
                        series2_1.getData().add(new XYChart.Data<String, Number>(Month.of(month).name(), 0));
                    } else {
                        series2.getData().add(new XYChart.Data<String, Number>(Month.of(month).name(), queryResult.getDouble("sale")));
                        series2_1.getData().add(new XYChart.Data<String, Number>(Month.of(month).name(), queryResult.getDouble("cups")));
                    }
                }
            } catch (Exception e) {
            }
            
            month++;
            
        }
        areaChart.getData().add(series2);
        areaChart.getData().add(series2_1);
    }

    private void getDataByYear() {
        XYChart.Series<String, Number> series3 = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> series3_1 = new XYChart.Series<String, Number>();
        areaChart.getData().clear();
        areaChart.setTitle("Sales so far");
        series3.setName("Earning Per Year");
        series3_1.setName("Sales Per Year");
        xAxis.setLabel("Year");
        year = 2021;
        while (year <= LocalDate.now().getYear() ) {
            String getQuery = "SELECT COUNT(1) as count, SUM(total_price) as sale, sum(drink_quantity) as cups FROM history WHERE date LIKE '" + year + "%' and status != 'Canceled'";
            
            try {
                Statement statement = connectDb.createStatement();
                ResultSet queryResult = statement.executeQuery(getQuery);

                while (queryResult.next()) {
                    if (queryResult.getInt("count") != 0) {
                        series3.getData().add(new XYChart.Data<String, Number>(year+ "", queryResult.getDouble("sale")));
                        series3_1.getData().add(new XYChart.Data<String, Number>(year+ "", queryResult.getDouble("cups")));
                    }
                }
            } catch (Exception e) {
            }
            year++;
        }
        areaChart.getData().add(series3);
        areaChart.getData().add(series3_1);
    }

    @FXML
    private void HandleSortByDay (ActionEvent event) {
        radioDay.setSelected(true);
        radioMonth.setSelected(false);
        radioYear.setSelected(false);
        getDataByDay();
    }

    @FXML
    private void HandleSortByMonth (ActionEvent event) {
        radioDay.setSelected(false);
        radioMonth.setSelected(true);
        radioYear.setSelected(false);
        getDataByMonth();
    }

    @FXML
    private void HandleSortByYear (ActionEvent event) {
        radioDay.setSelected(false);
        radioMonth.setSelected(false);
        radioYear.setSelected(true);
        getDataByYear();
    }
}

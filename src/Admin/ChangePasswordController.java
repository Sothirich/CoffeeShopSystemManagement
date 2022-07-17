package Admin;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ChangePasswordController implements Initializable {
    @FXML private PasswordField currentPasswordField;
    @FXML private TextField currentPasswordTxtField;
    @FXML private ImageView showCurrentPassword;
    
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordTxtField;
    @FXML private ImageView showNewPassword;
    
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTxtField;
    @FXML private ImageView showConfirmPassword;

    @FXML private Button changePasswordBtn;
    @FXML private Button confirmPasswordBtn;
    @FXML private Label warningLabel;
    @FXML private HBox currentPasswordHBox;
    @FXML private HBox ChangeBtnHBox;
    @FXML private HBox newPasswordHBox;
    @FXML private HBox confirmHBox;
    @FXML private HBox ConfirmBtnHBox;


    TimerTask task;
    Timer timer;

    Database db = new Database();
    Connection connectDb = db.connect();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentPasswordField.textProperty().bindBidirectional(currentPasswordTxtField.textProperty());
        newPasswordField.textProperty().bindBidirectional(newPasswordTxtField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(confirmPasswordTxtField.textProperty());

        newPasswordHBox.setVisible(false);
        confirmHBox.setVisible(false);
        ConfirmBtnHBox.setVisible(false);
    }

    //Current Password 
    @FXML
    private void currentPasswordSetOnMousePressed (MouseEvent event) {
        currentPasswordTxtField.toFront();
        showCurrentPassword.toFront();
    }
    @FXML 
    private void currentPasswordSetOnMouseReleased (MouseEvent event) {
        currentPasswordField.toFront();
        showCurrentPassword.toFront();
    }

    //New Password
    @FXML
    private void newPasswordSetOnMousePressed (MouseEvent event) {
        newPasswordTxtField.toFront();
        showNewPassword.toFront();
    }
    @FXML 
    private void newPasswordSetOnMouseReleased (MouseEvent event) {
        newPasswordField.toFront();
        showNewPassword.toFront();
    }

    //Current Password
    @FXML
    private void confirmPasswordSetOnMousePressed (MouseEvent event) {
        confirmPasswordTxtField.toFront();
        showConfirmPassword.toFront();
    }
    @FXML 
    private void confirmPasswordSetOnMouseReleased (MouseEvent event) {
        confirmPasswordField.toFront();
        showConfirmPassword.toFront();
    }

    @FXML
    private void HandleChangePasswordClicked (ActionEvent event) {
        String checkQuery = "SELECT admin_password FROM admin_account WHERE admin_username = '" + admin.getCurrentAdmin() + "'";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(checkQuery);

            while (queryResult.next()) {
                if (queryResult.getString("admin_password").equals(currentPasswordField.getText())) {
                    warningLabel.setText("");

                    currentPasswordHBox.setVisible(false);
                    currentPasswordHBox.managedProperty().bind(currentPasswordHBox.visibleProperty());

                    ChangeBtnHBox.setVisible(false);
                    ChangeBtnHBox.managedProperty().bind(ChangeBtnHBox.visibleProperty());
                    
                    newPasswordHBox.setVisible(true);
                    confirmHBox.setVisible(true);
                    ConfirmBtnHBox.setVisible(true);
                    
                } else {
                    warningLabel.setText("🚫 Your Password Is incorrect!");
                    currentPasswordField.setText("");
                }
            }
        } catch (SQLException e) {
        }
    }

    @FXML
    private void HandleConfirmPasswordClicked (ActionEvent event) {
        if (newPasswordField.getText().equals(confirmPasswordField.getText())) {
            String updateQuery = "UPDATE admin_account SET admin_password = '" + confirmPasswordField.getText() + "' WHERE admin_username = '" + admin.getCurrentAdmin() + "'";
            try {
                Statement statement = connectDb.createStatement();
                statement.executeUpdate(updateQuery);

                warningLabel.setText("✔ Changed Password Successfully!");
                currentPasswordField.setText("");
                currentPasswordHBox.setVisible(true);
                ChangeBtnHBox.setVisible(true);

                newPasswordHBox.setVisible(false);
                confirmHBox.setVisible(false);
                ConfirmBtnHBox.setVisible(false);
            } catch (SQLException e) {
            }
        } else {
            warningLabel.setText("🚫 Passsword not match!");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }
    }
}

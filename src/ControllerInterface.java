import java.io.IOException;
import java.sql.*;

import Admin.admin;
import User.Database;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Stage;

public class ControllerInterface {
    @FXML private TextField adminUsernameTextField;
    @FXML private PasswordField adminPasswordField;
    @FXML private Button loginButton;
    @FXML private Button OrderButton;
    @FXML private Label invalidLoginLabel;
    @FXML private ImageView backButton;
    @FXML private ImageView adminIcon;

    Stage window;
    Scene scene;
    Parent root;

    Boolean accIsTrue;
    Boolean isFullscreen;

    Database db = new Database();
    Connection connectDb = db.connect();

    @FXML
    private void HandleAdminLoginClicked() throws IOException {    
        if(adminUsernameTextField.getText().isBlank() == true || adminPasswordField.getText().isBlank() == true){
            invalidLoginLabel.setText("Please enter username/password.");
        } else {
            if (validLogin()) {
                window = (Stage) loginButton.getScene().getWindow();
                isFullscreen = true;
                loadInterface("Admin/AdminInterface");
            }
        }
    }

    @FXML
    private void HandleGoBackClicked() throws IOException {    
        window = (Stage) backButton.getScene().getWindow();
        isFullscreen = false;
        loadInterface("DefaultInterface");
    }

    @FXML
    private void HandleAdminIconClicked() throws IOException {    
        window = (Stage) adminIcon.getScene().getWindow();
        isFullscreen = false;
        loadInterface("AdminLoginInterface");
    }

    @FXML
    private void HandleOrderButtonClicked() throws IOException {    
        window = (Stage) OrderButton.getScene().getWindow();
        isFullscreen = true;
        loadInterface("User/UserInterface");
    }

    @FXML
    private void loadInterface(String UI) {
        try {
            root = FXMLLoader.load(getClass().getResource(UI +  ".fxml"));
            scene = new Scene(root);
            window.setScene(scene);

            window.setResizable(false);
            window.centerOnScreen();
            window.show();

            if (isFullscreen) {
                window.setFullScreenExitHint("");
                window.setFullScreen(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private boolean validLogin() {
        String verifyLogin = "SELECT COUNT(1), admin_username, role FROM admin_account WHERE admin_username = '" + adminUsernameTextField.getText().trim() + "' AND admin_password = BINARY '" + adminPasswordField.getText() + "'";
        try {
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1){
                    admin.setCurrentAdmin(queryResult.getString("admin_username"));
                    admin.setCurrentAdminRole(queryResult.getString("role"));
                    admin.ActiveStatus();
                    accIsTrue = true;
                } else {
                    invalidLoginLabel.setText("Invalid Login. Please try again.");
                    adminUsernameTextField.setText("");
                    adminPasswordField.setText("");
                    accIsTrue = false;
                }
            }
        } catch (Exception e) {
            invalidLoginLabel.setText("Failed to connect to the SERVER!");
        }

        return accIsTrue;
    }
}

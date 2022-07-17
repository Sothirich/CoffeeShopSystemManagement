package User;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ThankYouController {
    Stage window;
    Parent root;
    Scene scene;

    void loadScene() throws IOException{
        loadInterface("ThankYou");
        PauseTransition wait = new PauseTransition(Duration.seconds(1));
        wait.setOnFinished((e) -> {
            window.close();
            loadInterface("../DefaultInterface");
        });
        wait.play();
    }

    static void display() throws IOException {
        ThankYouController load = new ThankYouController();
        load.loadScene();
    }  

    @FXML
    private void loadInterface(String UI) {
        window = new Stage();
        window.setTitle("Coffee Shop System Management");
        window.getIcons().add(new Image(getClass().getResourceAsStream("resources/img/temp_icon.png")));
        
        try {
            root = FXMLLoader.load(getClass().getResource(UI +  ".fxml"));
            scene = new Scene(root);
            window.setScene(scene);
            window.setResizable(false);
            window.centerOnScreen();
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

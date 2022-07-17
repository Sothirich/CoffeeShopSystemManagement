package User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;

public class ItemController {
    @FXML private Label nameLabel;
    @FXML private ImageView img;

    @FXML
    private void click(MouseEvent mouseEvent) {
        myListener.onClickListener(coffee);
    }

    private Coffee coffee;
    private MyListener myListener;

    public void setData(Coffee coffee, MyListener myListener) {
        this.coffee = coffee;
        this.myListener = myListener;
        nameLabel.setText(coffee.getName());
        Image image = new Image(getClass().getResourceAsStream(coffee.getImgSrc()));
        img.setImage(image);
    }
}

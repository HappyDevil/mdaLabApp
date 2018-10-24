package views;

import controllers.MicrowaveController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import utils.ResourseUtils;


public class MicrowaveView {

    private Scene scene;
    private ImageView microwaveState;
    private Label timeToCook;

    public MicrowaveView() {
    }

    public Scene getScene() {
        if (scene == null) {
            scene = getInitScene();
        }
        return scene;
    }

    private Scene getInitScene() {
        microwaveState = new ImageView();
        timeToCook = new Label("");
        VBox vBox = new VBox(microwaveState, timeToCook);
        vBox.setSpacing(5.0);
        scene = new Scene(vBox, 500, 380);
        scene.setFill(Color.WHITE);
        return scene;
    }

    public void microwaveToCooking() {
        microwaveState.setImage(ResourseUtils.getMicrowaveCooking());
    }

    public void microwaveToEmpty() {
        microwaveState.setImage(ResourseUtils.getMicrowaveEmpty());
    }

    public void microwaveToReadyToCook() {
        microwaveState.setImage(ResourseUtils.getMicrowaveToReadyToCook());
    }

    public void microwaveToInterrupted() {
        microwaveState.setImage(ResourseUtils.getMicrowaveInterrupted());
    }

    public void microwaveToCookingComplete() {
        microwaveState.setImage(ResourseUtils.getMicrowaveToCookingComplete());
    }


    public void setMouseEvent(EventHandler<MouseEvent> mouseEventEventHandler) {
        scene.setOnMouseClicked(mouseEventEventHandler);
    }

    public void setTimeToCook(Integer timeToCook) {
        Platform.runLater(() -> {
            if(timeToCook>0)
                this.timeToCook.setText(timeToCook.toString());
            else this.timeToCook.setText("");
        });
    }
}

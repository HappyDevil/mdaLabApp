package views;

import controllers.MicrowaveController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.ResourseUtils;


public class MicrowaveView {

    private Scene scene;
    private ImageView microwaveState;
    private Label timeToCook;
    private Label logStateLabel;

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
        timeToCook = new Label("00:00");
        logStateLabel = new Label("");
        timeToCook.setTextFill(Color.WHITE);
        timeToCook.setFont(new Font("Arial", 24));
        logStateLabel.setFont(new Font("Arial", 14));
        timeToCook.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0.0), new Insets(0))));
        Pane stackPane = new Pane(microwaveState, timeToCook,logStateLabel);
        timeToCook.setLayoutX(410.0);
        timeToCook.setLayoutY(80.0);
        logStateLabel.setLayoutX(34);
        logStateLabel.setLayoutY(342);
        scene = new Scene(stackPane, 500, 380);
        scene.setFill(Color.WHITE);
        return scene;
    }

    public void microwaveToCooking() {
        changeImageTo(ResourseUtils.getMicrowaveCooking());
    }

    public void microwaveToEmpty() {
        changeImageTo(ResourseUtils.getMicrowaveEmpty());
    }

    public void microwaveToReadyToCook() {
        changeImageTo(ResourseUtils.getMicrowaveToReadyToCook());
    }

    private void changeImageTo(Image microwaveToReadyToCook) {
        if(microwaveState.getImage()!=microwaveToReadyToCook)
            microwaveState.setImage(microwaveToReadyToCook);
    }

    public void microwaveToInterrupted() {
        changeImageTo(ResourseUtils.getMicrowaveInterrupted());
    }

    public void microwaveToCookingComplete() {
        changeImageTo(ResourseUtils.getMicrowaveToCookingComplete());
    }


    public void setMouseEvent(EventHandler<MouseEvent> mouseEventEventHandler) {
        scene.setOnMouseClicked(mouseEventEventHandler);
    }

    public void setTimeToCook(String timeToCook) {
        this.timeToCook.setText(timeToCook);
    }

    public void setState(String state) {
        this.logStateLabel.setText(state);
    }
}

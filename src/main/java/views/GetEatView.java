package views;

import entity.Food;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.function.Consumer;

public class GetEatView {
    private Dialog<Food> dialog;

    public GetEatView() {

        dialog = new Dialog<>();
        dialog.setTitle("Диалог с микроволновкой");
        dialog.setHeaderText("Какую еду вы хотите приготовить?");

// Set the button types.
        ButtonType loginButtonType = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Название");

        TextField time = new TextField();
        time.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                String value = newValue.replaceAll("[^\\d]", "");
                time.setText(value);
            }
        });
        title.setPromptText("Время готовки");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Время готовки:"), 0, 1);
        grid.add(time, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        time.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(title::requestFocus);

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                int timeToCook = Integer.parseInt(time.getText());
                if(timeToCook>300){
                    timeToCook = timeToCook / 300;
                    timeToCook = timeToCook * 300;
                }
                return new Food(timeToCook,title.getText());
            }
            else dialog.close();
            return null;
        });


    }

    public void showAndWait(Consumer<Food> foodConsumer) {
        Optional<Food> result = dialog.showAndWait();
        result.ifPresent(foodConsumer);
    }
}

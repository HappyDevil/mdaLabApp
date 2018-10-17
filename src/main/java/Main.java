import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL resource = getClass().getResource("sample.fxml");
        if(resource!=null) {
            Parent root = FXMLLoader.load(resource);
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root, 300, 275));
            primaryStage.show();
        } else{
            primaryStage.setTitle("Tytulik");

            Button btn = new Button();
            btn.setText("click");


            StackPane layout = new StackPane();
            layout.getChildren().add(btn);

            Scene scene = new Scene(layout, 600, 300);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

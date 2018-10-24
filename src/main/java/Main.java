import controllers.MicrowaveController;
import javafx.application.Application;
import javafx.stage.Stage;
import stateContexts.MicrowaveContext;
import views.MicrowaveView;

public class Main extends Application {

    static MicrowaveView microwaveView;
    static MicrowaveController microwaveController;


    @Override
    public void start(Stage primaryStage) {
        microwaveView = new MicrowaveView();
        primaryStage.setScene(microwaveView.getScene());
        microwaveController = new MicrowaveController(new MicrowaveContext(),microwaveView);
        primaryStage.show();
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        microwaveController.stopApplication();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

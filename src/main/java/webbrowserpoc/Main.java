package webbrowserpoc;

import com.gluonhq.ignite.guice.GuiceContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.Arrays;

public class Main extends Application {
    private GuiceContext context = new GuiceContext(this, () -> Arrays.asList(new WebBrowserPocModule()));

    @Inject
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        context.init();

        fxmlLoader.setLocation(getClass().getClassLoader().getResource("view/WebBrowsers.fxml"));

        Parent root = fxmlLoader.load();
        Scene rootScene = new Scene(root);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(rootScene);
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import login.CustomerLoginController;
import scramble.ScrambleController;

import java.net.URL;

public class CustomerApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/main/customerMain.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane root = fxmlLoader.load(url.openStream());

        CustomerMainController customerMainController = fxmlLoader.getController();
        customerMainController.setPrimaryStage(primaryStage);


        Scene scene = new Scene(root);

        primaryStage.setTitle("Customer App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

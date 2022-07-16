package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.net.URL;

public class AdminApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //load main controller and component (it also loads header)
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/main/adminMain.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane root = fxmlLoader.load(url.openStream());

        AdminMainController adminAppController = fxmlLoader.getController();
        adminAppController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

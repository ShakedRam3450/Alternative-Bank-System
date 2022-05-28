package main;

import bank.Bank;
import bank.BankImpl;
import body.admin.AdminBodyController;
import body.customer.CustomerBodyController;
import header.HeaderController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import resources.Resources;

import java.net.URL;

public class BankApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //load main controller and component (it also loads header)
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(Resources.MAIN_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        ScrollPane root = fxmlLoader.load(url.openStream());

        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(primaryStage);
        mainController.setMainComponent((BorderPane) root.getContent());

        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("main.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

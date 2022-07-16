package main;

import dashboard.CustomerDashboardController;
import dto.CustomerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import login.CustomerLoginController;


import java.io.IOException;
import java.net.URL;

public class CustomerMainController {

    private Parent loginComponent;
    private CustomerLoginController loginComponentController;
    private Parent dashboardComponent;
    private CustomerDashboardController dashboardComponentController;

    @FXML private BorderPane mainComponent;
    private Stage primaryStage;

    @FXML
    public void initialize() throws IOException {
        loadLogin();
        loadDashboard();
        mainComponent.setCenter(loginComponent);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void loadDashboard() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/dashboard/customerDashboard.fxml");
        fxmlLoader.setLocation(url);
        this.dashboardComponent = fxmlLoader.load(url.openStream());
        this.dashboardComponentController = fxmlLoader.getController();
        this.dashboardComponentController.setCustomerMainController(this);
    }

    private void loadLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/login/customerLoginPage.fxml");
        fxmlLoader.setLocation(url);
        this.loginComponent = fxmlLoader.load(url.openStream());
        this.loginComponentController = fxmlLoader.getController();
        this.loginComponentController.setCustomerMainController(this);
    }

    public void switchToDashboard(CustomerDTO customer) {
        mainComponent.setCenter(dashboardComponent);
        dashboardComponentController.setUsername(customer.getName());
        dashboardComponentController.setCustomer(customer);
        dashboardComponentController.startRefresher();
    }

}

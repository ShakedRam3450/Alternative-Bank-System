package body.customer;

import body.customer.info.InfoController;
import body.customer.payment.PaymentController;
import body.customer.scramble.ScrambleController;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.NotificationDTO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import loan.Loan;
import main.MainController;
import resources.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class CustomerBodyController {
    private MainController mainController;

    @FXML private InfoController infoComponentController;
    @FXML private Parent infoComponent;
    @FXML private ScrambleController scrambleComponentController;
    @FXML private Parent scrambleComponent;
    @FXML private PaymentController paymentComponentController;
    @FXML private Parent paymentComponent;

    @FXML private Label balanceLabel;

    private CustomerDTO customer;
    private Map<String, List<LoanDTO>> notifications;
    public CustomerBodyController() {
        notifications = new HashMap<>();
    }

    @FXML
    public void initialize() {
        if(infoComponentController != null && scrambleComponentController != null && paymentComponentController != null) {
            infoComponentController.setCustomerBodyController(this);
            scrambleComponentController.setCustomerBodyController(this);
            paymentComponentController.setCustomerBodyController(this);

        }

    }
    public void setCustomer(CustomerDTO customer){
        this.customer = customer;
        infoComponentController.setCustomer(customer);
        paymentComponentController.setLoansTable();
        this.balanceLabel.setText(((Double)customer.getBalance()).toString());
        paymentComponentController.setNotificationsTable(mainController.getCustomerNotifications(customer.getName()));
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void charge(double amount){
        mainController.charge(customer, amount);

    }

    public void withdraw(double amount){
        if(customer.getBalance() < amount)
            System.out.println("amount is bigger than balance!");
        else
            mainController.withdraw(customer, amount);
    }

    public Collection<String> getCategories(){
        return mainController.getCategories();
    }

    public void setCategories(Set<String> categories) {
        scrambleComponentController.setCategories(categories);
    }

    public void scramble(int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories) {
        mainController.scramble(customer, amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);
    }

    public void displayEligibleLoans(List<LoanDTO> eligibleLoans) {
        scrambleComponentController.displayEligibleLoans(eligibleLoans);
    }

    public void placementActivation(List<LoanDTO> selectedLoans, int amount, int maxOwnership) {
        mainController.placementActivation(customer.getName(), selectedLoans, amount, maxOwnership);
    }

    public int getTime() {
        return mainController.getTime();
    }
}
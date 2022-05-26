package main;

import bank.Bank;
import bank.BankImpl;
import body.admin.AdminBodyController;
import body.customer.CustomerBodyController;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.NotificationDTO;
import exceptions.*;
import header.HeaderController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import resources.Resources;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController {

    @FXML private Parent headerComponent;
    @FXML private HeaderController headerComponentController;

    private Parent adminBodyComponent;
    private AdminBodyController adminBodyComponentController;
    private Parent customerBodyComponent;
    private CustomerBodyController customerBodyComponentController;

    @FXML private BorderPane mainComponent;

    private SimpleBooleanProperty isFileExist;
    private SimpleIntegerProperty time;

    private Bank bank;
    private Stage primaryStage;
    private Map<String, List<NotificationDTO>> customersNotifications ;// key = customer name

    public MainController(){
        bank = new BankImpl();
        isFileExist = new SimpleBooleanProperty();
        time = new SimpleIntegerProperty(1);
        customersNotifications = new HashMap<>();

    }
    @FXML
    public void initialize() throws IOException {
        if (headerComponentController != null){
            headerComponentController.setMainController(this);
            headerComponentController.getTimeLabel().textProperty().bind(time.asString());
        }
        isFileExist.set(false);
        loadAdmin();
        loadCustomer();
        mainComponent.setCenter(adminBodyComponent);
    }
    private void loadAdmin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(Resources.ADMIN_BODY_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        this.adminBodyComponent = fxmlLoader.load(url.openStream());
        this.adminBodyComponentController= fxmlLoader.getController();
        this.adminBodyComponentController.setMainController(this);
    }
    private void loadCustomer() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(Resources.CUSTOMER_BODY_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        this.customerBodyComponent = fxmlLoader.load(url.openStream());
        this.customerBodyComponentController= fxmlLoader.getController();
        this.customerBodyComponentController.setMainController(this);
    }
    public void setMainComponent(BorderPane mainComponent) {
        this.mainComponent = mainComponent;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public boolean isIsFileExist() {
        return isFileExist.get();
    }
    public void increaseYaz(){
       if (isIsFileExist()) {
           Map<String, LoanDTO> needToPayLoans = bank.timeAdvancement();
           time.set(bank.getTime());
           adminBodyComponentController.displayInfo(bank.getLoans(), bank.getCustomers());
           addNotifications(needToPayLoans);
       }
    }

    private void addNotifications(Map<String, LoanDTO> needToPayLoans) {
        needToPayLoans.forEach((loanId ,loanDTO) -> customersNotifications.get(loanDTO.getOwnerName()).add(new NotificationDTO(loanDTO, time.get())));
    }
    public List<NotificationDTO> getCustomerNotifications(String name) {
        return this.customersNotifications.get(name);
    }
    public void changeUser(String userName) throws IOException {
        if (userName != null) {
            if (userName.equals("Admin")) {
                mainComponent.setCenter(adminBodyComponent);
            } else {
                mainComponent.setCenter(customerBodyComponent);
                customerBodyComponentController.setCustomer(bank.getCustomers().get(userName));
            }
        }
    }
    public void charge(CustomerDTO customer, double amount){
       bank.deposit(customer.getName(), amount);
       customerBodyComponentController.setCustomer(bank.getCustomers().get(customer.getName()));
       adminBodyComponentController.displayInfo(bank.getLoans(), bank.getCustomers());
    }
    public void withdraw(CustomerDTO customer,  double amount){
       bank.withdrawal(customer.getName(), amount);
       customerBodyComponentController.setCustomer(bank.getCustomers().get(customer.getName()));
       adminBodyComponentController.displayInfo(bank.getLoans(), bank.getCustomers());
    }
    public void readFile(File file, String path){
        try {
            bank.readFile(file);
            time.set(bank.getTime());
            adminBodyComponentController.displayInfo(bank.getLoans(), bank.getCustomers());
            headerComponentController.displayFilePath(path);
            headerComponentController.initCustomers(bank.getCustomers());
            customerBodyComponentController.setCategories(bank.getCategories());
            bank.getCustomers().forEach((k,v) -> customersNotifications.put(k, new ArrayList<>()));
            isFileExist.set(true);
        } catch (Exception e) {
            showPopUpError(getErrorMassage(e));
        }

    }
    public String getErrorMassage(Exception e){
        Class <?> exceptionType = e.getClass();

        if(exceptionType == FileNotFoundException.class)
            return "File does not exist or a directory!";

        else if(exceptionType == JAXBException.class)
            return "JAXB Exception!";

        else if(exceptionType == FileNotXMLException.class)
            return "File is not XML file!";

        else if(exceptionType == NoSuchCategoryException.class) {
            NoSuchCategoryException exception = (NoSuchCategoryException)e;
            return "There is a loan with invalid category!" + "\n" +
            "The invalid category: " + exception.getInvalidCategory() + "\n" +
            "The allowed categories are: " + exception.getCategories();
        }

        else if(exceptionType == NoSuchCustomerException.class){
            NoSuchCustomerException exception = (NoSuchCustomerException)e;
            return "There is a customer in a loan that does not exists!" + "\n" +
            "The name of the customer: " + exception.getInvalidCustomer() + "\n" +
            "The customers that exist: " + exception.getCustomersNames();
        }

        else if(exceptionType == PaymentMarginException.class){
            PaymentMarginException exception = (PaymentMarginException)e;
            return "There is an issue with loan margin payment!" + "\n" +
            "the total time is " + exception.getTotalYazTime() + " but the margin is " + exception.getPaysEveryYaz();
        }

        else if(exceptionType == SameCustomerNameException.class) {
            SameCustomerNameException exception = (SameCustomerNameException) e;
            return "There are customers with the same name!" + "\n" +
            "The name that appears more that once is: " + exception.getName();
        }

        else if(exceptionType == SameLoanIdException.class){
            SameLoanIdException exception = (SameLoanIdException) e;
            return "There are loans with the same id!" + "\n" +
            "The id that appears more that once is: " + exception.getId();
        }

        else if (exceptionType == NegativeBalanceException.class){
            NegativeBalanceException exception = (NegativeBalanceException) e;
            return "Customer: " + exception.getName() + " has negative balance of: " + exception.getBalance();
        }

        else if (exceptionType == NegativeCapitalException.class){
            NegativeCapitalException exception = (NegativeCapitalException) e;
            System.out.println("Loan id: " + exception.getLoanId() + " has negative capital of: " + exception.getCapital());
        }

        else if (exceptionType == NegativePaysEveryYazException.class){
            NegativePaysEveryYazException exception = (NegativePaysEveryYazException) e;
            return "Loan id: " + exception.getLoanId() + " has negative PaysEveryYaz of: " + exception.getPaysEveryYaz();
        }

        else if (exceptionType == NegativeIntristPerPaymentException.class){
            NegativeIntristPerPaymentException exception = (NegativeIntristPerPaymentException) e;
            return "Loan id: " + exception.getLoanId() + " has negative IntristPerPayment of: " + exception.getIntristPerPayment();
        }

        else if (exceptionType == NegativeTotalYazTimeException.class){
            NegativeTotalYazTimeException exception = (NegativeTotalYazTimeException) e;
            return "Loan id: " + exception.getLoanId() + " has negative TotalYazTime of: " + exception.getTotalYazTime();
        }
        return null;
    }
    public void showPopUpError(String errorMsg){
        Stage popUpWindow = new Stage();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Error popup - opening file");

        Label label1= new Label(errorMsg);
        Button button1= new Button("Close");

        button1.setOnAction(e -> popUpWindow.close());
        ScrollPane layout = new ScrollPane();
        VBox vbox= new VBox(20);
        vbox.getChildren().addAll(label1, button1);
        vbox.setAlignment(Pos.CENTER);
        layout.setContent(vbox);
        Scene scene1= new Scene(layout);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }
    public SimpleIntegerProperty getTimeProperty() {
        return time;
    }
    public Collection<String> getCategories(){
        return bank.getCategories();
    }
    public void scramble(CustomerDTO customer, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories) {
        List<LoanDTO> eligibleLoans = bank.getEligibleLoans(customer, amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);
        customerBodyComponentController.displayEligibleLoans(eligibleLoans);
   }
    public Collection<CustomerDTO> getCustomers(){
       return bank.getCustomers().values();
    }
    public void placementActivation(String name, List<LoanDTO> selectedLoans, int amount, int maxOwnership) {
        bank.placementActivation(name, selectedLoans, amount, maxOwnership);
        customerBodyComponentController.setCustomer(bank.getCustomers().get(name));
        adminBodyComponentController.displayInfo(bank.getLoans(), bank.getCustomers());
    }
    public int getTime() {
        return time.get();
    }
}

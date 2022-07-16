package body;

import dto.CustomerDTO;
import dto.LoanDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import loans.LoansController;
import main.AdminMainController;
import org.controlsfx.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminBodyController {

    private LoansController loansComponentController;
    private MasterDetailPane loansComponent;
    @FXML private Label adminNameLabel;
    @FXML private Label timeLabel;
    private SimpleIntegerProperty time;
    @FXML private Button increaseYazBTN;
    @FXML private Label viewByLabel;
    @FXML private TableView<CustomerDTO> customersTable;
    @FXML private VBox tablesVBox;
    TableColumn<CustomerDTO,String> nameCol;
    TableColumn<CustomerDTO,Double> balanceCol;
    TableColumn<CustomerDTO,String> numOfBorrowerLoansCol;
    TableColumn<CustomerDTO,Integer> numOfLenderLoansCol;

    private AdminMainController adminMainController;

    private TimerTask infoRefresher;
    private Timer timer;

    public AdminBodyController(){
       time = new SimpleIntegerProperty(1);
    }

    @FXML
    public void initialize() throws IOException {
        loadLoans();
        tablesVBox.getChildren().add(loansComponent);

        timeLabel.textProperty().bind(time.asString());
        viewByLabel.setText("Admin");
        customersTable.setEditable(true);

        nameCol = new TableColumn<>("name");
        balanceCol = new TableColumn<>("balance");
        numOfBorrowerLoansCol = new TableColumn<>("numOfBorrowerLoans");
        numOfLenderLoansCol = new TableColumn<>("numOfLenderLoans");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        numOfBorrowerLoansCol.setCellValueFactory(new PropertyValueFactory<>("numOfBorrowerLoans"));
        numOfLenderLoansCol.setCellValueFactory(new PropertyValueFactory<>("numOfLenderLoans"));
    }

    private void loadLoans() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/loans/loansComponent.fxml");
        fxmlLoader.setLocation(url);
        this.loansComponent = fxmlLoader.load(url.openStream());
        this.loansComponentController = fxmlLoader.getController();
        //this.loansComponentController.setCustomerMainController(this);
    }

    public void setTime(int time) {
        this.time.set(time);
    }

    @FXML
    void increaseYaz(ActionEvent event) {
        adminMainController.increaseYaz();
    }

    public void setAdminMainController(AdminMainController adminMainController) {
        this.adminMainController = adminMainController;
    }

    public void displayInfo(List<LoanDTO> loans, Map<String,CustomerDTO> customers){
        displayCustomers(customers);
        loansComponentController.displayLoans(loans);
    }

    private void displayCustomers(Map<String,CustomerDTO> customers){
        customersTable.getColumns().clear();
        ObservableList<CustomerDTO> customerData = FXCollections.observableArrayList();
        customerData.addAll(customers.values());

        customersTable.setItems(customerData);
        customersTable.getColumns().addAll(nameCol, balanceCol, numOfBorrowerLoansCol, numOfLenderLoansCol);


    }

    public void setUserName(String userName) {
        adminNameLabel.setText(userName);
    }

    public void startRefresher() {
        infoRefresher = new AdminRefresher(this::displayCustomers, loansComponentController::displayLoans);
        timer = new Timer();
        timer.schedule(infoRefresher, 0, 2000);
    }

    public int getTime() {
        return time.get();
    }
}

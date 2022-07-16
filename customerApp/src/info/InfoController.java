package info;


import dto.LoanDTO;
import loans.LoansController;
import dashboard.CustomerDashboardController;
import dto.CustomerDTO;
import dto.TransferDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.MasterDetailPane;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoController {
    private CustomerDashboardController customerDashboardController;

    @FXML private LoansController loanerLoansComponentController;
    @FXML private MasterDetailPane loanerLoansComponent;
    @FXML private LoansController lenderLoansComponentController;
    @FXML private MasterDetailPane lenderLoansComponent;

    @FXML private TableView<TransferDTO> transactionsTable;
    private TableColumn<TransferDTO,Integer> timeCol;
    private TableColumn<TransferDTO,Double> amountCol;
    private TableColumn<TransferDTO,Double> balanceBeforeCol;
    private TableColumn<TransferDTO,Double> balanceAfterCol;
    private TableColumn<TransferDTO,String> typeCol;

    @FXML private TextField amountTF;
    @FXML private Button chargeBTN;;
    @FXML private Button withdrawBTN;
    @FXML private Label errorLabel;

    private CustomerDTO customer;

    @FXML
    public void initialize(){
        timeCol = new TableColumn<>("time");
        amountCol = new TableColumn<>("amount");
        balanceBeforeCol = new TableColumn<>("balance before");
        balanceAfterCol = new TableColumn<>("balance after");
        typeCol = new TableColumn<>("");

        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceBeforeCol.setCellValueFactory(new PropertyValueFactory<>("balanceBefore"));
        balanceAfterCol.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transferType"));

        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: red");

    }
    @FXML
    public void charge(ActionEvent event) {
        try{
            double amount = Double.parseDouble(amountTF.getText());

            if(amount < 0)
                System.out.println("Out Of Range!");//throw new OutOfRangeException();

            customerDashboardController.charge(amount);
            errorLabel.setText("");

        }
        catch (Exception e){
            errorLabel.setText("errorMSG");//customerDashboardController.getErrorMessage(e));
        }
        finally {
            amountTF.setText("");
        }
    }
    @FXML
    public void withdraw(ActionEvent event) {
        try{
            double amount = Double.parseDouble(amountTF.getText());

            if(amount < 0)
                System.out.println("Out Of Range!");//throw new OutOfRangeException();

            customerDashboardController.withdraw(amount);
            errorLabel.setText("");
        }
        catch (Exception e){
            errorLabel.setText("error");//errorLabel.setText(customerDashboardController.getErrorMessage(e));
        }
        finally {
            amountTF.setText("");
        }
    }
    private void transfersDisplay(Map<Integer, List<TransferDTO>> transfers){
        transactionsTable.getColumns().clear();
        ObservableList<TransferDTO> transfersData = FXCollections.observableArrayList();
        transfers.forEach((k,v) -> transfersData.addAll(transfers.get(k)));

        transactionsTable.setItems(transfersData);
        transactionsTable.getColumns().addAll(timeCol, typeCol, amountCol, balanceBeforeCol, balanceAfterCol);
    }

    public void setCustomer(CustomerDTO customer){
        this.customer = customer;
        loanerLoansComponentController.displayLoans(new ArrayList<>(customer.getBorrowerLoans().values()));
        lenderLoansComponentController.displayLoans(new ArrayList<>(customer.getLenderLoans().values()));
        transfersDisplay(customer.getTransfers());
    }

    public void setCustomerDashboardController(dashboard.CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }

    public void setErrorMsg(String msg) {
        errorLabel.setText(msg);
    }
}

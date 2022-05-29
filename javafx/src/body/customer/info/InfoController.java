package body.customer.info;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.TransferDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.MasterDetailPane;
import ui.exceptions.OutOfRangeException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class InfoController {
    private CustomerBodyController customerBodyController;

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
                throw new OutOfRangeException();

            customerBodyController.charge(amount);
            errorLabel.setText("");

        }
        catch (Exception e){
            errorLabel.setText(customerBodyController.getErrorMessage(e));
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
                throw new OutOfRangeException();

            customerBodyController.withdraw(amount);
            errorLabel.setText("");
        }
        catch (Exception e){
            errorLabel.setText(customerBodyController.getErrorMessage(e));
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
    public void setCustomerBodyController(CustomerBodyController customerBodyController) {
        this.customerBodyController = customerBodyController;
    }
    public void setCustomer(CustomerDTO customer){
        this.customer = customer;
        loanerLoansComponentController.displayLoans(new ArrayList<>(customer.getBorrowerLoans().values()));
        lenderLoansComponentController.displayLoans(new ArrayList<>(customer.getLenderLoans().values()));
        transfersDisplay(customer.getTransfers());
    }
}

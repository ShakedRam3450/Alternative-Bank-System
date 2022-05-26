package body.customer.payment;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.NotificationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import loan.Loan;
import org.controlsfx.control.MasterDetailPane;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentController {
    private CustomerBodyController customerBodyController;

    @FXML private LoansController loansComponentController;
    @FXML private MasterDetailPane loansComponent;

    @FXML private TextField payDebtTF;

    @FXML private TableView<NotificationDTO> notificationsTable;
    private TableColumn<NotificationDTO, Integer> yazCol;
    private TableColumn<NotificationDTO, String> loanIdCol;
    private TableColumn<NotificationDTO, Double> amountCol;

    @FXML private RadioButton onePaymentRBTN;
    @FXML private RadioButton payAllLoanRBTN;
    @FXML private RadioButton payDebtRBTN;
    private ToggleGroup paymentOptions;

    @FXML private Label errorLabel;

    public void setCustomerBodyController(CustomerBodyController customerBodyController) {
        this.customerBodyController = customerBodyController;
    }

    public PaymentController(){
        yazCol = new TableColumn<>("yaz");
        loanIdCol = new TableColumn<>("loanId");
        amountCol = new TableColumn<>("amount");

        paymentOptions = new ToggleGroup();
    }
    @FXML
    public void initialize(){
        notificationsTable.setEditable(true);

        yazCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        loanIdCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("onePaymentAmount"));

        onePaymentRBTN.setToggleGroup(paymentOptions);
        payAllLoanRBTN.setToggleGroup(paymentOptions);
        payDebtRBTN.setToggleGroup(paymentOptions);

        onePaymentRBTN.setSelected(true);
        payDebtTF.editableProperty().bind(payDebtRBTN.selectedProperty());

    }
    @FXML
    public void pay(ActionEvent event){
        try {
            LoanDTO selectedLoan = getSelectedLoan();
            Toggle selectedBTN = paymentOptions.getSelectedToggle();

            if (selectedBTN == onePaymentRBTN){
                if(!selectedLoan.isTimeToPay(customerBodyController.getTime()))
                    throw new Exception();

                //PAY LOAN
            }
            else if (selectedBTN == payAllLoanRBTN){

                //PAY ALL LOAN
            }
            else if (selectedBTN == payDebtRBTN){
                if(!selectedLoan.getStatus().equals(Loan.Status.IN_RISK))
                    throw new Exception();
                double amount = getAmount();


                //PAY DEBT
            }
            else
                throw new Exception();

        }catch (Exception e){
            errorLabel.setText("error");
        }

    }

    private double getAmount() {
        try {
            return Double.parseDouble(payDebtTF.getText());
        }catch (Exception e){
            System.out.println("not double");
        }
        finally {
            return 0;
        }
    }

    private LoanDTO getSelectedLoan() throws Exception {
        List<LoanDTO> selectedLoans = loansComponentController.getSelectedLoans();
        if(selectedLoans.size() != 1)
            throw new Exception();
        return selectedLoans.get(0);
    }

    public void setLoansTable() {
        List<LoanDTO> needToPayLoans = customerBodyController.getCustomer().getBorrowerLoans().values().stream()
                .filter(loan -> loan.getStatus().equals(Loan.Status.ACTIVE) || loan.getStatus().equals(Loan.Status.IN_RISK))
                .collect(Collectors.toList());
        loansComponentController.displayLoans(needToPayLoans);
        loansComponentController.addSelectCol();

    }

    public void setNotificationsTable(List<NotificationDTO> customerNotifications) {
        notificationsTable.getColumns().clear();

        ObservableList<NotificationDTO> notificationsData = FXCollections.observableArrayList();
        notificationsData.addAll(customerNotifications);

        notificationsTable.setItems(notificationsData);
        notificationsTable.getColumns().addAll(yazCol, loanIdCol, amountCol);
    }
}

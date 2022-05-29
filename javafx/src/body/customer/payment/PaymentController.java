package body.customer.payment;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.NotificationDTO;
import exceptions.PaymentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import loan.Loan;
import org.controlsfx.control.MasterDetailPane;
import ui.exceptions.OutOfRangeException;

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

        errorLabel.setStyle("-fx-text-fill: red");
        errorLabel.setText("");
    }
    @FXML
    public void pay(ActionEvent event){
        try {
            LoanDTO selectedLoan = getSelectedLoan();
            Toggle selectedBTN = paymentOptions.getSelectedToggle();
            int curTime = customerBodyController.getTime();

            if (selectedBTN == onePaymentRBTN){
                if(!selectedLoan.isTimeToPay(curTime)) //not time to pay exception
                    throw new PaymentException("not time to pay");

                customerBodyController.payOnePayment(selectedLoan); // already paid this payment exception
            }
            else if (selectedBTN == payAllLoanRBTN){

                customerBodyController.payAllLoan(selectedLoan);
            }
            else if (selectedBTN == payDebtRBTN){
                if(!selectedLoan.getStatus().equals(Loan.Status.IN_RISK)) //not in risk exception
                    throw new PaymentException("loan is not in debt");
                double amount = getAmount();
                if(amount > customerBodyController.getCustomer().getBalance())
                    throw new PaymentException("you dont that much money");
                else if (amount < 0)
                    throw new OutOfRangeException();
                payDebtTF.setText("");
                customerBodyController.payDebt(selectedLoan, amount);
            }

            errorLabel.setText("");

        }catch (Exception e){
            errorLabel.setText(customerBodyController.getErrorMessage(e));
        }

    }

    private double getAmount() throws Exception {
        double res = 0;
        res = Double.parseDouble(payDebtTF.getText());
        return res;
    }

    private LoanDTO getSelectedLoan() throws Exception {
        List<LoanDTO> selectedLoans = loansComponentController.getSelectedLoans();
        if(selectedLoans.size() != 1)
            throw new PaymentException("you need to choose 1 loan");
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

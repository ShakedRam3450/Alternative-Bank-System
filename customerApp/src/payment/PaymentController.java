package payment;


import exceptions.*;
import loans.LoansController;
import dashboard.CustomerDashboardController;
import dto.LoanDTO;
import dto.NotificationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import loan.Loan;
import org.controlsfx.control.MasterDetailPane;


import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentController {
    private CustomerDashboardController customerDashboardController;

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
    @FXML private ComboBox<String> loanSelectCB;

    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
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
       /* try {
            String selectedLoanId = loanSelectCB.getSelectionModel().getSelectedItem();//getSelectedLoan();
            Toggle selectedBTN = paymentOptions.getSelectedToggle();
            int curTime = customerDashboardController.getTime();

            if (selectedBTN == onePaymentRBTN){
                if(!selectedLoan.isTimeToPay(curTime)) //not time to pay exception
                    throw new PaymentException("not time to pay");

                customerDashboardController.payOnePayment(selectedLoan); // already paid this payment exception
            }
            else if (selectedBTN == payAllLoanRBTN){

                customerDashboardController.payAllLoan(selectedLoan);
            }
            else if (selectedBTN == payDebtRBTN){
                if(!selectedLoan.getStatus().equals(Loan.Status.IN_RISK)) //not in risk exception
                    throw new PaymentException("loan is not in debt");
                double amount = getAmount();
                if(amount > customerDashboardController.getCustomer().getBalance())
                    throw new PaymentException("you dont that much money");
                else if (amount < 0)
                    throw new OutOfRangeException();
                payDebtTF.setText("");
                customerDashboardController.payDebt(selectedLoan, amount);
            }

            errorLabel.setText("");

        }catch (Exception e){
            errorLabel.setText(getErrorMassage(e));
        }*/

    }

    private double getAmount() throws Exception {
        double res = 0;
        res = Double.parseDouble(payDebtTF.getText());
        return res;
    }

   /* private LoanDTO getSelectedLoan() throws Exception {
        List<LoanDTO> selectedLoans = loansComponentController.getSelectedLoans();
        if(selectedLoans.size() != 1)
            throw new PaymentException("you need to choose 1 loan");
        return selectedLoans.get(0);
    }*/

    public void setLoansTable() {
        List<LoanDTO> needToPayLoans = customerDashboardController.getCustomer().getBorrowerLoans().values().stream()
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

    private String getErrorMassage(Exception e){
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

        /*else if(exceptionType == NoSuchCustomerException.class){
            NoSuchCustomerException exception = (NoSuchCustomerException)e;
            return "There is a customer in a loan that does not exists!" + "\n" +
                    "The name of the customer: " + exception.getInvalidCustomer() + "\n" +
                    "The customers that exist: " + exception.getCustomersNames();
        }*/

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
            return "Loan id: " + exception.getLoanId() + " has negative capital of: " + exception.getCapital();
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


}

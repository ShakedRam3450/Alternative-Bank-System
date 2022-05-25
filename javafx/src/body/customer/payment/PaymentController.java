package body.customer.payment;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.NotificationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    public void setCustomerBodyController(CustomerBodyController customerBodyController) {
        this.customerBodyController = customerBodyController;
    }

    @FXML
    public void initialize(){
        notificationsTable.setEditable(true);

        yazCol = new TableColumn<>("yaz");
        loanIdCol = new TableColumn<>("loanId");
        amountCol = new TableColumn<>("amount");

        yazCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        loanIdCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("onePaymentAmount"));
    }

    public void setLoansTable() {
        List<LoanDTO> needToPayLoans = customerBodyController.getCustomer().getBorrowerLoans().values().stream()
                .filter(loan -> loan.getStatus().equals(Loan.Status.ACTIVE) || loan.getStatus().equals(Loan.Status.IN_RISK))
                .collect(Collectors.toList());
        loansComponentController.displayLoans(needToPayLoans);
    }

    public void setNotificationsTable(List<NotificationDTO> customerNotifications) {
        notificationsTable.getColumns().clear();

        ObservableList<NotificationDTO> notificationsData = FXCollections.observableArrayList();
        notificationsData.addAll(customerNotifications);

        notificationsTable.setItems(notificationsData);
        notificationsTable.getColumns().addAll(yazCol, loanIdCol, amountCol);
    }
}

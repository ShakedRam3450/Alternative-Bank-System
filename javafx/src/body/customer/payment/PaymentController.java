package body.customer.payment;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.LoanDTO;
import javafx.fxml.FXML;
import loan.Loan;
import org.controlsfx.control.MasterDetailPane;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentController {
    private CustomerBodyController customerBodyController;

    @FXML private LoansController loansComponentController;
    @FXML private MasterDetailPane loansComponent;

    public void setCustomerBodyController(CustomerBodyController customerBodyController) {
        this.customerBodyController = customerBodyController;
    }

    @FXML
    public void initialize(){

    }

    public void setLoansTable() {
        List<LoanDTO> needToPayLoans = customerBodyController.getCustomer().getBorrowerLoans().values().stream()
                .filter(loan -> loan.getStatus().equals(Loan.Status.ACTIVE) || loan.getStatus().equals(Loan.Status.IN_RISK))
                .collect(Collectors.toList());
        loansComponentController.displayLoans(needToPayLoans);
    }
}

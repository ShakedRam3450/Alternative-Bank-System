package bank;

import com.sun.org.apache.xerces.internal.util.Status;
import customer.Customer;
import dto.CustomerDTO;
import dto.LoanDTO;
import exceptions.*;
import javafx.collections.ObservableList;
import loan.Loan;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Bank {
    boolean isFileExist();

    void readFile(File file) throws FileNotFoundException, FileNotXMLException, JAXBException, NoSuchCategoryException, NoSuchCustomerException, PaymentMarginException, SameCustomerNameException, SameLoanIdException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativeBalanceException, NegativePaysEveryYazException, NegativeCapitalException;

    Map<String, CustomerDTO> getCustomers();

    List<LoanDTO> getLoans();

    Set<String> getCategories();

    int getTime();

    void deposit(String chosenCustomer, double amount);

    boolean withdrawal(String chosenCustomer, double amount);

    List<LoanDTO> getEligibleLoans(CustomerDTO customer, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories);

    void placementActivation(String chosenCustomer, List<LoanDTO> chosenLoans, int amount, int maxOwnership);

    void timeAdvancement();
}

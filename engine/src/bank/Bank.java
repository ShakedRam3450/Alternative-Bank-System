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

    void readFile(File file, String customerName) throws FileNotFoundException, FileNotXMLException, JAXBException, NoSuchCategoryException, PaymentMarginException, SameCustomerNameException, SameLoanIdException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativeBalanceException, NegativePaysEveryYazException, NegativeCapitalException;

    Map<String, CustomerDTO> getCustomers();

    List<LoanDTO> getLoans();

    Set<String> getCategories();

    int getTime();

    void deposit(String chosenCustomer, double amount);

    boolean withdrawal(String chosenCustomer, double amount);

    List<LoanDTO> getEligibleLoans(String customerName, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, Set<String> chosenCategories);

    void placementActivation(String chosenCustomer, List<LoanDTO> chosenLoans, int amount, int maxOwnership);

    void timeAdvancement();

    void payOnePayment(LoanDTO selectedLoan) throws Exception;

    void payAllLoan(LoanDTO selectedLoan);

    void payDebt(LoanDTO selectedLoan, double amount);

    boolean getIsAdminLoggedIn();

    void setIsAdminLoggedIn(boolean val);

    void addAdmin(String usernameFromParameter);

    boolean isUserExists(String usernameFromParameter);

    void addCustomer(String usernameFromParameter);

    List<LoanDTO> getCustomerNeedToPayLoans(String customerName);

    void insertNewLoan(String customerName, String id, int capital, String category, int totalYazTime, int paysEveryYaz, int interestPerPayment) throws SameLoanIdException, NoSuchCategoryException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativePaysEveryYazException, NegativeCapitalException, PaymentMarginException;

    int getVersion();
}

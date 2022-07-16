import bank.Bank;
import bank.BankImpl;
import dto.CustomerDTO;
import dto.InvestorDTO;
import dto.LoanDTO;
import exceptions.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.*;

public class Console implements UserInterface{
    private Bank bank;
    private Menu menu;
    private final int STRING_CHOICE = -1;

    public Console(){
        bank = new BankImpl();
        menu = new Menu();
    }
    @Override
    public void start() {
        int choice;
        boolean exit = false;
        do {
            choice = menu.getMainChoice(bank.getTime());

            if(isNoFileButNeeded(choice)){
                System.out.println("There is no file but is needed!");
                continue;
            }
            switch (choice){
                case 1:
                    //readFile();
                    break;
                case 2:
                    displayLoans();
                    break;
                case 3:
                    displayCustomers();
                    break;
                case 4:
                    deposit();
                    break;
                case 5:
                    withdrawal();
                    break;
                case 6:
                    //placementActivation();
                    break;
                case 7:
                    timeAdvancement();
                    break;
                case 8:
                    exit = true;
                    break;
                default:
                    break;
            }

        } while (!exit);
        System.out.println("Goodbye...");
    }
    private boolean isNoFileButNeeded(int choice){
        if(!bank.isFileExist())
            return choice >= 2 && choice <=7;
        return false;
    }
    /*public void readFile(){
        String fileName;
        fileName = menu.getFileName();
        try {
            bank.readFile(fileName);
            System.out.println("File was read successfully");
        } catch (Exception e){
           printErrorMessage(e);
        }

    }*/
    public void printErrorMessage(Exception e){
        Class <?> exceptionType = e.getClass();

        if(exceptionType == FileNotFoundException.class)
            System.out.println("File does not exist or a directory!");

        else if(exceptionType == JAXBException.class)
            System.out.println("JAXB Exception!");

        else if(exceptionType == FileNotXMLException.class)
            System.out.println("File is not XML file!");

        else if(exceptionType == NoSuchCategoryException.class) {
            NoSuchCategoryException exception = (NoSuchCategoryException)e;
            System.out.println("There is a loan with invalid category!");
            System.out.println("The invalid category: " + exception.getInvalidCategory());
            System.out.println("The allowed categories are: " + exception.getCategories());
        }

        /*else if(exceptionType == NoSuchCustomerException.class){
            NoSuchCustomerException exception = (NoSuchCustomerException)e;
            System.out.println("There is a customer in a loan that does not exists!");
            System.out.println("The name of the customer: " + exception.getInvalidCustomer());
            System.out.println("The customers that exist: " + exception.getCustomersNames());
        }*/

        else if(exceptionType == PaymentMarginException.class){
            PaymentMarginException exception = (PaymentMarginException)e;
            System.out.println("There is an issue with loan margin payment!");
            System.out.println("the total time is " + exception.getTotalYazTime() + " but the margin is " + exception.getPaysEveryYaz());
        }

        else if(exceptionType == SameCustomerNameException.class) {
            SameCustomerNameException exception = (SameCustomerNameException) e;
            System.out.println("There are customers with the same name!");
            System.out.println("The name that appears more that once is: " + exception.getName());
        }

        else if(exceptionType == SameLoanIdException.class){
            SameLoanIdException exception = (SameLoanIdException) e;
            System.out.println("There are loans with the same id!");
            System.out.println("The id that appears more that once is: " + exception.getId());
        }

        else if (exceptionType == NegativeBalanceException.class){
            NegativeBalanceException exception = (NegativeBalanceException) e;
            System.out.println("Customer: " + exception.getName() + " has negative balance of: " + exception.getBalance());
        }

        else if (exceptionType == NegativeCapitalException.class){
            NegativeCapitalException exception = (NegativeCapitalException) e;
            System.out.println("Loan id: " + exception.getLoanId() + " has negative capital of: " + exception.getCapital());
        }

        else if (exceptionType == NegativePaysEveryYazException.class){
            NegativePaysEveryYazException exception = (NegativePaysEveryYazException) e;
            System.out.println("Loan id: " + exception.getLoanId() + " has negative PaysEveryYaz of: " + exception.getPaysEveryYaz());
        }

        else if (exceptionType == NegativeIntristPerPaymentException.class){
            NegativeIntristPerPaymentException exception = (NegativeIntristPerPaymentException) e;
            System.out.println("Loan id: " + exception.getLoanId() + " has negative InterestPerPayment of: " + exception.getIntristPerPayment());
        }

        else if (exceptionType == NegativeTotalYazTimeException.class){
            NegativeTotalYazTimeException exception = (NegativeTotalYazTimeException) e;
            System.out.println("Loan id: " + exception.getLoanId() + " has negative TotalYazTime of: " + exception.getTotalYazTime());
        }

    }
    public void displayLoans(){
        List<LoanDTO> loans = bank.getLoans();
        for (LoanDTO loan: loans){
            System.out.println("id=" + loan.getId());
            System.out.println("owner=" + loan.getOwnerName());
            System.out.println("category=" + loan.getCategory());
            System.out.println("capital=" + loan.getCapital() + ", totalYazTime=" + loan.getTotalYazTime());
            System.out.println("interestPerPayment=" + loan.getInterestPerPayment() + ", paysEveryYaz=" + loan.getPaysEveryYaz());
            System.out.println("status=" + statusDisplay(loan));
        }
    }
    private StringBuilder statusDisplay(LoanDTO loan){
        StringBuilder res = new StringBuilder();
        res.append(loan.getStatus()).append("\n");
        switch (loan.getStatus()){
            case NEW:
                break;
            case PENDING:
                res.append(investorsDisplay(loan));
                res.append("Amount raised: ").append(loan.getAmountRaised()).append("Amount remaining: ").append(loan.getAmountRemaining()).append("\n");
                break;
            case ACTIVE:
                res.append(investorsDisplay(loan));
                res.append("Start time: ").append(loan.getStartTime()).append("\n");
                res.append("Next payment time: ");
                if(loan.getPayments().isEmpty())
                    res.append(loan.getStartTime() + loan.getPaysEveryYaz()).append("\n");
                else
                    res.append(loan.getLastPaymentTime() + loan.getPaysEveryYaz()).append("\n");
                res.append(paymentsDisplay(loan));
                res.append("Total capital paid: ").append(loan.getTotalCapitalPaid()).append("\n");
                res.append("Total interest paid: ").append(loan.getTotalInterestPaid()).append("\n");
                res.append("Total capital remaining: ").append(loan.getTotalCapitalRemaining()).append("\n");
                res.append("Total interest remaining: ").append(loan.getTotalInterestRemaining()).append("\n");
                break;
            case IN_RISK:
                res.append(investorsDisplay(loan));
                res.append("Start time: ").append(loan.getStartTime()).append("\n");
                res.append("Next payment time: ");
                if(loan.getPayments().isEmpty())
                    res.append(loan.getStartTime() + (loan.getPaysEveryYaz() * loan.getNumberOfUnpaidPayments())).append("\n");
                else
                    res.append(loan.getLastPaymentTime() + (loan.getPaysEveryYaz() * loan.getNumberOfUnpaidPayments())).append("\n");
                res.append(paymentsDisplay(loan));
                res.append("Total capital paid: ").append(loan.getTotalCapitalPaid()).append("\n");
                res.append("Total interest paid: ").append(loan.getTotalInterestPaid()).append("\n");
                res.append("Total capital remaining: ").append(loan.getTotalCapitalRemaining()).append("\n");
                res.append("Total interest remaining: ").append(loan.getTotalInterestRemaining()).append("\n");
                res.append("Total debt: ").append(loan.getDebt()).append(" number of unpaid payments: ").append(loan.getNumberOfUnpaidPayments()).append("\n");
                break;
            case FINISHED:
                res.append(investorsDisplay(loan));
                res.append("Start time: ").append(loan.getStartTime()).append("\n");
                res.append("End time: ").append(loan.getEndTime()).append("\n");
                res.append(paymentsDisplay(loan));
                break;
        }
        return res;
    }
    private StringBuilder paymentsDisplay(LoanDTO loan){
        StringBuilder res = new StringBuilder();
        res.append("Payments:\n");
        for (Integer key: loan.getPayments().keySet())
            res.append(loan.getPayments().get(key));
        return res;
    }
    private StringBuilder investorsDisplay(LoanDTO loan){
        StringBuilder res = new StringBuilder();
        res.append("Investors:\n");
        for(String key: loan.getInvestors().keySet()){
            InvestorDTO tmpInvestor = loan.getInvestors().get(key);
            res.append(tmpInvestor.getName()).append(" ").append(tmpInvestor.getPartInInvestment() * loan.getCapital()).append("\n");
        }
        return res;
    }
    public void displayCustomers(){
        Map<String, CustomerDTO> customers = bank.getCustomers();
        CustomerDTO tmpCustomer;
        for (String key: customers.keySet()){
            tmpCustomer = customers.get(key);
            System.out.println("name=" + tmpCustomer.getName() + ", balance=" + tmpCustomer.getBalance());
            System.out.println("transfers:" );
            System.out.println(transfersDisplay(tmpCustomer));
            System.out.println("borrowerLoans:");;
            System.out.println(loansDisplay(tmpCustomer.getBorrowerLoans()));
            System.out.println("lenderLoans:"); ;
            System.out.println(loansDisplay(tmpCustomer.getLenderLoans()));
        }
    }
    private StringBuilder transfersDisplay(CustomerDTO customer){
        StringBuilder res = new StringBuilder();
        if(customer.getTransfers().isEmpty()){
            res.append("None\n");
            return res;
        }
        for (Integer key: customer.getTransfers().keySet())
            res.append(customer.getTransfers().get(key)).append("\n");
        return res;
    }
    private StringBuilder loansDisplay(Map<String, LoanDTO> loans){
        StringBuilder res = new StringBuilder();
        if(loans.isEmpty()){
            res.append("None\n");
            return res;
        }

        List<LoanDTO> loansAsList = new ArrayList<>(loans.values());
        for (LoanDTO loan: loansAsList){
            res.append("id=").append(loan.getId()).append("\n");
            res.append("owner=").append(loan.getOwnerName()).append("\n");
            res.append("category=").append(loan.getCategory()).append("\n");
            res.append("capital=").append(loan.getCapital()).append(", totalYazTime=").append(loan.getTotalYazTime()).append("\n");
            res.append("interestPerPayment=").append(loan.getInterestPerPayment()).append(", paysEveryYaz=").append(loan.getPaysEveryYaz()).append("\n");
            res.append("status=").append(statusDisplay(loan)).append("\n");
        }
        return res;
    }
    public void deposit(){
        Map<String, CustomerDTO> customers = bank.getCustomers();
        String chosenCustomer = menu.getCustomerChoice(customers);
        int amount = menu.getAmount("Please enter amount to deposit:");
        bank.deposit(chosenCustomer, amount);
        System.out.println("deposit success...");
    }
    public void withdrawal(){
        Map<String, CustomerDTO> customers = bank.getCustomers();
        String chosenCustomer = menu.getCustomerChoice(customers);
        int amount = menu.getAmount("Please enter amount to withdraw:");
        while (!bank.withdrawal(chosenCustomer, amount)){
            System.out.println("requested withdrawal amount is bigger than balance!");
            amount = menu.getAmount("Please enter amount to withdraw:");
        }
        System.out.println("withdrawal success...");
    }
    /*public void placementActivation(){
        Map<String, CustomerDTO> customers = bank.getCustomers();
        String chosenCustomer = menu.getCustomerChoice(customers);
        int amount = menu.getAmount("Please enter amount to invest:");
        while (bank.getCustomers().get(chosenCustomer).getBalance() < amount){
            System.out.println("This customer cant invest so much!");
            amount = menu.getAmount("Please enter amount to invest:");
        }
        Set<String> chosenCategories = menu.getCategoryChoice(bank.getCategories());
        double minimumInterest = menu.getMinimumInterest();
        int minimumTotalTime = menu.getMinimumTotalTime();
        List<LoanDTO> eligibleLoans = bank.getEligibleLoans(chosenCustomer, amount, chosenCategories, minimumInterest, minimumTotalTime);
        List<LoanDTO> chosenLoans = getLoansChoice(eligibleLoans);
        if(chosenLoans != null)
            bank.placementActivation(chosenCustomer, chosenLoans, amount);

    }*/
    private List<LoanDTO> getLoansChoice(List<LoanDTO> eligibleLoans){
        int index = 1, choice = 0;
        List<LoanDTO> res = new ArrayList<>();
        boolean validChoice = false, finish = false;
        if(eligibleLoans.size() == 0) {
            System.out.println("No loans to invest in");
            return null;
        }
        while(!finish){
            System.out.println("Choose the loans you want to participate in(at least ONE, press ENTER after each choice: ");
            for (LoanDTO loan: eligibleLoans){
                System.out.println(index + " - id=" + loan.getId());
                System.out.println("owner=" + loan.getOwnerName());
                System.out.println("category=" + loan.getCategory());
                System.out.println("capital=" + loan.getCapital() + ", totalYazTime=" + loan.getTotalYazTime());
                System.out.println("interestPerPayment=" + loan.getInterestPerPayment() + ", paysEveryYaz=" + loan.getPaysEveryYaz());
                System.out.println("status=" + statusDisplay(loan));
                index++;
            }
            System.out.println(index + " - Finish choice");

            while (choice != eligibleLoans.size() + 1){
                choice = menu.getChoice();
                if(choice == STRING_CHOICE)
                    System.out.println("String was entered!");
                else {
                    validChoice = menu.isInRangeChoice(choice, 1, index);
                    if (!validChoice)
                        System.out.println("Number is out of range!");
                    else if(choice != eligibleLoans.size() + 1)
                        if(!res.contains(eligibleLoans.get(choice - 1)))
                            res.add(eligibleLoans.get(choice - 1));
                }
            }
            if(res.isEmpty()) {
                System.out.println("You have to choose at least ONE loan!");
                index = 1;
                choice = 0;
                validChoice = false;
            }
            else
                finish = true;
        }

        return res;
    }
    public void timeAdvancement(){
        bank.timeAdvancement();
        System.out.println("Time was advanced...");
        System.out.println("We were in time: " + (bank.getTime() - 1) + " and now the time is: " + bank.getTime());
    }
}

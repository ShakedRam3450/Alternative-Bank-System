package file;

import exceptions.*;
import generated.AbsDescriptor;
import generated.AbsLoan;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileChecker {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "generated";

    public InputStream isFileExist(String fileName) throws FileNotFoundException{
        return new FileInputStream(fileName);
    }
    public void isXMLFile(String fileName) throws FileNotXMLException {
        if(!fileName.endsWith(".xml"))
            throw new FileNotXMLException();
    }
    public AbsDescriptor openFile(InputStream in) throws JAXBException{
        return deserializeFrom(in);
    }
    private static AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }
    public void checkXMLContent(AbsDescriptor absDescriptor) throws NoSuchCategoryException, PaymentMarginException, SameCustomerNameException, SameLoanIdException, NegativeBalanceException, NegativeCapitalException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativePaysEveryYazException {
        List<String> categories = absDescriptor.getAbsCategories().getAbsCategory();
        //List<AbsCustomer> customers = absDescriptor.getAbsCustomers().getAbsCustomer();
        List<AbsLoan> loans = absDescriptor.getAbsLoans().getAbsLoan();
        checkCategories(loans, categories);
        checkPayments(loans);
        checkLoansId(loans);
        checkNumbers(loans);
    }
    private void checkNumbers(List<AbsLoan> loans) throws NegativeBalanceException, NegativeCapitalException, NegativeTotalYazTimeException, NegativePaysEveryYazException, NegativeIntristPerPaymentException {

        for (AbsLoan loan: loans){
            if (loan.getAbsCapital() < 0)
                throw new NegativeCapitalException(loan.getId().trim(), loan.getAbsCapital());
            else if(loan.getAbsTotalYazTime() < 0)
                throw new NegativeTotalYazTimeException(loan.getId().trim(), loan.getAbsTotalYazTime());
            else if(loan.getAbsPaysEveryYaz() < 0)
                throw new NegativePaysEveryYazException(loan.getId().trim(), loan.getAbsPaysEveryYaz());
            else if(loan.getAbsIntristPerPayment() < 0)
                throw new NegativeIntristPerPaymentException(loan.getId().trim(), loan.getAbsIntristPerPayment());
        }
    }
    private void checkLoansId(List<AbsLoan> loans) throws SameLoanIdException{
        String tmpId;
        int counter = 0;
        List<String> loansIds = new ArrayList<>();

        for (AbsLoan loan: loans)
            loansIds.add(loan.getId().trim());

        for (AbsLoan loan: loans){
            tmpId = loan.getId().trim();
            for(String id: loansIds){
                if(id.equalsIgnoreCase(tmpId))
                    counter++;
            }
            if(counter > 1)
                throw new SameLoanIdException(tmpId);
            counter = 0;
        }
    }
    /*private void checkCustomersNames(List<AbsCustomer> customers) throws SameCustomerNameException{
        String tmpName;
        int counter = 0;
        List<String> customersNames = new ArrayList<>();

        for (AbsCustomer customer: customers)
            customersNames.add(customer.getName().trim());

        for (AbsCustomer customer: customers){
            tmpName = customer.getName().trim();
            for(String name: customersNames){
                if(name.equalsIgnoreCase(tmpName))
                    counter++;
            }
            if(counter > 1)
                throw new SameCustomerNameException(tmpName);
            counter = 0;
        }
    }*/
    private void checkPayments(List<AbsLoan> loans) throws PaymentMarginException{
        for(AbsLoan loan: loans)
            if(loan.getAbsTotalYazTime() % loan.getAbsPaysEveryYaz() != 0)
                throw new PaymentMarginException(loan.getAbsTotalYazTime(), loan.getAbsPaysEveryYaz());

    }
    /*private void checkCustomersInLoans(List<AbsLoan> loans, List<AbsCustomer> customers) throws NoSuchCustomerException{
        for (AbsLoan loan: loans) {
            if(!isValidCustomer(loan, customers))
                throw new NoSuchCustomerException(loan.getAbsOwner().trim(), customers);
        }
    }*/
    /*private boolean isValidCustomer(AbsLoan loan, List<AbsCustomer> customers){
        String loanOwnerName = loan.getAbsOwner().trim();
        for (AbsCustomer customer: customers){
            if (loanOwnerName.equalsIgnoreCase(customer.getName().trim()))
                return true;
        }
        return false;
    }*/
    private void checkCategories(List<AbsLoan> loans, List<String> categories) throws NoSuchCategoryException{
        for (AbsLoan loan: loans){
            if(!isValidCategory(loan, categories))
                throw new NoSuchCategoryException(loan.getAbsCategory().trim(), categories);
        }
    }
    private boolean isValidCategory(AbsLoan loan, List<String> categories){
        String loanCategory = loan.getAbsCategory().trim();
        for (String category: categories){
            if(loanCategory.equalsIgnoreCase(category.trim()))
                return true;
        }
        return false;
    }
}

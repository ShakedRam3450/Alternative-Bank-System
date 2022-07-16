import bank.Bank;
import exceptions.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;

@WebServlet(name = "InsertNewLoanServlet", urlPatterns = "/insertNewLoan")
public class InsertNewLoanServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String customerName = req.getParameter("customerName");
        String id = req.getParameter("id");
        int capital = Integer.parseInt(req.getParameter("capital"));
        String category = req.getParameter("category");
        int totalYazTime = Integer.parseInt(req.getParameter("totalYazTime"));
        int paysEveryYaz = Integer.parseInt(req.getParameter("paysEveryYaz"));
        int interestPerPayment = Integer.parseInt(req.getParameter("interestPerPayment"));

        try {
            Bank bank = ServletUtils.getBank(getServletContext());
            bank.insertNewLoan(customerName, id, capital, category, totalYazTime, paysEveryYaz, interestPerPayment);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e){
            resp.getWriter().println(getErrorMassage(e));
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }

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

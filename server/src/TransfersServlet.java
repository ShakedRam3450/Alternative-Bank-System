import bank.Bank;
import com.google.gson.Gson;
import dto.CustomerDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "TransfersServlet", urlPatterns = "/transfer")
public class TransfersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String customerName = request.getParameter("customerName");
        String actionType = request.getParameter("actionType");
        double amount = Double.parseDouble(request.getParameter("amount"));
        Bank bank = ServletUtils.getBank(getServletContext());
        Gson gson = new Gson();


        if(actionType.equals("+")) { //deposit
            bank.deposit(customerName,amount);
            response.setStatus(HttpServletResponse.SC_OK);
        }

        else { //withdraw
            if(bank.withdrawal(customerName,amount))
                response.setStatus(HttpServletResponse.SC_OK);
            else
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        CustomerDTO customerDTO = bank.getCustomers().get(customerName);
        try(PrintWriter out = response.getWriter()) {
            String jsonCustomerDTO = gson.toJson(customerDTO);
            out.println(jsonCustomerDTO);
            out.flush();
        }
    }
}

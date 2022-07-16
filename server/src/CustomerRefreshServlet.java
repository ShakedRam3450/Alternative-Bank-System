import bank.Bank;
import com.google.gson.Gson;
import dto.AdminInfoDTO;
import dto.CustomerDTO;
import dto.CustomerInfoDTO;
import dto.LoanDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Constants;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "CustomerRefreshServlet", urlPatterns = "/customerRefresh")
public class CustomerRefreshServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String customerName = req.getParameter("customerName");
        int version = Integer.parseInt(req.getParameter("version"));

        try (PrintWriter out = resp.getWriter()) {
            Gson gson = Constants.GSON_INSTANCE;
            Bank bank = ServletUtils.getBank(getServletContext());
            if(bank.getVersion() != version){
                CustomerDTO customerDTO = bank.getCustomers().get(customerName);
                List<LoanDTO> needToPayLoans = bank.getCustomerNeedToPayLoans(customerName);
                int time = bank.getTime();
                Set<String> categories = bank.getCategories();

                CustomerInfoDTO customerInfo = new CustomerInfoDTO(customerDTO,needToPayLoans,bank.getVersion(),time,categories);

                String info = gson.toJson(customerInfo);
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.println(gson.toJson(info));
                out.flush();
            }
            else{
                resp.setStatus(HttpServletResponse.SC_OK);
            }


        }
    }
}

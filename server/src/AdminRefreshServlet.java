import bank.Bank;
import com.google.gson.Gson;
import dto.AdminInfoDTO;
import dto.CustomerDTO;
import dto.LoanDTO;
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

@WebServlet(name = "AdminRefreshServlet", urlPatterns = "/adminRefresh")
public class AdminRefreshServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = Constants.GSON_INSTANCE;
            Bank bank = ServletUtils.getBank(getServletContext());
            Map<String, CustomerDTO> customers = bank.getCustomers();
            List<LoanDTO> loans = bank.getLoans();
            AdminInfoDTO info = new AdminInfoDTO(customers,loans);
            out.println(gson.toJson(info));
            out.flush();
        }
    }

}

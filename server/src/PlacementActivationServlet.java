import bank.Bank;
import com.google.gson.reflect.TypeToken;
import dto.CustomerDTO;
import dto.LoanDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Constants;
import utils.ServletUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@WebServlet(name = "PlacementActivationServlet", urlPatterns = "/placementActivation")
public class PlacementActivationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Bank bank = ServletUtils.getBank(getServletContext());
        String customerName = req.getParameter("customerName");
        int amount = Integer.parseInt(req.getParameter("amount"));
        int maxOwnership = Integer.parseInt(req.getParameter("maxOwnership"));

        Type listType = new TypeToken<List<LoanDTO>>(){}.getType();
        List<LoanDTO> eligibleLoans = Constants.GSON_INSTANCE.fromJson(req.getReader(), listType);

        bank.placementActivation(customerName, eligibleLoans, amount, maxOwnership);
        CustomerDTO customerDTO = bank.getCustomers().get(customerName);
        String json = Constants.GSON_INSTANCE.toJson(customerDTO);
        resp.getWriter().println(json);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}

import bank.Bank;
import com.google.gson.Gson;
import dto.LoanDTO;
import dto.ScrambleDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Constants;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ScrambleServlet", urlPatterns = "/scramble")
public class ScrambleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Bank bank = ServletUtils.getBank(getServletContext());
        Gson gson = new Gson();
        String data = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        ScrambleDTO scrambleDTO = gson.fromJson(data, ScrambleDTO.class);

        String customerName = scrambleDTO.getCustomerName();
        int amount = scrambleDTO.getAmount();
        int minInterest = scrambleDTO.getMinInterest();
        int minYaz = scrambleDTO.getMinYaz();
        int maxLoans = scrambleDTO.getMaxLoans();
        int maxOwnership = scrambleDTO.getMaxOwnership();
        Set<String> chosenCategories = scrambleDTO.getChosenCategories();

        List<LoanDTO> eligibleLoans = bank.getEligibleLoans(customerName, amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);
        resp.getWriter().println(Constants.GSON_INSTANCE.toJson(eligibleLoans));
        resp.setStatus(HttpServletResponse.SC_OK);

    }
}

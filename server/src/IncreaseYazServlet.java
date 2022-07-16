import bank.Bank;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Constants;
import utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "IncreaseYazServlet", urlPatterns = "/increaseYaz")
public class IncreaseYazServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Bank bank = ServletUtils.getBank(getServletContext());
        bank.timeAdvancement();
        Gson gson = Constants.GSON_INSTANCE;
        String json = gson.toJson(bank.getTime());
        resp.getWriter().println(json);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}

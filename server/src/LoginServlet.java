import bank.Bank;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import utils.Constants;
import utils.ServletUtils;


import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter("isAdmin").equals("1"))
            adminLogin(request, response);
        else
            customerLogin(request, response);
    }

    private void adminLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        //UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Bank bank = ServletUtils.getBank(getServletContext());
        String usernameFromParameter = request.getParameter("name");

        synchronized (this){
            if(usernameFromParameter.isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("empty");
            }
            else if(bank.getIsAdminLoggedIn()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().println("admin is already logged in " + usernameFromParameter);
            }
            else if (bank.isUserExists(usernameFromParameter)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().println("conflict " + usernameFromParameter );
            }
            else {
                bank.addAdmin(usernameFromParameter);
                bank.setIsAdminLoggedIn(true);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("ok "+ usernameFromParameter);
            }
        }

    }

    private void customerLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        //UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Bank bank = ServletUtils.getBank(getServletContext());
        String usernameFromParameter = request.getParameter("name");

        synchronized (this) {
            if(usernameFromParameter.isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("empty");
            }
            else if (bank.isUserExists(usernameFromParameter)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().println("conflict " + usernameFromParameter );
            }
            else {
                bank.addCustomer(usernameFromParameter);
                response.setStatus(HttpServletResponse.SC_OK);
                String json = Constants.GSON_INSTANCE.toJson(bank.getCustomers().get(usernameFromParameter));
                response.getWriter().println(json);
            }
        }
    }

}



package utils;

import bank.Bank;
import bank.BankImpl;
import bank.UserManager;
import jakarta.servlet.ServletContext;

public class ServletUtils {

    public static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    public static final String BANK_ATTRIBUTE_NAME = "bank";
    private static final Object userManagerLock = new Object();
    private static final Object bankLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
        if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null)
            servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static Bank getBank(ServletContext servletContext) {

        synchronized (bankLock) {
            if (servletContext.getAttribute(BANK_ATTRIBUTE_NAME) == null)
                servletContext.setAttribute(BANK_ATTRIBUTE_NAME, new BankImpl());
        }
        return (Bank) servletContext.getAttribute(BANK_ATTRIBUTE_NAME);
    }
}

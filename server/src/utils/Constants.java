package utils;

import com.google.gson.Gson;

public class Constants {
    public static Gson GSON_INSTANCE = new Gson();
    public static String MAIN_URL = "http://localhost:8080/server_Web_exploded";
    public static String ADMIN_REFRESH_URL = "/adminRefresh";
    public static String FILE_URL = "/readFile";
    public static String LOGIN_URL = "/login";
    public static String TRANSFERS_URL = "/transfer";
    public static String INCREASE_YAZ_URL = "/increaseYaz";
    public static String SCRAMBLE_URL = "/scramble";
    public static String PLACEMENT_ACTIVATION_URL = "/placementActivation";
    public static String CUSTOMER_REFRESH_URL = "/customerRefresh";
    public static String INSERT_NEW_LOAN_URL = "/insertNewLoan";
    public static int NO_LIMIT = -1;
}

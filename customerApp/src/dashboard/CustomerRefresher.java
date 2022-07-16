package dashboard;

import com.google.gson.Gson;
import com.oracle.jrockit.jfr.Producer;
import dto.AdminInfoDTO;
import dto.CustomerDTO;
import dto.CustomerInfoDTO;
import dto.LoanDTO;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import main.HttpClientUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomerRefresher extends TimerTask {
    private Supplier<Integer> getVersion;
    private String customerName;
    private Consumer<Integer> setVersion;
    private Consumer<Integer> setTime;
    private Consumer<CustomerDTO> setCustomer;
    private Consumer<List<LoanDTO>> addNotifications;
    private Consumer<Set<String>> setCategories;


    public CustomerRefresher(Supplier<Integer> getVersion ,String customerName, Consumer<Integer> setVersion, Consumer<Integer> setTime, Consumer<CustomerDTO> setCustomer,
                             Consumer<List<LoanDTO>> addNotifications, Consumer<Set<String>> setCategories){
        this.getVersion = getVersion;
        this.customerName = customerName;
        this.setVersion = setVersion;
        this.setTime = setTime;
        this.setCustomer = setCustomer;
        this.addNotifications = addNotifications;
        this.setCategories = setCategories;
    }
    @Override
    public void run(){
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.CUSTOMER_REFRESH_URL)
                .newBuilder()
                .addQueryParameter("customerName", customerName)
                .addQueryParameter("version", String.valueOf(this.getVersion.get()))
                .build()
                .toString();

        HttpClientUtil.runGetReq(finalUrl, new Callback() {

            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {
                System.out.println("OnFailure");
            }

            @Override
            public void onResponse(@org.jetbrains.annotations.NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpServletResponse.SC_ACCEPTED) {
                    String json = response.body().string();
                    json = json.replace("\\", "");
                    json = json.substring(1);
                    json = json.replaceAll("]}\"", "]}");
                    Gson gson = new Gson();
                    CustomerInfoDTO customerInfo = gson.fromJson(json, CustomerInfoDTO.class);
                    CustomerDTO customerDTO = customerInfo.getCustomer();
                    List<LoanDTO> needToPayLoans = customerInfo.getNeedToPayLoans();
                    Set<String> categories = customerInfo.getCategories();
                    int time = customerInfo.getTime();
                    int version = customerInfo.getVersion();

                    Platform.runLater(() ->{
                        setTime.accept(time);
                        setVersion.accept(version);
                        addNotifications.accept(needToPayLoans);
                        setCategories.accept(categories);
                        setCustomer.accept(customerDTO);
                    });
                }

            }
        });
    }
}

package body;

import com.google.gson.Gson;
import dto.AdminInfoDTO;
import dto.CustomerDTO;
import dto.LoanDTO;
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
import java.util.TimerTask;
import java.util.function.Consumer;

public class AdminRefresher extends TimerTask {
    Consumer<Map<String, CustomerDTO>> displayCustomers;
    Consumer<List<LoanDTO>> displayLoans;

    public AdminRefresher(Consumer<Map<String, CustomerDTO>> displayCustomers, Consumer<List<LoanDTO>> displayLoans) {
        this.displayCustomers = displayCustomers;
        this.displayLoans = displayLoans;
    }

    @Override
    public void run(){
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.ADMIN_REFRESH_URL)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runGetReq(finalUrl, new Callback() {

            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {
                System.out.println("OnFailure");
            }

            @Override
            public void onResponse(@org.jetbrains.annotations.NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Gson gson = Constants.GSON_INSTANCE;
                AdminInfoDTO info = gson.fromJson(json, AdminInfoDTO.class);
                Platform.runLater(() ->{
                    displayCustomers.accept(info.getCustomers());
                    displayLoans.accept(info.getLoans());
                });
            }
        });
    }
}

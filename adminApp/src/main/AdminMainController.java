package main;

import body.AdminBodyController;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import login.AdminLoginController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;

import java.io.IOException;
import java.net.URL;

public class AdminMainController {
    private Parent loginComponent;
    private AdminLoginController loginComponentController;
    private Parent bodyComponent;
    private AdminBodyController bodyComponentController;

    @FXML private BorderPane mainComponent;
    private Stage primaryStage;

    @FXML
    public void initialize() throws IOException {
        loadLogin();
        loadDashboard();
        mainComponent.setCenter(loginComponent);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void loadDashboard() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/body/adminBody.fxml");
        fxmlLoader.setLocation(url);
        this.bodyComponent = fxmlLoader.load(url.openStream());
        this.bodyComponentController = fxmlLoader.getController();
        this.bodyComponentController.setAdminMainController(this);
    }

    private void loadLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/login/adminLoginPage.fxml");
        fxmlLoader.setLocation(url);
        this.loginComponent = fxmlLoader.load(url.openStream());
        this.loginComponentController = fxmlLoader.getController();
        this.loginComponentController.setAdminMainController(this);
    }

    public void switchToDashboard(String userName) {
        mainComponent.setCenter(bodyComponent);
        bodyComponentController.setUserName(userName);
        bodyComponentController.startRefresher();
    }

    @FXML
    public void increaseYaz() {
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.INCREASE_YAZ_URL)
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
                int time = gson.fromJson(json, int.class);
                Platform.runLater(() ->{
                    bodyComponentController.setTime(time);
                });
                //NEED TO UPDATE CUSTOMERS ABOUT THE NEW TIME

            }
        });
    }
}

package login;

import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.AdminMainController;
import main.HttpClientUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import utils.Constants;

import java.io.IOException;

public class AdminLoginController {

    private AdminMainController adminMainController;
    private Stage primaryStage;
    @FXML
    private ScrollPane mainComponent;
    @FXML
    private TextField userNameTF;
    @FXML
    private Button loginBTN;
    @FXML
    private Label msgLabel;

    @FXML
    void login(ActionEvent event) {
        String userName = userNameTF.getText();

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.LOGIN_URL)
                .newBuilder()
                .addQueryParameter("name", userName)
                .addQueryParameter("isAdmin", "1")
                .build()
                .toString();

        HttpClientUtil.runGetReq(finalUrl, new Callback() {

            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {
                Platform.runLater(() ->
                        msgLabel.setText("onFailure!")
                );
            }

            @Override
            public void onResponse(@org.jetbrains.annotations.NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                switch (code){
                    case HttpServletResponse.SC_BAD_REQUEST:
                        Platform.runLater(() ->{
                            msgLabel.setText("empty " + userName);
                        });
                        break;
                    case HttpServletResponse.SC_FORBIDDEN:
                        Platform.runLater(() ->{
                            msgLabel.setText("admin is already logged in " + userName);
                        });
                        break;
                    case HttpServletResponse.SC_CONFLICT:
                        Platform.runLater(() ->{
                            msgLabel.setText("already exist " + userName);
                        });
                        break;
                    case HttpServletResponse.SC_OK:
                        Platform.runLater(() ->{
                            adminMainController.switchToDashboard(userName);
                        });
                        break;
                    default:
                        break;
                }
            }
        });

    }
    public void setAdminMainController(AdminMainController adminMainController) {
        this.adminMainController = adminMainController;
    }
}


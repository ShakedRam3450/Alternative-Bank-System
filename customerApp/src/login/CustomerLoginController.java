package login;

import dto.CustomerDTO;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.CustomerMainController;
import main.HttpClientUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.ServletUtils;

import java.io.IOException;

public class CustomerLoginController {

    private CustomerMainController customerMainController;
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
                .addQueryParameter("isAdmin", "0")
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
                    case HttpServletResponse.SC_CONFLICT:
                        Platform.runLater(() ->{
                            msgLabel.setText("already exist " + userName);
                        });
                        break;
                    case HttpServletResponse.SC_OK:
                        CustomerDTO customerDTO = Constants.GSON_INSTANCE.fromJson(response.body().string(), CustomerDTO.class);
                        Platform.runLater(() ->{
                            customerMainController.switchToDashboard(customerDTO);
                        });

                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void setCustomerMainController(CustomerMainController customerMainController) {
        this.customerMainController = customerMainController;
    }


}

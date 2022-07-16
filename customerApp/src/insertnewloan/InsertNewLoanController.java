package insertnewloan;

import dashboard.CustomerDashboardController;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.HttpClientUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;

import java.io.IOException;
import java.util.Collection;


public class InsertNewLoanController {
    @FXML private TextField idTF;
    @FXML private TextField capitalTF;
    @FXML private ComboBox<String> categoryCB;
    @FXML private TextField totalYazTimeTF;
    @FXML private TextField paysEveryYazTF;
    @FXML private TextField interestPerPaymentTF;
    @FXML private Button sendNewLoanBTN;
    @FXML private Label errorLabel;
    private String customerName;

    private CustomerDashboardController customerDashboardController;

    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @FXML
    public void sendNewLoan(ActionEvent event) {
        try {
            String id = idTF.getText();
            int capital = Integer.parseInt(capitalTF.getText());
            String category = categoryCB.getValue();
            int totalYazTime = Integer.parseInt(totalYazTimeTF.getText());
            int paysEveryYaz = Integer.parseInt(paysEveryYazTF.getText());
            int interestPerPayment = Integer.parseInt(interestPerPaymentTF.getText());

            if(id.isEmpty() || category.isEmpty()){
                errorLabel.setText("Fill all fields");
                return;
            }
            sendLoanToServer(id, capital, category, totalYazTime, paysEveryYaz, interestPerPayment);
            errorLabel.setText("");

        }catch (NumberFormatException e){
            errorLabel.setText("String was entered instead of int");
        }

    }

    private void sendLoanToServer(String id, int capital, String category, int totalYazTime, int paysEveryYaz, int interestPerPayment) {
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.INSERT_NEW_LOAN_URL)
                .newBuilder()
                .addQueryParameter("customerName", this.customerName)
                .addQueryParameter("id", id)
                .addQueryParameter("capital", String.valueOf(capital))
                .addQueryParameter("category", category)
                .addQueryParameter("totalYazTime", String.valueOf(totalYazTime))
                .addQueryParameter("paysEveryYaz", String.valueOf(paysEveryYaz))
                .addQueryParameter("interestPerPayment", String.valueOf(interestPerPayment))
                .build()
                .toString();

        HttpClientUtil.runGetReq(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("onFailure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpServletResponse.SC_NOT_ACCEPTABLE){
                    String errorMsg = response.body().string();
                    Platform.runLater(() ->{
                        errorLabel.setText(errorMsg);
                    });
                }
            }
        });
    }

    public void setCategories(Collection<String> categories){
        categoryCB.getItems().clear();
        categoryCB.getItems().addAll(categories);
    }
}

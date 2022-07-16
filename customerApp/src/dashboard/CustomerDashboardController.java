package dashboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.CustomerDTO;
import dto.LoanDTO;
import dto.NotificationDTO;
import dto.ScrambleDTO;
import exceptions.PaymentException;
import info.InfoController;
import insertnewloan.InsertNewLoanController;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.HttpClientUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import payment.PaymentController;
import scramble.ScrambleController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.CustomerMainController;
import utils.Constants;
import utils.ServletUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class CustomerDashboardController {
    @FXML private Button loadFileBTN;
    @FXML private Label viewByLabel;
    @FXML private Label userNameLabel;
    @FXML private Label filePathLabel;
    @FXML private Label balanceLabel;
    @FXML private Label timeLabel;

    private SimpleDoubleProperty balance;
    private SimpleIntegerProperty time;
    private CustomerRefresher infoRefresher;
    private Timer timer;
    private Set<String> categories;
    private int version;

    private CustomerDTO customer;
    private List<NotificationDTO> notifications;

    @FXML private ScrollPane mainComponent;

    @FXML private InfoController infoComponentController;
    @FXML private ScrollPane infoComponent;

    @FXML private ScrambleController scrambleComponentController;
    @FXML private ScrollPane scrambleComponent;

    @FXML private PaymentController paymentComponentController;
    @FXML private ScrollPane paymentComponent;

    @FXML private InsertNewLoanController insertNewLoanComponentController;
    @FXML private ScrollPane insertNewLoanComponent;

    private CustomerMainController customerMainController;

    public void setCustomerMainController(CustomerMainController customerMainController) {
        this.customerMainController = customerMainController;

    }

    public CustomerDashboardController() {
        balance = new SimpleDoubleProperty(0);
        time = new SimpleIntegerProperty(1);
        notifications = new ArrayList<>();
        categories = new HashSet<>();
        version = 1;
    }

    @FXML
    public void initialize() {
        viewByLabel.setText("Customer");
        balanceLabel.textProperty().bind(balance.asString());
        timeLabel.textProperty().bind(time.asString());

        if (infoComponentController != null && scrambleComponentController != null &&
                paymentComponentController != null && insertNewLoanComponentController != null) {
            infoComponentController.setCustomerDashboardController(this);
            scrambleComponentController.setCustomerDashboardController(this);
            paymentComponentController.setCustomerDashboardController(this);
            insertNewLoanComponentController.setCustomerDashboardController(this);
            insertNewLoanComponentController.setCustomerName(userNameLabel.getText());
        }

    }

    @FXML
    void loadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select words file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(customerMainController.getPrimaryStage());
        if (selectedFile == null)
            return;

        String absolutePath = selectedFile.getAbsolutePath();
        //customerMainController.readFile(selectedFile,absolutePath);
        sendFile(selectedFile, absolutePath);
    }
    private void sendFile(File selectedFile, String absolutePath) {
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.FILE_URL)
                .newBuilder()
                .addQueryParameter("path", absolutePath)
                .addQueryParameter("customerName", userNameLabel.getText())
                .build()
                .toString();

        HttpClientUtil.runGetReq(finalUrl, new Callback() {
            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {
                Platform.runLater(() -> {
                    filePathLabel.setText("Fail");
                });
            }

            @Override
            public void onResponse(@org.jetbrains.annotations.NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                switch (code) {
                    case HttpServletResponse.SC_NOT_ACCEPTABLE:
                        String errorMsg = response.body().string();
                        Platform.runLater(() -> {
                            showPopUpError(errorMsg);
                        });
                        break;
                    case HttpServletResponse.SC_OK:
                        Platform.runLater(() -> {
                            filePathLabel.setText(absolutePath);
                            try {
                                String jsonCustomerDTO = response.body().string();
                                CustomerDTO customerDTO = new Gson().fromJson(jsonCustomerDTO, CustomerDTO.class);
                                infoComponentController.setCustomer(customerDTO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    default:
                        break;
                }
            }
        });
    }
    public void setUsername(String name) {
        this.userNameLabel.setText(name);
    }
    public void charge(double amount){
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.TRANSFERS_URL)
                .newBuilder()
                .addQueryParameter("customerName", userNameLabel.getText())
                .addQueryParameter("actionType", "+")
                .addQueryParameter("amount", String.valueOf(amount))
                .build()
                .toString();

        System.out.println(Constants.MAIN_URL + Constants.TRANSFERS_URL);
        HttpClientUtil.runGetReq(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("OnFailure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                switch (code){
                    case HttpServletResponse.SC_FORBIDDEN:
                        System.out.println("forbidden");
                        break;
                    case HttpServletResponse.SC_OK:
                        Platform.runLater(() ->{
                            updateTransfers(response);
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }
    public void withdraw(double amount) {
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.TRANSFERS_URL)
                .newBuilder()
                .addQueryParameter("customerName", userNameLabel.getText())
                .addQueryParameter("actionType", "-")
                .addQueryParameter("amount", String.valueOf(amount))
                .build()
                .toString();

        HttpClientUtil.runGetReq(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("OnFailure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                switch (code){
                    case HttpServletResponse.SC_FORBIDDEN:
                        Platform.runLater(() -> {
                            infoComponentController.setErrorMsg("you are poor");
                        });
                        break;
                    case HttpServletResponse.SC_OK:
                        Platform.runLater(() ->{
                            updateTransfers(response);
                        });

                        break;
                    default:
                        break;
                }
            }
        });
    }
    private void updateTransfers(@NotNull Response response){

        try {
            String jsonCustomerDTO = response.body().string();
            CustomerDTO customerDTO = new Gson().fromJson(jsonCustomerDTO, CustomerDTO.class);
            infoComponentController.setCustomer(customerDTO);
            balance.set(customerDTO.getBalance());
            infoComponentController.setErrorMsg("");
        } catch (IOException e) {
                e.printStackTrace();
            }

    }
    public void scramble(int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories) {
       ScrambleDTO scrambleDTO = new ScrambleDTO(userNameLabel.getText(), amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);
       String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.SCRAMBLE_URL)
                .newBuilder()
                .build()
                .toString();

       Gson gson = Constants.GSON_INSTANCE;
       String json = gson.toJson(scrambleDTO);

       HttpClientUtil.runPostReq(finalUrl, json, new Callback() {
           @Override
           public void onFailure(@NotNull Call call, @NotNull IOException e) {
               System.out.println("OnFailure");
           }

           @Override
           public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
               Type listType = new TypeToken<List<LoanDTO>>(){}.getType();
               String json = response.body().string();
               List<LoanDTO> eligibleLoans = Constants.GSON_INSTANCE.fromJson(json, listType);
               Platform.runLater(() ->{
                   scrambleComponentController.displayEligibleLoans(eligibleLoans);
               });

           }
       });
    }
    private void showPopUpError(String errorMsg){
        Stage popUpWindow = new Stage();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Error popup - opening file");

        Label label1= new Label(errorMsg);
        Button button1= new Button("Close");

        button1.setOnAction(e -> popUpWindow.close());
        ScrollPane layout = new ScrollPane();
        VBox vbox= new VBox(20);
        vbox.getChildren().addAll(label1, button1);
        vbox.setAlignment(Pos.CENTER);
        layout.setContent(vbox);
        Scene scene1= new Scene(layout);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }
    public CustomerDTO getCustomer() {
        return customer;
    }
    public void setTime(int time) {
        this.time.set(time);
    }
    public int getTime() {
        return time.get();
    }
    public void payOnePayment(LoanDTO selectedLoan) throws Exception {

    }
    public void payAllLoan(LoanDTO selectedLoan) {

    }
    public void payDebt(LoanDTO selectedLoan, double amount) {

    }
    public void placementActivation(List<LoanDTO> selectedLoans, int amount, int maxOwnership) {
        String finalUrl = HttpUrl
                .parse(Constants.MAIN_URL + Constants.PLACEMENT_ACTIVATION_URL)
                .newBuilder()
                .addQueryParameter("customerName", userNameLabel.getText())
                .addQueryParameter("amount", String.valueOf(amount))
                .addQueryParameter("maxOwnership", String.valueOf(maxOwnership))
                .build()
                .toString();

        String selectedLoansJson = Constants.GSON_INSTANCE.toJson(selectedLoans);

        HttpClientUtil.runPostReq(finalUrl, selectedLoansJson, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("OnFailure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                CustomerDTO customerDTO = Constants.GSON_INSTANCE.fromJson(json, CustomerDTO.class);
                Platform.runLater(() -> {
                    setCustomer(customerDTO);
                });
            }
        });
    }
    public void setCustomer(CustomerDTO customer){
        this.customer = customer;
        this.balance.set(customer.getBalance());
        infoComponentController.setCustomer(customer);
        paymentComponentController.setLoansTable();
        paymentComponentController.setNotificationsTable(this.notifications);
    }
    public void startRefresher() {
        infoRefresher = new CustomerRefresher(
                this::getVersion,
                userNameLabel.getText(),
                this::setVersion,
                this::setTime,
                this::setCustomer,
                this::addNotifications,
                this::setCategories
        );
        timer = new Timer();
        timer.schedule(infoRefresher, 0, 2000);
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public int getVersion() {
        return version;
    }

    public void addNotifications(List<LoanDTO> needToPayLoans) {
        needToPayLoans.forEach((loanDTO) -> notifications.add(new NotificationDTO(loanDTO, time.get())));
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
        scrambleComponentController.setCategories(categories);
        insertNewLoanComponentController.setCategories(categories);
    }

    public Set<String> getCategories() {
        return categories;
    }
}

/*
    public void withdraw(double amount){
        if(customer.getBalance() < amount)
            System.out.println("amount is bigger than balance!");
        else
            customerMainController.withdraw(customer, amount);
    }

    public Collection<String> getCategories(){
        return customerMainController.getCategories();
    }

    public void setCategories(Set<String> categories) {
        scrambleComponentController.setCategories(categories);
    }

    public void scramble(int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories) {
        customerMainController.scramble(customer, amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);
    }

    public void displayEligibleLoans(List<LoanDTO> eligibleLoans) {
        scrambleComponentController.displayEligibleLoans(eligibleLoans);
    }

    public int getTime() {
        return customerMainController.getTime();
    }

    public String getErrorMessage(Exception e) {
        Class <?> exceptionType = e.getClass();

        if(exceptionType == OutOfRangeException.class)
            return e.toString();
        else if(exceptionType == NumberFormatException.class)
            return "String was entered";
        else if(exceptionType == PaymentException.class)
            return e.toString();

        return null;
    }*/

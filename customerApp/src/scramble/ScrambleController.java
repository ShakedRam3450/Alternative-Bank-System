package scramble;


import loans.LoansController;
import dashboard.CustomerDashboardController;
import dto.LoanDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.MasterDetailPane;
import utils.Constants;


import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScrambleController {
    @FXML private TextField amountTF;
    @FXML private TextField interestTF;
    @FXML private TextField maxNumOfLoanerLoansTF;
    @FXML private TextField yazTF;
    @FXML private TextField maxOwnershipTF;
    @FXML private CheckComboBox<String> categoriesCB;
    @FXML private Button scrambleBTN;
    @FXML private Button sendBTN;
    @FXML private LoansController loansComponentController;
    @FXML private MasterDetailPane loansComponent;
    @FXML private Label errorLabel;
    @FXML private VBox vboxBe;
    @FXML private CheckComboBox<String> loansSelectCB;
    private int amount;
    private int maxOwnership;
    private CustomerDashboardController customerDashboardController;
    private boolean isFilterMade;
    private List<LoanDTO> eligibleLoans;

    public ScrambleController(){
        isFilterMade = false;
    }
    @FXML
    public void initialize(){
        errorLabel.setStyle("-fx-text-fill: red");
    }
    @FXML
    public void scramble(ActionEvent event) {
        try {
            this.amount = StringToInt(amountTF.getText(), 0, Constants.NO_LIMIT);
            if(amount == Constants.NO_LIMIT) {
                errorLabel.setText("You didnt enter amount to invest");
                loansComponentController.clear();
                return;
            }
            /*if(amount > customerDashboardController.getCustomer().getBalance()) {
                errorLabel.setText("you dont have enough money");
                loansComponentController.clear();
                return;
            }*/
            int minInterest = StringToInt(interestTF.getText(), 0, Constants.NO_LIMIT);
            int minYaz = StringToInt(yazTF.getText(), 0, Constants.NO_LIMIT);
            int maxLoans = StringToInt(maxNumOfLoanerLoansTF.getText(), 0, Constants.NO_LIMIT);
            this.maxOwnership = StringToInt(maxOwnershipTF.getText(), 0, 101);
            ObservableList<String> chosenCategories = categoriesCB.getCheckModel().getCheckedItems();

                errorLabel.setText("");

            customerDashboardController.scramble(amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);

        } catch (Exception e) {
            errorLabel.setText("get error msg");
            //errorLabel.setText(customerDashboardController.getErrorMessage(e));
            loansComponentController.clear();
        }finally {
            cleanFields();
        }

    }
    private void cleanFields() {
        amountTF.clear();
        interestTF.clear();
        yazTF.clear();
        maxNumOfLoanerLoansTF.clear();
        maxOwnershipTF.clear();
        categoriesCB.getCheckModel().clearChecks();
    }
    private int StringToInt(String text, int low, int high) throws Exception {
        if (text.isEmpty())
            return Constants.NO_LIMIT;

        int amount = Integer.parseInt(text);

        if(high == Constants.NO_LIMIT){
            if(amount <= low)
                System.out.println("Out Of Range");//throw new OutOfRangeException();
        }
        else if(!(amount > low && amount < high))
            System.out.println("Out Of Range");//throw new OutOfRangeException();

        return amount;

    }
    public void displayEligibleLoans(List<LoanDTO> eligibleLoans) {
        errorLabel.setText("");
        isFilterMade = true;
        loansComponentController.displayLoans(eligibleLoans);
        loansComponentController.addSelectCol();
        loansSelectCB.getItems().clear();
        eligibleLoans.forEach((loanDTO -> loansSelectCB.getItems().add(loanDTO.getId())));
        this.eligibleLoans = eligibleLoans;
    }

    @FXML
    public void placementActivation(ActionEvent event){
        if(isFilterMade){
            List<String> selectedLoansIds = loansSelectCB.getCheckModel().getCheckedItems();
            List<LoanDTO> selectedLoans = this.eligibleLoans.stream().filter((loanDTO) -> selectedLoansIds.contains(loanDTO.getId())).collect(Collectors.toList());
            if(selectedLoans.isEmpty()){
                errorLabel.setText("you didnt choose loans");
                return;
            }
            customerDashboardController.placementActivation(selectedLoans, amount, maxOwnership);
            errorLabel.setText("");
            loansComponentController.clear();
        }
    }

    public void setCategories(Collection<String> categories){
        categoriesCB.getItems().clear();
        categoriesCB.getItems().addAll(categories);
    }


    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }


}

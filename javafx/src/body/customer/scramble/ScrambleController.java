package body.customer.scramble;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.LoanDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import loan.Loan;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.MasterDetailPane;
import resources.Resources;
import sun.plugin2.util.ColorUtil;

import java.util.Collection;
import java.util.List;

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
    private int amount;
    private int maxOwnership;
    private CustomerBodyController customerBodyController;
    private boolean isFilterMade;

    public ScrambleController(){
        isFilterMade = false;
    }
    @FXML
    public void initialize(){

    }
    @FXML
    public void scramble(ActionEvent event) {
        try {
            this.amount = StringToInt(amountTF.getText(), 0, Resources.NO_LIMIT);
            if(amount == Resources.NO_LIMIT) {
                errorLabel.setText("you didnt enter amount to invest");
                return;
            }
            if(amount > customerBodyController.getCustomer().getBalance()) {
                errorLabel.setText("you dont have enough money");
                return;
            }
            int minInterest = StringToInt(interestTF.getText(), 0, Resources.NO_LIMIT);
            int minYaz = StringToInt(yazTF.getText(), 0, Resources.NO_LIMIT);
            int maxLoans = StringToInt(maxNumOfLoanerLoansTF.getText(), 0, Resources.NO_LIMIT);
            this.maxOwnership = StringToInt(maxOwnershipTF.getText(), 0, 101);
            ObservableList<String> chosenCategories = categoriesCB.getCheckModel().getCheckedItems();

            customerBodyController.scramble(amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);

        } catch (Exception e) {
            errorLabel.setText("not in range or string");
        }finally {
            cleanFields();
        }
    }
    @FXML
    public void placementActivation(ActionEvent event){
        if(isFilterMade){
            List<LoanDTO> selectedLoans = loansComponentController.getSelectedLoans();
            customerBodyController.placementActivation(selectedLoans, amount, maxOwnership);
            loansComponentController.clear();
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
            return Resources.NO_LIMIT;
        int amount = Integer.parseInt(text);
        if(high == Resources.NO_LIMIT){
            if(amount <= low)
                throw new Exception();
        }
        else if(!(amount > low && amount < high))
            throw new Exception();
        return amount;

    }

    public void setCustomerBodyController(CustomerBodyController customerBodyController) {
        this.customerBodyController = customerBodyController;
    }
    public void setCategories(Collection<String> categories){
        categoriesCB.getItems().clear();
        categoriesCB.getItems().addAll(categories);
    }

    public void displayEligibleLoans(List<LoanDTO> eligibleLoans) {
        errorLabel.setText("");
        isFilterMade = true;
        loansComponentController.displayLoans(eligibleLoans);
        loansComponentController.addSelectCol();
    }
}

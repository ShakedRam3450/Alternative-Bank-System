package body.customer.scramble;

import body.customer.CustomerBodyController;
import body.loans.LoansController;
import dto.LoanDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import loan.Loan;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.MasterDetailPane;
import resources.Resources;
import sun.plugin2.util.ColorUtil;
import task.ScrambleTask;
import ui.exceptions.OutOfRangeException;

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
    @FXML private VBox vboxBe;
    private int amount;
    private int maxOwnership;
    private CustomerBodyController customerBodyController;
    private boolean isFilterMade;

    /*@FXML private Label taskLabel;
    @FXML private ProgressBar taskProgressBar;*/

    public ScrambleController(){
        isFilterMade = false;
    }
    @FXML
    public void initialize(){
        errorLabel.setStyle("-fx-text-fill: red");
        vboxBe.getStyleClass().add("be2");
        //taskLabel.setText("not loaded yet");

    }
    @FXML
    public void scramble(ActionEvent event) {
        try {
            this.amount = StringToInt(amountTF.getText(), 0, Resources.NO_LIMIT);
            if(amount == Resources.NO_LIMIT) {
                errorLabel.setText("You didnt enter amount to invest");
                loansComponentController.clear();
                return;
            }
            if(amount > customerBodyController.getCustomer().getBalance()) {
                errorLabel.setText("you dont have enough money");
                loansComponentController.clear();
                return;
            }
            int minInterest = StringToInt(interestTF.getText(), 0, Resources.NO_LIMIT);
            int minYaz = StringToInt(yazTF.getText(), 0, Resources.NO_LIMIT);
            int maxLoans = StringToInt(maxNumOfLoanerLoansTF.getText(), 0, Resources.NO_LIMIT);
            this.maxOwnership = StringToInt(maxOwnershipTF.getText(), 0, 101);
            ObservableList<String> chosenCategories = categoriesCB.getCheckModel().getCheckedItems();

                errorLabel.setText("");

            customerBodyController.scramble(amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);

        } catch (Exception e) {
            errorLabel.setText(customerBodyController.getErrorMessage(e));
            loansComponentController.clear();
        }finally {
            cleanFields();
        }
    }
    @FXML
    public void placementActivation(ActionEvent event){
        if(isFilterMade){
            List<LoanDTO> selectedLoans = loansComponentController.getSelectedLoans();
            if(selectedLoans.isEmpty()){
                errorLabel.setText("you didnt choose loans");
                return;
            }
            customerBodyController.placementActivation(selectedLoans, amount, maxOwnership);
            errorLabel.setText("");
            loansComponentController.clear();
        }
    }

   /* public Label getTaskLabel() {
        return taskLabel;
    }

    public ProgressBar getTaskProgressBar() {
        return taskProgressBar;
    }*/

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
                throw new OutOfRangeException();
        }
        else if(!(amount > low && amount < high))
            throw new OutOfRangeException();

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

   /* public void bindForTask(Task<List<LoanDTO>> task) {
        taskLabel.textProperty().bind(task.messageProperty());
        taskProgressBar.progressProperty().bind(task.progressProperty());
    }*/
}

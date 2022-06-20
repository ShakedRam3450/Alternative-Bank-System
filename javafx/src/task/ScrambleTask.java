package task;

import bank.Bank;
import com.sun.deploy.association.utility.AppUtility;
import dto.CustomerDTO;
import dto.LoanDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScrambleTask extends Task<List<LoanDTO>> {
    private Bank bank;
    private CustomerDTO customer;
    private int amount, minInterest, minYaz, maxLoans, maxOwnership;
    private ObservableList<String> chosenCategories;
    private List<LoanDTO> eligibleLoans;
    private Label progressText;
    private ProgressBar progressBar;

    public ScrambleTask(Bank bank, CustomerDTO customer, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories, Label text, ProgressBar progressBar){
        this.bank = bank;
        this.customer = customer;
        this.amount = amount;
        this.minInterest = minInterest;
        this.minYaz = minYaz;
        this.maxLoans = maxLoans;
        this.maxOwnership = maxOwnership;
        this.chosenCategories = chosenCategories;
        this.progressText = text;
        this.progressBar = progressBar;

    }

    @Override
    protected List<LoanDTO> call() throws Exception {
        try{
            this.eligibleLoans = bank.getEligibleLoans(customer, amount, minInterest, minYaz, maxLoans, maxOwnership, chosenCategories);

            updateMessage("Loading...");
            updateProgress(20,100);
            Platform.runLater(() -> {
                progressText.setText("Loading...");
            });
            Thread.sleep(500);

            updateMessage("check customer...");
            updateProgress(40, 100);
            Platform.runLater(() -> {
                progressText.setText("check customer...");
            });
            Thread.sleep(500);

            updateMessage("check filters...");
            updateProgress(60, 100);
            Platform.runLater(() -> {
                progressText.setText("check filters...");
            });
            Thread.sleep(500);

            updateMessage("finding loans...");
            updateProgress(80, 100);
            Platform.runLater(() -> {
                progressText.setText("finding loans...");
            });
            Thread.sleep(500);

            updateMessage("Done!");
            updateProgress(100, 100);
            Platform.runLater(() -> {
                progressText.setText("Done!");
            });
            Thread.sleep(500);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eligibleLoans;
    }
}

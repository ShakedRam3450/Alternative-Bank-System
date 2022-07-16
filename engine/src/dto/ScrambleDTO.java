package dto;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ScrambleDTO implements Serializable {
    private String customerName;
    private int amount;
    private int minInterest;
    private int minYaz;
    private int maxLoans;
    private int maxOwnership;
    private Set<String> chosenCategories;

    public ScrambleDTO(String customerName, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories){
        this.customerName = customerName;
        this.amount = amount;
        this.minInterest = minInterest;
        this.minYaz = minYaz;
        this.maxLoans = maxLoans;
        this.maxOwnership = maxOwnership;
        this.chosenCategories = new HashSet<>(chosenCategories);
    }

    public int getAmount() {
        return amount;
    }

    public int getMaxLoans() {
        return maxLoans;
    }

    public int getMaxOwnership() {
        return maxOwnership;
    }

    public int getMinInterest() {
        return minInterest;
    }

    public int getMinYaz() {
        return minYaz;
    }

    public Set<String> getChosenCategories() {
        return chosenCategories;
    }

    public String getCustomerName() {
        return customerName;
    }

}

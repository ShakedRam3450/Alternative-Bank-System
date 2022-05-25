package dto;

import customer.Customer;
import customer.Investor;
import javafx.scene.control.CheckBox;
import loan.Loan;
import loan.Payment;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoanDTO {
    private String id;
    private String ownerName;
    private String category;
    private int capital, totalYazTime, paysEveryYaz, startTime, interestPerPayment;
    private Loan.Status status;
    private Map<String, InvestorDTO> investors;
    private int amountRaised, amountRemaining; //PENDING
    private int nextPaymentTime; //ACTIVE
    private double debt; //IN_RISK
    private int endTime; //FINISHED
    private Map<Integer, PaymentDTO> payments; //key = yazTime
    private CheckBox select;

    public LoanDTO(Loan loan){
        this.id = loan.getId();
        this.ownerName = loan.getOwner().getName();
        this.category = loan.getCategory();
        this.capital = loan.getCapital();
        this.totalYazTime = loan.getTotalYazTime();
        this.paysEveryYaz = loan.getPaysEveryYaz();
        this.startTime = loan.getStartTime();
        this.endTime = loan.getEndTime();
        this.interestPerPayment = loan.getInterestPerPayment();
        this.status = loan.getStatus();
        this.amountRaised = loan.getAmountRaised();
        this.amountRemaining = loan.getAmountRemaining();
        this.nextPaymentTime = loan.getNextPaymentTime();
        this.debt = loan.getDebt();
        investorsToInvestorsDTO(loan.getInvestors());
        paymentsToPaymentsDTO(loan.getPayments());
        this.select = new CheckBox();
    }

    public CheckBox getSelect() {
        return select;
    }

    private void investorsToInvestorsDTO(Map<String, Investor> originalInvestors){
        Map<String, InvestorDTO> res = new HashMap<>();
        for(String key: originalInvestors.keySet()){
            res.put(key, new InvestorDTO(originalInvestors.get(key)));
        }
        this.investors = res;
    }
    private void paymentsToPaymentsDTO(Map<Integer, Payment> originalPayments){
        Map<Integer, PaymentDTO> res = new HashMap<>();
        for(Integer key: originalPayments.keySet()){
            res.put(key, new PaymentDTO(originalPayments.get(key)));
        }
        this.payments = res;
    }
    @Override
    public String toString() {
        return  "id=" + id +
                ", owner=" + ownerName +
                ", category='" + category + '\'' +
                ", capital=" + capital +
                ", totalYazTime=" + totalYazTime +
                ", paysEveryYaz=" + paysEveryYaz +
                ", startTime=" + startTime +
                ", interestPerPayment=" + interestPerPayment +
                ", status=" + status +
                ", investors=" + investors +
                ", amountRaised=" + amountRaised +
                ", amountRemaining=" + amountRemaining +
                ", nextPaymentTime=" + nextPaymentTime +
                ", debt=" + debt +
                ", endTime=" + endTime +
                ", payments=" + payments;
    }
    public String getId() {
        return id;
    }

    public int getPaysEveryYaz() {
        return paysEveryYaz;
    }

    public int getTotalYazTime() {
        return totalYazTime;
    }

    public int getCapital() {
        return capital;
    }

    public String getCategory() {
        return category;
    }

    public int getInterestPerPayment() {
        return interestPerPayment;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Loan.Status getStatus() {
        return status;
    }

    public Map<String, InvestorDTO> getInvestors() {
        return investors;
    }

    public Map<Integer, PaymentDTO> getPayments() {
        return payments;
    }

    public double getDebt() {
        return debt;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getAmountRemaining() {
        return amountRemaining;
    }

    public int getAmountRaised() {
        return amountRaised;
    }

    public int getNextPaymentTime() {
        return nextPaymentTime;
    }
    public int getLastPaymentTime(){
        if(payments.isEmpty())
            return 0;
        return Collections.max(payments.keySet());
    }
    public double getOnePaymentAmount(){
        return getCapitalPart() + getInterestPart();
    }
    public double getCapitalPart(){
        return ((double)capital / totalYazTime) * paysEveryYaz;
    }
    public double getInterestPart(){
        return ((double)interestPerPayment / 100) * getCapitalPart();
    }
    public int getNumberOfUnpaidPayments(){
        return (int) (debt/getOnePaymentAmount());
    }
    public double getTotalCapitalPaid(){
        double res = 0;
        for (Integer key: payments.keySet())
            res += payments.get(key).getCapitalPart();
        return res;
    }
    public double getTotalInterestPaid(){
        double res = 0;
        for (Integer key: payments.keySet())
            res += payments.get(key).getInterestPart();
        return res;
    }
    public double getTotalCapitalRemaining(){
        return capital - getTotalCapitalPaid();
    }
    public double getTotalInterestRemaining(){
        return (getInterestPart() * (totalYazTime/paysEveryYaz)) - getTotalInterestPaid();
    }
}

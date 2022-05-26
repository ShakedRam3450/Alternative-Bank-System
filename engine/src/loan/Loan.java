package loan;

import customer.Customer;
import customer.Investor;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static loan.Loan.Status.*;

public class Loan {
    private String id;
    private Customer owner;
    private String category;
    private int capital, totalYazTime, paysEveryYaz, startTime, interestPerPayment;
    private Status status;
    private Map<String, Investor> investors;
    private int amountRaised, amountRemaining; //PENDING
    private int nextPaymentTime; //ACTIVE
    private double debt; //IN_RISK
    private int endTime; //FINISHED
    private Map<Integer, Payment> payments; //key = yazTime

    public enum Status{
        NEW, PENDING, ACTIVE, IN_RISK, FINISHED;
    }
    public Loan(String id, Customer owner, String category, int capital, int totalYazTime, int paysEveryYaz, int interestPerPayment){
        this.id = id;
        this.owner = owner;
        this.category = category;
        this.capital = capital;
        this.totalYazTime = totalYazTime;
        this.paysEveryYaz = paysEveryYaz;
        this.interestPerPayment = interestPerPayment;
        this.status = NEW;
        this.investors = new HashMap<>();
        this.amountRemaining = capital;
        this.payments = new HashMap<>();
        this.debt = 0;
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

    public String getId() {
        return id;
    }
    public int getTotalYazTime(){
        return totalYazTime;
    }
    public int getInterestPerPayment() {
        return interestPerPayment;
    }
    public int getCapital() {
        return capital;
    }
    public Customer getOwner() {
        return owner;
    }
    public String getCategory() {
        return category;
    }
    public int getAmountRemaining() {
        return amountRemaining;
    }
    public Status getStatus() {
        return status;
    }
    public int getStartTime() {
        return startTime;
    }

    public Map<Integer, Payment> getPayments() {
        return payments;
    }

    public Map<String, Investor> getInvestors() {
        return investors;
    }

    public double getDebt() {
        return debt;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getNextPaymentTime() {
        return nextPaymentTime;
    }

    public int getAmountRaised() {
        return amountRaised;
    }

    public int getPaysEveryYaz() {
        return paysEveryYaz;
    }

    public int getLastPaymentTime(){
        if(payments.isEmpty())
            return 0;
        return Collections.max(payments.keySet());
    }

    public int getNumberOfUnpaidPayments(){
        return (int) (debt/getOnePaymentAmount());
    }
    public void invest(Customer investor, int amount, int currentTime){
        amountRemaining -= amount;
        amountRaised += amount;
        if(status == NEW)
            status = PENDING;
        if(amountRemaining == 0){ //Status = PENDING
            makeLoanActive(currentTime);
        }
        double partInInvestment = (double) amount / capital;
        investors.put(investor.getName(), new Investor(investor, partInInvestment));
    }
    private void makeLoanActive(int startTime){
        this.status = ACTIVE;
        this.startTime = startTime;
        owner.deposit(capital, startTime);
    }
    public boolean isTimeToPay(int curTime){
        return (curTime - startTime) % paysEveryYaz == 0;
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
    public boolean pay(int time){
        if(!owner.withdrawal(getOnePaymentAmount() + debt, time)){ //failed to pay
            status = IN_RISK;
            debt += getOnePaymentAmount();
            return false;
        }
        //succeeded to pay
        payments.put(time, new Payment(time, getCapitalPart() * (getNumberOfUnpaidPayments() + 1), getInterestPart() * (getNumberOfUnpaidPayments() + 1),  getOnePaymentAmount() + debt));
        payInvestors(time);
        debt = 0;
        status = ACTIVE;

        if(isLoanFinished()){
            status = FINISHED;
            endTime = time;
        }
        return true;
    }
    private boolean isLoanFinished(){
        double totalNeedToPay = getOnePaymentAmount() * (totalYazTime / paysEveryYaz);
        double totalPaid = 0;
        for (Payment payment: payments.values())
            totalPaid += payment.getTotalAmount();
        return totalNeedToPay == totalPaid;
    }
    private void payInvestors(int time){
        investors.values().forEach(investor -> investor.receiveMoney(time, getOnePaymentAmount() + debt));
        /*for (Investor investor: investors.values()){
            investor.receiveMoney(time, getOnePaymentAmount() + debt);
        }*/
    }
    public void activeToInRisk() {
        status = IN_RISK;
        debt += getOnePaymentAmount();
    }

    public boolean isLateToPay(int curTime) {
        if ((curTime - startTime) % paysEveryYaz == 1 && curTime - startTime != 1) {
            if (getLastPaymentTime() != curTime - 1)
                return true;
        }
        return false;
    }
}

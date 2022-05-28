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
    public Loan.Status getLastPaymentStatus() {
        if (payments.isEmpty())
            return null;
        return payments.get(Collections.max(payments.keySet())).getStatus();
    }
    public int getNumberOfUnpaidPayments(){
         return (int) Math.ceil(debt/getOnePaymentAmount());
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
    public boolean pay(double amount, double capitalPart, double interestPart, int time){
        if(!owner.withdrawal(amount, time)){ //failed to pay
            return false;
        }
        //succeeded to pay
        if(payments.containsKey(time)){
            payments.get(time).addToPayment(capitalPart, interestPart,  amount, ACTIVE);
        }
        else
            payments.put(time, new Payment(time, capitalPart, interestPart,  amount, ACTIVE));

        payInvestors(amount, time);

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
    private void payInvestors(double amount, int time){
        investors.values().forEach(investor -> investor.receiveMoney(time, amount));

    }
    public void missedPayment() {
        status = IN_RISK;
        debt += getOnePaymentAmount();
    }
    public boolean isLateToPay(int curTime) {
        //if yesterday need to pay AND last payment was not yesterday AND yesterday was not start time
        return isTimeToPay(curTime - 1) && getLastPaymentTime() != curTime - 1 && startTime != curTime -1;
    }
    public void payDebt(double amount, int time) {
        int numberOfUnpaidPayments= getNumberOfUnpaidPayments();

        if(payments.containsKey(time))
            payments.get(time).addToTotalAmount(amount);
        else
            payments.put(time, new Payment(time, numberOfUnpaidPayments * getCapitalPart(), numberOfUnpaidPayments * getInterestPart(),  amount, IN_RISK));

        payInvestors(amount, time);

        debt -= amount;

        if(debt == 0)
            status = ACTIVE;


    }
}

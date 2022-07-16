package customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loan.Loan;

import static customer.Transfer.TransferType.DEPOSIT;
import static customer.Transfer.TransferType.WITHDRAWAL;

public class Customer {
    private double balance;
    private Map<Integer, List<Transfer>> transfers;
    private String name;
    private Map<String, Loan> borrowerLoans;
    private Map<String, Loan> lenderLoans;

    public Customer(){
        balance = 0;
        transfers = new HashMap<Integer, List<Transfer>>();
        borrowerLoans = new HashMap<String, Loan>();
        lenderLoans = new HashMap<String, Loan>();
    }
    public Customer(int balance, String name){
        this.balance = balance;
        this.name = name;
        transfers = new HashMap<Integer, List<Transfer>>();
        borrowerLoans = new HashMap<String, Loan>();
        lenderLoans = new HashMap<String, Loan>();
    }


    public String getName(){
        return name;
    }
    public double getBalance(){
        return balance;
    }
    public Map<Integer, List<Transfer>> getTransfers(){
        return transfers;
    }

    public Map<String, Loan> getBorrowerLoans() {
        return borrowerLoans;
    }

    public Map<String, Loan> getLenderLoans() {
        return lenderLoans;
    }

    public void deposit(double amount, int time){
        balance += amount;
        if(!transfers.containsKey(time))
            transfers.put(time, new ArrayList<>());
        transfers.get(time).add(new Transfer(time, amount, balance - amount, balance, DEPOSIT));
    }
    public boolean withdrawal(double amount, int time){
        if(amount > balance)
            return false;

        balance -= amount;
        if(!transfers.containsKey(time))
            transfers.put(time, new ArrayList<>());
        transfers.get(time).add(new Transfer(time, amount, balance + amount, balance, WITHDRAWAL));
        return true;
    }
    public void addBorrowerLoan(Loan loan){
        borrowerLoans.put(loan.getId(), loan);
    }
    public void addLenderLoans(List<Loan> loans){
        for(Loan loan: loans)
            lenderLoans.put(loan.getId(), loan);
    }
    public int getNumOfBorrowerLoans(){
        return borrowerLoans.size();
    }
}

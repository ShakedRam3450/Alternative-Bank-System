package exceptions;

public class NegativeBalanceException extends Exception{
    private String name;
    private double balance;

    public NegativeBalanceException(String name, double balance){
        this.balance = balance;
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }
}

package loan;

public class Payment {
    private int time;
    private double capitalPart;
    private double interestPart;
    private double totalAmount;
    private Loan.Status status;

    public Payment(int time, double capitalPart, double interestPart, double totalAmount, Loan.Status status){
        this.time = time;
        this.capitalPart = capitalPart;
        this.interestPart = interestPart;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "time=" + time +
                ", capitalPart=" + capitalPart +
                ", interestPart=" + interestPart +
                ", totalAmount=" + totalAmount + "\n";
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public double getCapitalPart() {
        return capitalPart;
    }

    public double getInterestPart() {
        return interestPart;
    }

    public int getTime() {
        return time;
    }

    public Loan.Status getStatus() {
        return status;
    }

    public void addToPayment(double capitalPart, double interestPart, double amount, Loan.Status status) {
        this.status = status;
        this.totalAmount += amount;
        this.capitalPart += capitalPart;
        this.interestPart += interestPart;
    }
}

package loan;

public class Payment {
    private int time;
    private double capitalPart;
    private double interestPart;
    private double totalAmount;

    public Payment(int time,double capitalPart,double interestPart,double totalAmount){
        this.time = time;
        this.capitalPart = capitalPart;
        this.interestPart = interestPart;
        this.totalAmount = totalAmount;
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
}

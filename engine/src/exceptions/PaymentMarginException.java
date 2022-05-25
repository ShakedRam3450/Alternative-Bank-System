package exceptions;

public class PaymentMarginException extends Exception{
    private int totalYazTime;
    private int paysEveryYaz;

    public PaymentMarginException(int totalYazTime, int paysEveryYaz){
        this.paysEveryYaz = paysEveryYaz;
        this.totalYazTime = totalYazTime;
    }
    public int getTotalYazTime(){
        return totalYazTime;
    }
    public int getPaysEveryYaz(){
        return paysEveryYaz;
    }
}

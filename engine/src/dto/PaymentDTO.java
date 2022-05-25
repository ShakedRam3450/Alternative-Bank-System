package dto;

import loan.Payment;

public class PaymentDTO {
    private int time;
    private double capitalPart;
    private double interestPart;
    private double totalAmount;

    public PaymentDTO(Payment payment){
        this.time =payment.getTime();
        this.capitalPart = payment.getCapitalPart();
        this.interestPart = payment.getInterestPart();
        this.totalAmount = payment.getTotalAmount();
    }
    public double getTotalAmount(){
        return totalAmount;
    }

    public int getTime() {
        return time;
    }

    public double getInterestPart() {
        return interestPart;
    }

    public double getCapitalPart() {
        return capitalPart;
    }
}

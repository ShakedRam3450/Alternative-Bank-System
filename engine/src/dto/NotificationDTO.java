package dto;

public class NotificationDTO {
    private LoanDTO loan;
    private int time;
    private String loanId;
    private double onePaymentAmount;

    public NotificationDTO(LoanDTO loan, int time){
        this.loan = loan;
        this.time = time;
        this.loanId = loan.getId();
        this.onePaymentAmount = loan.getOnePaymentAmount();
    }

    public int getTime() {
        return time;
    }

    public LoanDTO getLoan() {
        return loan;
    }

    public String getLoanId() {
        return loanId;
    }

    public double getOnePaymentAmount() {
        return onePaymentAmount;
    }
}

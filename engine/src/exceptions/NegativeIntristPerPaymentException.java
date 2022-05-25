package exceptions;

public class NegativeIntristPerPaymentException extends Exception{
    private String loanId;
    private int IntristPerPayment;

    public NegativeIntristPerPaymentException(String loanId, int IntristPerPayment){
        this.IntristPerPayment = IntristPerPayment;
        this.loanId = loanId;
    }

    public String getLoanId() {
        return loanId;
    }

    public int getIntristPerPayment() {
        return IntristPerPayment;
    }
}

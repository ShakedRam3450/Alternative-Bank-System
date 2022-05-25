package exceptions;

public class NegativePaysEveryYazException extends Exception{
    private String loanId;
    private int paysEveryYaz;

    public NegativePaysEveryYazException(String loanId, int paysEveryYaz){
        this.paysEveryYaz = paysEveryYaz;
        this.loanId = loanId;
    }

    public String getLoanId() {
        return loanId;
    }

    public int getPaysEveryYaz() {
        return paysEveryYaz;
    }
}

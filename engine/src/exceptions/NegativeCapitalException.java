package exceptions;

public class NegativeCapitalException extends Exception {
    private String loanId;
    private int capital;

    public NegativeCapitalException(String loanId, int capital){
        this.capital = capital;
        this.loanId = loanId;
    }

    public int getCapital() {
        return capital;
    }

    public String getLoanId() {
        return loanId;
    }
}

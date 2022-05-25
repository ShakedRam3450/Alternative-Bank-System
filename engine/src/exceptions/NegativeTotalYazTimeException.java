package exceptions;

public class NegativeTotalYazTimeException extends Exception{
    private String loanId;
    private int totalYazTime;

    public NegativeTotalYazTimeException(String loanId, int totalYazTime){
        this.totalYazTime = totalYazTime;
        this.loanId = loanId;
    }

    public String getLoanId() {
        return loanId;
    }

    public int getTotalYazTime() {
        return totalYazTime;
    }
}

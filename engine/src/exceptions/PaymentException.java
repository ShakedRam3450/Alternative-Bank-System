package exceptions;

public class PaymentException extends Exception{
    private String msg;

    public PaymentException(String msg){
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}

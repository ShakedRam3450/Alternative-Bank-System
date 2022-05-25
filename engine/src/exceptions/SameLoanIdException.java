package exceptions;

public class SameLoanIdException extends Exception{
    private String id;
    public SameLoanIdException(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

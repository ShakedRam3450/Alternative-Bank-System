package exceptions;

public class SameCustomerNameException extends Exception{
    private String name;

    public SameCustomerNameException(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}

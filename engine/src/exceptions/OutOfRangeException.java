package exceptions;

public class OutOfRangeException extends Exception{

    public OutOfRangeException(){}

    @Override
    public String toString() {
        return "Out of range number was entered";
    }
}

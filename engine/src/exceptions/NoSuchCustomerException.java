package exceptions;

import generated.AbsCustomer;

import java.util.ArrayList;
import java.util.List;

public class NoSuchCustomerException extends Exception{
    private String invalidCustomer;
    private List<String> customersNames;

    public NoSuchCustomerException(String invalidCustomer, List<AbsCustomer> customers){
        this.invalidCustomer = invalidCustomer;
        customersNames = new ArrayList<>();

        for (AbsCustomer customer: customers)
            customersNames.add(customer.getName().trim());
    }
    public String getInvalidCustomer(){
        return invalidCustomer;
    }
    public List<String> getCustomersNames(){
        return customersNames;
    }
}

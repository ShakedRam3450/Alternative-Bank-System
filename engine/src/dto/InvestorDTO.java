package dto;

import customer.Customer;
import customer.Investor;

public class InvestorDTO {
    private String name;
    private double partInInvestment; //number between 0 and 1

    public InvestorDTO(Investor investor){
        this.name = investor.getCustomer().getName();
        this.partInInvestment = investor.getPartInInvestment();
    }

    public String getName() {
        return name;
    }

    public double getPartInInvestment() {
        return partInInvestment;
    }
}

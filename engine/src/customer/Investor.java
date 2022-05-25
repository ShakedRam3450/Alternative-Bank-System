package customer;

public class Investor {
   private Customer customer;
   private double partInInvestment; //number between 0 and 1

   public Investor(Customer customer, double partInInvestment){
       this.customer = customer;
       this.partInInvestment = partInInvestment;
   }

    public Customer getCustomer() {
        return customer;
    }
    public String getName(){
       return customer.getName();
    }
    public double getPartInInvestment() {
        return partInInvestment;
    }

    public void receiveMoney(int time, double totalLoanAmount){
        customer.deposit(totalLoanAmount * partInInvestment, time);
   }
}

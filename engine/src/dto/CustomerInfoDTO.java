package dto;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CustomerInfoDTO {
    private CustomerDTO customer;
    private List<LoanDTO> needToPayLoans;
    private int version;
    private int time;
    private Set<String> categories;

    public CustomerInfoDTO(CustomerDTO customer, List<LoanDTO> needToPayLoans, int version, int time, Set<String> categories){
        this.customer = customer;
        this.needToPayLoans = needToPayLoans;
        this.version = version;
        this.time = time;
        this.categories = categories;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public List<LoanDTO> getNeedToPayLoans() {
        return needToPayLoans;
    }

    public int getTime() {
        return time;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public int getVersion() {
        return version;
    }
}

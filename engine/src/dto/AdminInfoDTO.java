package dto;

import java.util.List;
import java.util.Map;

public class AdminInfoDTO {
    Map<String, CustomerDTO> customers;
    List<LoanDTO> loans;

    public AdminInfoDTO(Map<String, CustomerDTO> customers, List<LoanDTO> loans){
        this.customers = customers;
        this.loans = loans;
    }

    public List<LoanDTO> getLoans() {
        return loans;
    }

    public Map<String, CustomerDTO> getCustomers() {
        return customers;
    }
}

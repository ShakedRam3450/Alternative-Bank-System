package dto;

import customer.Customer;
import customer.Transfer;
import javafx.beans.property.SimpleDoubleProperty;
import loan.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDTO {
    private SimpleDoubleProperty balance;
    private String name;
    private Map<Integer, List<TransferDTO>> transfers;
    private Map<String, LoanDTO> borrowerLoans;
    private int numOfBorrowerLoans;
    private Map<String, LoanDTO> lenderLoans;
    private int numOfLenderLoans;

    public CustomerDTO(Customer customer){
        name = customer.getName();
        balance = new SimpleDoubleProperty();
        balance.set(customer.getBalance());
        transfersToTransfersDTO(customer);
        loansToLoansDTO(customer);
        numOfBorrowerLoans = borrowerLoans.size();
        numOfLenderLoans = lenderLoans.size();
    }
    @Override
    public String toString() {
        return "name=" + name +
                ", balance=" + balance +
                ", transfers=" + transfers +
                ", borrowerLoans=" + borrowerLoans +
                ", lenderLoans=" + lenderLoans;
    }

    private void transfersToTransfersDTO(Customer customer){
        Map<Integer, List<Transfer>> originalTransfers = customer.getTransfers();
        Map<Integer, List<TransferDTO>> res = new HashMap<>();
        List<Transfer> tmpTransferList;

        for (Integer key: originalTransfers.keySet()){
            tmpTransferList = originalTransfers.get(key);
            res.put(key, new ArrayList<>());
            for (Transfer transfer: tmpTransferList)
                res.get(key).add(new TransferDTO(transfer));
        }

        this.transfers = res;
    }
    private void loansToLoansDTO(Customer customer){
        Map<String, Loan> originalBorrowerLoans = customer.getBorrowerLoans();
        Map<String, LoanDTO> resBorrower = new HashMap<>();

        Map<String, Loan> originalLenderLoans = customer.getLenderLoans();
        Map<String, LoanDTO> resLender = new HashMap<>();

        for (String key: originalBorrowerLoans.keySet()){
            resBorrower.put(key, new LoanDTO(originalBorrowerLoans.get(key)));
        }
        for (String key: originalLenderLoans.keySet()){
            resLender.put(key, new LoanDTO(originalLenderLoans.get(key)));
        }

        this.borrowerLoans = resBorrower;
        this.lenderLoans = resLender;
    }

    public String getName(){
        return name;
    }

    public double getBalance() {
        return balance.get();
    }
    public SimpleDoubleProperty getBalanceProperty(){
        return balance;
    }

    public Map<String, LoanDTO> getBorrowerLoans() {
        return borrowerLoans;
    }

    public Map<String, LoanDTO> getLenderLoans() {
        return lenderLoans;
    }

    public Map<Integer, List<TransferDTO>> getTransfers() {
        return transfers;
    }

    public int getNumOfBorrowerLoans() {
        return numOfBorrowerLoans;
    }

    public int getNumOfLenderLoans() {
        return numOfLenderLoans;
    }
}

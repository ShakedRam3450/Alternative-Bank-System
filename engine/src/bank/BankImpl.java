package bank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import customer.Customer;
import dto.CustomerDTO;
import dto.LoanDTO;
import exceptions.*;
import file.FileChecker;
import generated.AbsCustomer;
import generated.AbsDescriptor;
import generated.AbsLoan;
import javafx.collections.ObservableList;
import loan.Loan;
import javax.annotation.Resources;
import javax.xml.bind.JAXBException;

import static loan.Loan.Status.*;

public class BankImpl implements Bank {
    private boolean fileExist;
    private int time;
    private Map<String, Customer> customers;
    private Map<String, Loan> newLoans;
    private Map<String, Loan> pendingLoans;
    private Map<String, Loan> activeLoans;
    private Map<String, Loan> inRiskLoans;
    private Map<String, Loan> finishedLoans;
    private Set<String> categories;

    public BankImpl(){
        fileExist = false;
        time = 1;
        categories = new HashSet<String>();
        customers = new HashMap<String, Customer>();
        newLoans = new HashMap<String, Loan>();
        pendingLoans = new HashMap<String, Loan>();
        activeLoans = new HashMap<String, Loan>();
        inRiskLoans = new HashMap<String, Loan>();
        finishedLoans = new HashMap<String, Loan>();
    }
    public boolean isFileExist(){
        return fileExist;
    }
    public int getTime(){
        return time;
    }
    public void readFile(File file) throws FileNotFoundException, FileNotXMLException, JAXBException, NoSuchCategoryException, NoSuchCustomerException, PaymentMarginException, SameCustomerNameException, SameLoanIdException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativeBalanceException, NegativePaysEveryYazException, NegativeCapitalException {
        AbsDescriptor absDescriptor = null;
        FileChecker fileChecker = new FileChecker();
        InputStream in = new FileInputStream(file);
        absDescriptor = fileChecker.openFile(in);
        fileChecker.checkXMLContent(absDescriptor);
        fromJAXBObjToBank(absDescriptor);
    }
    private void fromJAXBObjToBank(AbsDescriptor absDescriptor){
        cleanMyBank();
        takeCategories(absDescriptor.getAbsCategories().getAbsCategory());
        takeCustomers(absDescriptor.getAbsCustomers().getAbsCustomer());
        takeLoans(absDescriptor.getAbsLoans().getAbsLoan());
        time = 1;
        fileExist = true;
    }
    private void cleanMyBank(){
        fileExist = false;
        time = 1;
        categories = new HashSet<String>();
        customers = new HashMap<String, Customer>();
        newLoans = new HashMap<String, Loan>();
        pendingLoans = new HashMap<String, Loan>();
        activeLoans = new HashMap<String, Loan>();
        inRiskLoans = new HashMap<String, Loan>();
        finishedLoans = new HashMap<String, Loan>();
    }
    private void takeCategories(List<String> categories){
        String tmp;
        for (String category: categories) {
            tmp = category.trim().toLowerCase();
            tmp = tmp.substring(0,1).toUpperCase() + tmp.substring(1);
            this.categories.add(tmp);
        }

    }
    private void takeCustomers(List<AbsCustomer> absCustomers){
        String tmpName;

        for (AbsCustomer absCustomer: absCustomers){
            tmpName = absCustomer.getName().trim().toLowerCase();
            tmpName = tmpName.substring(0,1).toUpperCase() + tmpName.substring(1);
            this.customers.put(tmpName,new Customer(absCustomer.getAbsBalance(), tmpName));
        }
    }
    private void takeLoans(List<AbsLoan> absLoans){
        for (AbsLoan absLoan: absLoans)
            newLoans.put(stringShaper(absLoan.getId()),new Loan(stringShaper(absLoan.getId()),this.customers.get(stringShaper(absLoan.getAbsOwner())), stringShaper(absLoan.getAbsCategory()), absLoan.getAbsCapital(),absLoan.getAbsTotalYazTime(), absLoan.getAbsPaysEveryYaz(), absLoan.getAbsIntristPerPayment()));
        for (Loan loan: newLoans.values()){
            loan.getOwner().addBorrowerLoan(loan);
        }
    }
    private String stringShaper(String name){
        String res;
        res = name.trim().toLowerCase();
        res = res.substring(0,1).toUpperCase() + res.substring(1);
        return res;
    }
    public Map<String, CustomerDTO> getCustomers(){
        Map<String, CustomerDTO> res = new HashMap<>();
        for (Customer customer: customers.values()){
            res.put(customer.getName(), new CustomerDTO(customer));
        }
        return res;
        //return customers;
    }
    public List<LoanDTO> getLoans() {
        List<LoanDTO> res = new ArrayList<>();
        res.addAll(loanCollectionToLoanDTOList(newLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(pendingLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(activeLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(inRiskLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(finishedLoans.values()));
        return res;
        /*List<Loan> res = new ArrayList<>();
        res.addAll(newLoans.values());
        res.addAll(pendingLoans.values());
        res.addAll(activeLoans.values());
        res.addAll(inRiskLoans.values());
        res.addAll(finishedLoans.values());
        return res;*/

    }
    private List<LoanDTO> loanCollectionToLoanDTOList(Collection<Loan> list){
        List<LoanDTO> res = new ArrayList<>();
        for (Loan loan: list)
            res.add(new LoanDTO(loan));
        return res;
    }
    private List<Loan> loanDTOCollectionToRealLoan(Collection<LoanDTO> list){
        List<Loan> res = new ArrayList<>();
        for (LoanDTO loanDTO: list)
            res.add(findLoan(loanDTO.getId()));
        return res;
    }
    private Loan findLoan(String loanName){
        if(newLoans.containsKey(loanName))
            return newLoans.get(loanName);
        else
            return pendingLoans.get(loanName);
    }
    public Set<String> getCategories(){
        return new HashSet<String>(categories);
    }
    public void deposit(String chosenCustomer, double amount){
        customers.get(chosenCustomer).deposit(amount, time);
    }
    public boolean withdrawal(String chosenCustomer, double amount){
        return customers.get(chosenCustomer).withdrawal(amount, time);
    }
    public List<LoanDTO> getEligibleLoans(CustomerDTO customer, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, ObservableList<String> chosenCategories){
        List<Loan> allLoans = new ArrayList<>();
        List<Loan> eligibleLoans;
        /*List<Loan> needToRemove = new ArrayList<>();
        boolean found = false;
        String tmpCategory;*/
        allLoans.addAll(newLoans.values());
        allLoans.addAll(pendingLoans.values());

        eligibleLoans = allLoans.stream()
                .filter(tmpLoan -> tmpLoan.getInterestPerPayment() >= minInterest)
                .filter(tmpLoan -> tmpLoan.getTotalYazTime() >= minYaz)
                .filter(tmpLoan -> !tmpLoan.getOwner().getName().equals(customer.getName()))
                .filter(tmpLoan -> tmpLoan.getOwner().getNumOfBorrowerLoans() <= maxLoans || maxLoans < 0)
                .collect(Collectors.toList());
        if (!chosenCategories.isEmpty()){
            eligibleLoans = eligibleLoans.stream().filter(tmpLoan -> chosenCategories.contains(tmpLoan.getCategory())).collect(Collectors.toList());
        }
        /*if(!chosenCategories.isEmpty()){
            for (Loan loan: eligibleLoans){
                found = false;
                tmpCategory = loan.getCategory();
                for(String category: chosenCategories){
                    if(tmpCategory.equals(category)) {
                        found = true;
                        break;
                    }
                }
                if(!found)
                    needToRemove.add(loan);

            }
            for (Loan loan: needToRemove)
                eligibleLoans.remove(loan);

        }*/
        return loanCollectionToLoanDTOList(eligibleLoans);
    }
    public void placementActivation(String chosenCustomer, List<LoanDTO> chosenLoans, int amount, int maxOwnership){
        int numOfLoans = chosenLoans.size(), remainder = amount % numOfLoans;
        int tmpAmountRemaining, index = 0, sumInvested = 0;
        int[] amountForEachLoan;
        List<Loan> realChosenLoans = loanDTOCollectionToRealLoan(chosenLoans);

        customers.get(chosenCustomer).addLenderLoans(realChosenLoans);

        realChosenLoans.sort(Comparator.comparingInt(Loan::getAmountRemaining));
        amountForEachLoan = setAmountForEachLoan(numOfLoans, amount, remainder);

        for (Loan loan: realChosenLoans){
            //if customer has more to invest in this loan than needed
            if(loan.getAmountRemaining() < amountForEachLoan[index]){
                amountForEachLoan[index] -= loan.getAmountRemaining();
                sumInvested += loan.getAmountRemaining();
                invest(chosenCustomer, loan ,loan.getAmountRemaining());
                amount = Arrays.stream(amountForEachLoan).sum();
                numOfLoans--;
                if(numOfLoans == 0)
                    break;
                remainder = amount % numOfLoans;

                amountForEachLoan = setAmountForEachLoan(numOfLoans, amount, remainder);
                index = 0;
            }
            //normal state
            else{
                invest(chosenCustomer, loan, amountForEachLoan[index]);
                sumInvested += amountForEachLoan[index];
                amountForEachLoan[index] = 0;
                index++;
            }
        }
        withdrawal(chosenCustomer, sumInvested);
    }
    private int[] setAmountForEachLoan(int numOfLoans, int amount, int remainder){
        int[] amountForEachLoan = new int[numOfLoans];
        for(int i = 0; i < numOfLoans; i++)
            amountForEachLoan[i] = (amount / numOfLoans);
        for(int i = 0; i < numOfLoans; i++){
            if (remainder == 0)
                break;
            amountForEachLoan[i]++;
            remainder--;
        }
        return amountForEachLoan;
    }
    private void invest(String investor, Loan loan, int amount){
        Loan.Status prevStatus = loan.getStatus();
        loan.invest(customers.get(investor), amount, time);
        if (prevStatus == NEW){
            newLoans.remove(loan.getId());
            if(loan.getAmountRemaining() == 0)
                activeLoans.put(loan.getId(), loan);
            else
                pendingLoans.put(loan.getId(), loan);
        }
        else if(loan.getStatus() == ACTIVE){
            pendingLoans.remove(loan.getId());
            activeLoans.put(loan.getId(), loan);
        }
    }
    public void timeAdvancement(){
        time++;
        Map<Integer, List<Loan>> needToPayLoans = getNeedToPayLoans();

        //sort each loan list by payment amount
        for (List<Loan> loans: needToPayLoans.values())
            loans.sort(Comparator.comparingDouble(Loan::getOnePaymentAmount));

        //sort key list
        List<Integer> keyList = new ArrayList<>(needToPayLoans.keySet());
        Collections.sort(keyList);

        payLoans(needToPayLoans, keyList);

    }
    private void payLoans(Map<Integer, List<Loan>> needToPayLoans, List<Integer> keyList){
        Loan.Status prevStatus;

        for (Integer key: keyList) {
            for (Loan loan : needToPayLoans.get(key)) {
                prevStatus = loan.getStatus();
                if (!loan.pay(time)) { //ACTIVE -> IN_RISK
                    if (prevStatus == ACTIVE){
                        activeLoans.remove(loan.getId());
                        inRiskLoans.put(loan.getId(), loan);
                    }
                }
                else if (loan.getStatus() == FINISHED) {  //ACTIVE -> FINISHED
                    if (prevStatus == ACTIVE)
                        activeLoans.remove(loan.getId());
                    else //IN_RISK -> FINISHED
                        inRiskLoans.remove(loan.getId());
                    finishedLoans.put(loan.getId(), loan);
                }
                else if(prevStatus == IN_RISK){ //IN_RISK -> ACTIVE
                    inRiskLoans.remove(loan.getId());
                    activeLoans.put(loan.getId(), loan);
                }
            }
        }
    }
    private Map<Integer, List<Loan>> getNeedToPayLoans(){
        Map<Integer, List<Loan>> needToPayLoans = new HashMap<>();//key=loan startTime

        for (Loan loan: activeLoans.values()){
            if(loan.isTimeToPay(time)){
                if(!needToPayLoans.containsKey(loan.getStartTime()))
                    needToPayLoans.put(loan.getStartTime(), new ArrayList<>());
                needToPayLoans.get(loan.getStartTime()).add(loan);
            }
        }
        for (Loan loan: inRiskLoans.values()){
            if(loan.isTimeToPay(time)){
                if(!needToPayLoans.containsKey(loan.getStartTime()))
                    needToPayLoans.put(loan.getStartTime(), new ArrayList<>());
                needToPayLoans.get(loan.getStartTime()).add(loan);
            }
        }
        return needToPayLoans;
    }
}

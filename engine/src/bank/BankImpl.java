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
import dto.PaymentDTO;
import exceptions.*;
import file.FileChecker;
import generated.AbsDescriptor;
import generated.AbsLoan;
import loan.Loan;

import javax.xml.bind.JAXBException;

import static loan.Loan.Status.*;

public class BankImpl implements Bank {
    private boolean fileExist;
    private boolean isAdminLoggedIn;
    private String adminName;
    private int time;
    private int version;
    private Map<String, Customer> customers;
    private Map<String, Loan> newLoans;
    private Map<String, Loan> pendingLoans;
    private Map<String, Loan> activeLoans;
    private Map<String, Loan> inRiskLoans;
    private Map<String, Loan> finishedLoans;
    private Set<String> categories;
    private Map<String, List<Loan>> customerToLoanNeedToPayLoans; //key =customerName

    public BankImpl(){
        fileExist = false;
        isAdminLoggedIn = false;
        time = 1;
        version = 1;
        categories = new HashSet<String>();
        customers = new HashMap<String, Customer>();
        newLoans = new HashMap<String, Loan>();
        pendingLoans = new HashMap<String, Loan>();
        activeLoans = new HashMap<String, Loan>();
        inRiskLoans = new HashMap<String, Loan>();
        finishedLoans = new HashMap<String, Loan>();
        customerToLoanNeedToPayLoans = new HashMap<>();
    }
    public boolean isFileExist(){
        return fileExist;
    }
    public int getTime(){
        return time;
    }
    public void insertNewLoan(String customerName, String id, int capital, String category, int totalYazTime, int paysEveryYaz, int interestPerPayment) throws SameLoanIdException, NoSuchCategoryException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativePaysEveryYazException, NegativeCapitalException, PaymentMarginException {
        checkId(id);
        checkCategory(category);
        checkNumbers(id,capital, totalYazTime, paysEveryYaz, interestPerPayment);
        newLoans.put(id, new Loan(id,this.customers.get(customerName),category,capital,totalYazTime,paysEveryYaz,interestPerPayment));
        version++;
    }

    @Override
    public int getVersion() {
        return version;
    }
    private void checkNumbers(String id, int capital, int totalYazTime, int paysEveryYaz, int interestPerPayment) throws NegativeCapitalException, NegativeTotalYazTimeException, NegativePaysEveryYazException, NegativeIntristPerPaymentException, PaymentMarginException {
        if (capital < 0)
            throw new NegativeCapitalException(id, capital);
        if (totalYazTime < 0)
            throw new NegativeTotalYazTimeException(id, totalYazTime);
        if (paysEveryYaz < 0)
            throw new NegativePaysEveryYazException(id, paysEveryYaz);
        if (interestPerPayment < 0)
            throw new NegativeIntristPerPaymentException(id, interestPerPayment);
        if(totalYazTime % paysEveryYaz != 0)
            throw new PaymentMarginException(totalYazTime,paysEveryYaz);

    }

    private void checkCategory(String category) throws NoSuchCategoryException {
        if(!this.categories.contains(category))
            throw new NoSuchCategoryException(category, new ArrayList<>(this.categories));
    }

    private void checkId(String id) throws SameLoanIdException {
        if(this.newLoans.containsKey(id) || this.pendingLoans.containsKey(id) ||
                this.inRiskLoans.containsKey(id) || this.finishedLoans.containsKey(id)){
            throw new SameLoanIdException(id);
        }

    }

    public void readFile(File file, String customerName) throws FileNotFoundException, FileNotXMLException, JAXBException, NoSuchCategoryException, PaymentMarginException, SameCustomerNameException, SameLoanIdException, NegativeTotalYazTimeException, NegativeIntristPerPaymentException, NegativeBalanceException, NegativePaysEveryYazException, NegativeCapitalException {
        version++;
        AbsDescriptor absDescriptor = null;
        FileChecker fileChecker = new FileChecker();
        InputStream in = new FileInputStream(file);
        absDescriptor = fileChecker.openFile(in);
        fileChecker.checkXMLContent(absDescriptor);
        fromJAXBObjToBank(absDescriptor, customerName);
    }
    private void fromJAXBObjToBank(AbsDescriptor absDescriptor, String customerName){
        //cleanMyBank();
        takeCategories(absDescriptor.getAbsCategories().getAbsCategory());
        //takeCustomers(absDescriptor.getAbsCustomers().getAbsCustomer());
        takeLoans(absDescriptor.getAbsLoans().getAbsLoan(), customerName);
        //time = 1;
        //fileExist = true;
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
    private void takeLoans(List<AbsLoan> absLoans, String customerName){
        for (AbsLoan absLoan: absLoans)
            newLoans.put(stringShaper(absLoan.getId()),new Loan(stringShaper(absLoan.getId()),this.customers.get(customerName), stringShaper(absLoan.getAbsCategory()), absLoan.getAbsCapital(),absLoan.getAbsTotalYazTime(), absLoan.getAbsPaysEveryYaz(), absLoan.getAbsIntristPerPayment()));
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
    public synchronized Map<String, CustomerDTO> getCustomers(){
        Map<String, CustomerDTO> res = new HashMap<>();
        for (Customer customer: customers.values()){
            res.put(customer.getName(), new CustomerDTO(customer));
        }
        return res;
        //return customers;
    }
    public synchronized List<LoanDTO> getLoans() {
        List<LoanDTO> res = new ArrayList<>();
        res.addAll(loanCollectionToLoanDTOList(newLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(pendingLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(activeLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(inRiskLoans.values()));
        res.addAll(loanCollectionToLoanDTOList(finishedLoans.values()));
        return res;


    }
    private List<LoanDTO> loanCollectionToLoanDTOList(Collection<Loan> list){
        List<LoanDTO> res = new ArrayList<>();
        if(list != null){
            for (Loan loan: list)
                res.add(new LoanDTO(loan));
        }
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
    public synchronized Set<String> getCategories(){
        return new HashSet<String>(categories);
    }
    public synchronized void deposit(String chosenCustomer, double amount){
        customers.get(chosenCustomer).deposit(amount, time);
        //version++;
    }
    public synchronized boolean withdrawal(String chosenCustomer, double amount){
        //version++;
        return customers.get(chosenCustomer).withdrawal(amount, time);
    }
    public synchronized List<LoanDTO> getEligibleLoans(String customerName, int amount, int minInterest, int minYaz, int maxLoans, int maxOwnership, Set<String> chosenCategories){
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
                .filter(tmpLoan -> !tmpLoan.getOwner().getName().equals(customerName))
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
        version++;
        int numOfLoans = chosenLoans.size(), remainder = amount % numOfLoans;
        int tmpAmountRemaining, index = 0, sumInvested = 0;
        int[] amountForEachLoan;
        List<Loan> realChosenLoans = loanDTOCollectionToRealLoan(chosenLoans);
        double maxOwnershipPercentage = maxOwnership > 0 ? (double) maxOwnership / 100 : 1;
        customers.get(chosenCustomer).addLenderLoans(realChosenLoans);
        realChosenLoans.sort(Comparator.comparingInt(Loan::getAmountRemaining));


            amountForEachLoan = setAmountForEachLoan(numOfLoans, amount, remainder);
            for (Loan loan: realChosenLoans){
                //if customer has more to invest in this loan than needed
                if(loan.getAmountRemaining() < amountForEachLoan[index]){
                    amountForEachLoan[index] -= loan.getAmountRemaining();
                    sumInvested += (int) Math.min(loan.getAmountRemaining(), maxOwnershipPercentage*loan.getCapital());
                    invest(chosenCustomer, loan , (int) Math.min(loan.getAmountRemaining(), maxOwnershipPercentage*loan.getCapital()));
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
                    invest(chosenCustomer, loan, (int) Math.min(amountForEachLoan[index], maxOwnershipPercentage * loan.getCapital()));
                    sumInvested += (int) Math.min(amountForEachLoan[index], maxOwnershipPercentage * loan.getCapital());
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
    public synchronized void timeAdvancement(){
        version++;
        time++;
        Map<String, Loan> needToPayLoans = getNeedToPayLoans();
        //Map<String, LoanDTO> needToPayLoansDTO = new HashMap<>();

        checkIfLateToPay();

        needToPayLoans.values().forEach((loan) -> {
            String customerName = loan.getOwner().getName();
            this.customerToLoanNeedToPayLoans.computeIfAbsent(customerName, k -> new ArrayList<>());
            this.customerToLoanNeedToPayLoans.get(customerName).add(loan);

        });
        /*needToPayLoans.forEach((k,v) -> needToPayLoansDTO.put(k, new LoanDTO(v)));
        return needToPayLoansDTO;*/

    }
    public synchronized List<LoanDTO> getCustomerNeedToPayLoans(String customerName){
         return loanCollectionToLoanDTOList(this.customerToLoanNeedToPayLoans.get(customerName));
    }
    public synchronized void payOnePayment(LoanDTO selectedLoan) throws Exception {
        version++;
        String loanId = selectedLoan.getId();
        Loan.Status prevStatus = selectedLoan.getStatus();

        if (selectedLoan.getLastPaymentTime() == time && activeLoans.get(loanId).getLastPaymentStatus().equals(ACTIVE)) //already paid this payment
            throw new PaymentException("you already paid this payment");

        if (selectedLoan.getStatus().equals(ACTIVE)) {
            activeLoans.get(loanId).pay(selectedLoan.getOnePaymentAmount(), selectedLoan.getCapitalPart(), selectedLoan.getInterestPart(), time);
        }
        else{
            inRiskLoans.get(loanId).pay(selectedLoan.getOnePaymentAmount(), selectedLoan.getCapitalPart(), selectedLoan.getInterestPart(), time);
        }
        checkIfLoanFinished(prevStatus, loanId);
    }
    public synchronized void payAllLoan(LoanDTO selectedLoan){
        version++;
        double totalCapitalRemaining = selectedLoan.getTotalCapitalRemaining();
        double totalInterestRemaining = selectedLoan.getTotalInterestRemaining();
        //double totalAmountRemaining = totalCapitalRemaining + totalInterestRemaining;
        double totalAmountPaid = getTotalAmountPaid(selectedLoan);
        double totalNeedToPay = selectedLoan.getOnePaymentAmount() * (selectedLoan.getTotalYazTime() / selectedLoan.getPaysEveryYaz());

        Loan.Status prevStatus = selectedLoan.getStatus();

        if (selectedLoan.getStatus().equals(ACTIVE))
            activeLoans.get(selectedLoan.getId()).pay(totalNeedToPay - totalAmountPaid, totalCapitalRemaining, totalInterestRemaining, time);
        else
            inRiskLoans.get(selectedLoan.getId()).pay(totalNeedToPay - totalAmountPaid + selectedLoan.getDebt(),totalCapitalRemaining, totalInterestRemaining, time);

        checkIfLoanFinished(prevStatus, selectedLoan.getId());
    }
    public synchronized void payDebt(LoanDTO selectedLoan, double amount){
        version++;
        Loan loan = inRiskLoans.get(selectedLoan.getId());
        if(loan.getOwner().withdrawal(Math.min(amount, selectedLoan.getDebt()), time)) {
            loan.payDebt(Math.min(amount, selectedLoan.getDebt()), this.time);

            if(loan.getStatus().equals(ACTIVE)){
                activeLoans.put(loan.getId(), loan);
                inRiskLoans.remove(loan.getId());
            }
        }
        /*else
            System.out.println("amount is bigger than balance");*/
    }
    private double getTotalAmountPaid(LoanDTO selectedLoan) {
        double res = 0;
        for (PaymentDTO paymentDTO: selectedLoan.getPayments().values())
            res += paymentDTO.getTotalAmount();
        return res;
    }
    private void checkIfLoanFinished(Loan.Status prevStatus, String loanId) {
        if(prevStatus.equals(ACTIVE)){
            if(activeLoans.get(loanId).getStatus().equals(FINISHED)){
                finishedLoans.put(loanId, activeLoans.get(loanId));
                activeLoans.remove(loanId);
            }
        }
        else if(prevStatus.equals(IN_RISK)){
            if(inRiskLoans.get(loanId).getStatus().equals(FINISHED)){
                finishedLoans.put(loanId, inRiskLoans.get(loanId));
                inRiskLoans.remove(loanId);
            }
        }
    }
    private void checkIfLateToPay() {
        List<String> toInRiskKeys = new ArrayList<>();

        //IN RISK loans
        inRiskLoans.forEach((loanId, loan) -> {
            if(loan.isLateToPay(this.time))
                loan.missedPayment();
        });

        //ACTIVE loans
       activeLoans.forEach((loanId, loan) ->{
           if(loan.isLateToPay(this.time)){
               toInRiskKeys.add(loanId);
               loan.missedPayment();
               inRiskLoans.put(loanId,loan);
           }
       });
       toInRiskKeys.forEach(loadId -> activeLoans.remove(loadId));


    }
    private Map<String, Loan> getNeedToPayLoans(){
        Map<String, Loan> needToPayLoans = new HashMap<>();

        activeLoans.forEach((k,v) -> {
            if(v.isTimeToPay(time))
                needToPayLoans.put(k,v);
        });

        inRiskLoans.forEach((k,v) -> {
            if(v.isTimeToPay(time))
                needToPayLoans.put(k,v);
        });

        return needToPayLoans;

    }
    public synchronized boolean getIsAdminLoggedIn(){
        return isAdminLoggedIn;
    }
    public synchronized void setIsAdminLoggedIn(boolean val){
        isAdminLoggedIn = true;
    }
    public synchronized void addAdmin(String usernameFromParameter){
        adminName = usernameFromParameter;
    }
    public synchronized boolean isUserExists(String usernameFromParameter){
        if(isAdminLoggedIn)
            if(usernameFromParameter.equals(adminName))
                return true;

        for(String name: customers.keySet())
            if (name.equals(usernameFromParameter))
                return true;

        return false;
    }
    public synchronized void addCustomer(String usernameFromParameter){
        customers.put(usernameFromParameter, new Customer(0, usernameFromParameter));
        version++;
    }

}


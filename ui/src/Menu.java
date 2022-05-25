import com.sun.org.apache.xerces.internal.util.Status;
import customer.Customer;
import dto.CustomerDTO;
import dto.LoanDTO;
import loan.Loan;

import java.util.*;

public class Menu{
    private final int NO_LIMIT = -1;
    private final int STRING_CHOICE = -1;
    private final int NO_CHOICE = -1;

    public void displayMainMenu(int time){
        System.out.println("Time: " + time);
        System.out.println("Please Choose an option (number between 1-8):");
        System.out.println("1 - Read file");
        System.out.println("2 - Display loans");
        System.out.println("3 - Display customers");
        System.out.println("4 - Money deposit");
        System.out.println("5 - Money withdrawal ");
        System.out.println("6 - Placement activation");
        System.out.println("7 - Time unit advancement");
        System.out.println("8 - Exit");
    }
    public int getMainChoice(int time){
        boolean validChoice = false;
        int choice;

        do {
            displayMainMenu(time);
            choice = getChoice();
            if(choice == STRING_CHOICE)
                System.out.println("String was entered!");
            else {
                validChoice = isInRangeChoice(choice, 1, 8);
                if(!validChoice)
                    System.out.println("Out of range number was entered!");
            }
        } while (!validChoice);

        return choice;
    }
    public int getChoice(){
        Scanner scanObj = new Scanner(System.in);
        int choice = STRING_CHOICE;
        try {
            choice = scanObj.nextInt();
        } catch (Exception e) {
            scanObj.nextLine();
        }
        return choice;
    }
    public boolean isInRangeChoice(int choice,int smallest, int biggest){
        if(biggest != NO_LIMIT)
            return choice >= smallest && choice <= biggest;
        return choice >= smallest;
    }
    public String getFileName(){
        System.out.println("Please enter full path name:");
        Scanner scanObj = new Scanner(System.in);
        return scanObj.nextLine();
    }
    public String getCustomerChoice(Map<String, CustomerDTO> customers){
        int index = 1;
        int choice = 0;
        boolean validChoice = false;

        List<String> names = new ArrayList<>();

        System.out.println("Please choose a customer:");
        for(String key: customers.keySet()) {
            System.out.println(index + " - " + "Name: " + customers.get(key).getName() + ", Balance: "+ customers.get(key).getBalance());
            names.add(key);
            index++;
        }
        while (!validChoice){
            choice = getChoice();
            if(choice == STRING_CHOICE)
                System.out.println("String was entered!");
            else {
                validChoice = isInRangeChoice(choice,1, index - 1);
                if(!validChoice)
                    System.out.println("Number is out of range!");
                else
                    break;
            }
            index = 1;
            System.out.println("Please choose a customer:");
            for(String key: customers.keySet()) {
                System.out.println(index + " - " + "Name: " + customers.get(key).getName() + ", Balance: "+ customers.get(key).getBalance());
                index++;
            }
        }
        return names.get(choice - 1);
    }
    public int getAmount(String msg){
        boolean validChoice = false;
        int choice;

        do {
            System.out.println(msg);
            choice = getChoice();
            if(choice == STRING_CHOICE)
                System.out.println("String was entered!");
            else {
                validChoice = isInRangeChoice(choice,1, NO_LIMIT);
                if(!validChoice)
                    System.out.println("Please enter a positive Integer!");
            }
        } while (!validChoice);
        return choice;
    }
    public Set<String> getCategoryChoice(Set<String> categories){
        System.out.println("Please choose the categories (if any - select finish choice) you want to invest in and press finish choice when you are done (press ENTER after each choice):");
        List<String> tmpCategories = new ArrayList<String>(categories);
        Set<String> res = new HashSet<String>();
        int index = 1;
        int choice = 0;
        boolean validChoice = false;
        for (String category: tmpCategories){
            System.out.println(index + " - " + category);
            index++;
        }
        System.out.println(index + " - Finish choice");

        while (choice != tmpCategories.size() + 1){
            choice = getChoice();
            if(choice == STRING_CHOICE)
                System.out.println("String was entered!");
            else {
                validChoice = isInRangeChoice(choice, 1, index);
                if (!validChoice)
                    System.out.println("Number is out of range!");
                else if(choice != tmpCategories.size() + 1)
                    res.add(tmpCategories.get(choice - 1));
            }
        }
        return res;
    }
    public double getMinimumInterest(){
        boolean validChoice = false;
        int choice;

        do {
            System.out.println("Please enter minimum interest: [enter 0 if you don't care]");
            choice = getChoice();
            if(choice == STRING_CHOICE)
                System.out.println("String was entered!");
            else {
                validChoice = isInRangeChoice(choice,0, NO_LIMIT);
                if(!validChoice)
                    System.out.println("Please enter a positive Integer!");
            }
        } while (!validChoice);
        return choice;
    }
    public int getMinimumTotalTime(){
        boolean validChoice = false;
        int choice;

        do {
            System.out.println("Please enter minimum total time for loan: [enter 0 if you don't care]");
            choice = getChoice();
            if(choice == STRING_CHOICE)
                System.out.println("String was entered!");
            else {
                validChoice = isInRangeChoice(choice,0, NO_LIMIT);
                if(!validChoice)
                    System.out.println("Please enter a positive Integer!");
            }
        } while (!validChoice);
        return choice;
    }

}

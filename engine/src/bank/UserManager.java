package bank;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private Set<String> usersSet;
    private boolean isAdminLoggedIn;

    public UserManager() {
        usersSet = new HashSet<>();
        isAdminLoggedIn = false;
    }

    public synchronized boolean getIsAdminLoggedIn(){
        return isAdminLoggedIn;
    }

    public void setIsAdminLoggedIn(boolean adminLoggedIn) {
        isAdminLoggedIn = adminLoggedIn;
    }

    public synchronized void addUser(String username) {
        usersSet.add(username);
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }
}
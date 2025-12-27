package util;

import model.CreateAccount;

public class UserSession {
    private static UserSession instance;
    private CreateAccount currentUser;
    
    private UserSession() {}
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void setCurrentUser(CreateAccount user) {
        this.currentUser = user;
        System.out.println("User session set: " + (user != null ? user.getUsername() + " (ID: " + user.getUserId() + ")" : "null"));
    }
    
    public CreateAccount getCurrentUser() {
        return currentUser;
    }
    
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null && currentUser.getUserId() != null;
    }
    
    public void logout() {
        System.out.println("User logged out: " + (currentUser != null ? currentUser.getUsername() : "null"));
        currentUser = null;
    }
}
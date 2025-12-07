package validator;

import java.io.File;
import java.util.Date;

import model.CreateAccount;

public class UserFormValidator {


    public boolean hasMissingField(CreateAccount user) {
        return user.getUsername() == null && user.getUsername().isEmpty()
            && user.getEmail() == null && user.getEmail().isEmpty()
            && user.getPassword() == null && user.getPassword().isEmpty()
            && user.getPhone() == null && user.getPhone().isEmpty()
            && user.getDateOfBirth() == null
            && user.getGender() == null && user.getGender().isEmpty()
            && user.getProfilePicPath() == null && user.getProfilePicPath().isEmpty();
    }

    public boolean isUsernameTooShort(CreateAccount user){
        if(user == null || user.getUsername() == null) return true;

        return user.getUsername().trim().length() > 10;
    }


    public boolean isValidEmail(String email){
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean isStrongPassword(String password){
        return password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$") || password.length() < 9;
    }

    public boolean isValidBirthday(Date dob){
        Date today = new Date();
        long age = today.getYear() - dob.getYear();

        return dob.before(today) && age >= 15 && age <= 120;
    }

    public boolean isValidBio(String bio){
        if (bio.length() > 500) return false;
        if (bio.matches(".*<.*>.*")) return false;
        if (bio.toLowerCase().contains("drop table")) return false;
        return true;
    }

    public boolean isValidImage(File file){
        if (file == null) return false;

        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") ||
            name.endsWith(".jpeg") ||
            name.endsWith(".png");
        }

    public String sanitize(String input){
        return input.replaceAll("[<>\"']", "");
    }

    public boolean isUsernameTooRepetitive(CreateAccount user) {
        if (user.getUsername() == null) return true;
        return user.getUsername().matches("(.)\\1{3,}"); 
    }

}

package model;

import java.time.LocalDate;


public class CreateAccount {
    private Long uid;
    private String userName;
    private String email;
    private String password;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private String gender;
    private String role;
    private String bio;
    private String profilePicPath;
    private String status;

    public CreateAccount(){}

    public CreateAccount(String userName, String email, String password,
                String phone, LocalDate dateOfBirth, String address, String gender,
                String role, String bio, String profilePicPath) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.bio = bio;
        this.profilePicPath = profilePicPath;
    }


    public Long getUserId(){return uid;}
    public void setUserId(Long uid){ this.uid = uid;}

    public String getUsername(){ return userName; }
    public void setUsername(String userName){ this.userName = userName; }

    public String getEmail(){ return email; }
    public void setEmail(String email){this.email = email; }

    public String getPassword(){ return password; }
    public void setPasword(String password){this.password = password; }

    public String getPhone(){ return phone; }
    public void setPhone(String phone){this.phone = phone;}

    public LocalDate getDateOfBirth(){ return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth){ this.dateOfBirth = dateOfBirth; }

    public String getAddress(){ return address; }
    public void setAddress(String address){this.address = address;}

    public String getGender(){ return gender; }
    public void setGender(String gender){this.gender = gender;}

    public String getRole(){ return role; }
    public void setRole(String role){ this.role = role; }

    public String getBio(){ return bio; }
    public void setBio(String bio){this.bio = bio;}

    public String getProfilePicPath(){ return profilePicPath; }
    public void setProfilePicPath(String profilePicPath){ this.profilePicPath = profilePicPath; }

    public String getStatus(){ return status; }
    public void setStatus(String status){this.status = status; }

}

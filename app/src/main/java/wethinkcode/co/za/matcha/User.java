package wethinkcode.co.za.matcha;

import android.net.Uri;

public class User {

    private String  platform;
    private String  username;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  gender;
    private String  birthDate;
    private String  sexPref;
    private String  bio;
    private String  interests;
    private String  profPic;
    private String  pics;
    private String  location;

    public User(){
        platform = "matcha";
    };

    public String getPlatform() {
        return platform;
    }

    public String getInterests() {
        return interests;
    }

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSexPref() {
        return sexPref;
    }

    public void setSexPref(String sexPref) {
        this.sexPref = sexPref;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
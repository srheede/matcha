package wethinkcode.co.za.matcha;

import android.net.Uri;

public class User {

    String firebaseID;
    String username;
    String firstName;
    String lastName;
    String email;
    String gender;
    String birthDate;
    String sexPref;
    String bio;
    String interests[];
    Uri profPic;
    String pics[] = new String[4];
    String location;

    public User(){

    };

    public String[] getInterests() {
        return interests;
    }

    public Uri getProfPic() {
        return profPic;
    }

    public void setProfPic(Uri profPic) {
        this.profPic = profPic;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public String[] getPics() {
        return pics;
    }

    public void setPics(String[] pics) {
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

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirebaseID() {
        return firebaseID;
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
package wethinkcode.co.za.matcha;

public class User {
    String Username;
    String Email;
    String FirstName;
    String Surname;
    String Password;
    String Gender;
    String SexPref;
    String Bio;
    String Interests;
    String Pics;
    String ProfPic;
    String Location;

    public User(String username, String email, String firstName, String surname, String password, String gender, String sexPref, String bio) {
        Username = username;
        Email = email;
        FirstName = firstName;
        Surname = surname;
        Password = password;
        Gender = gender;
        SexPref = sexPref;
        Bio = bio;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getSurname() {
        return Surname;
    }

    public String getPassword() {
        return Password;
    }

    public String getGender() {
        return Gender;
    }

    public String getSexPref() {
        return SexPref;
    }

    public String getBio() {
        return Bio;
    }
}

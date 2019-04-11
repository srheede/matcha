package wethinkcode.co.za.matcha;

public class User {
    private String UID;
    private String Username;
    private String Email;
/*    String FirstName;
    String Surname;
    String Gender;
    String SexPref;
    String Bio;
    String Interests;
    String Pics;
    String ProfPic;
    String Location;*/

    public User(String Uid, String username, String email) {
        UID = Uid;
        Username = username;
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

/*    public String getFirstName() {
        return FirstName;
    }

    public String getSurname() {
        return Surname;
    }

    public String getGender() {
        return Gender;
    }

    public String getSexPref() {
        return SexPref;
    }

    public String getBio() {
        return Bio;
    }*/
}

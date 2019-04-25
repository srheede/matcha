package wethinkcode.co.za.matcha;

public class User {
    public String id;
    public String first_name;
    public String last_name;
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
        id = Uid;
        first_name = username;
        last_name = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

/*    public User(String Uid, String username, String email) {
        UID = Uid;
        Username = username;
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }*/

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

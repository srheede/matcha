package wethinkcode.co.za.matcha;


import com.google.gson.Gson;

public class Post {
    private String  firebaseID;
    private String data;

    public Post(String firebaseID, String data) {
        this.firebaseID = firebaseID;
        this.data= data;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

package wethinkcode.co.za.matcha;


import com.google.gson.Gson;

public class Post {
    private String  userId;
    private String data;

    public Post(String userId, String data) {
        this.userId = userId;
        this.data= data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

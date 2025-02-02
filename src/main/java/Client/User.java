package Client;

import org.json.JSONObject;

public class User {
    private String username;
    private int age;
    private String password;

    public User(JSONObject response) {
        this.username = response.getString("username");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

    public JSONObject userTOJson() {
        JSONObject json = new JSONObject();
        json.put("username", this.username);
        return json;
    }
    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }


}
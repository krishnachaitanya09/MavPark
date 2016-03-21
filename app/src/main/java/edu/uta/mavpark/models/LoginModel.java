package edu.uta.mavpark.models;

/**
 * Created by krish on 3/18/2016.
 */
public class LoginModel {

    public LoginModel(String username, String password) {
        this.username = username;
        this.password = password;
        this.grant_type = "password";
    }

    public String username;
    public String password;
    public String grant_type;

}

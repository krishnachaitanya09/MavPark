package edu.uta.mavpark.models;

/**
 * Created by krish on 3/20/2016.
 */
public class RegisterModel {

    public RegisterModel(String name, String utaId, String email, String password, String confirmPassword, String role) {
        this.name = name;
        this.utaId = utaId;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }

    public String name;
    public String utaId;
    public String email;
    public String password;
    public String confirmPassword;
    public String role;
}

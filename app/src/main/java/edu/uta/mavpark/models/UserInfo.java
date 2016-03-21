package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by krish on 3/18/2016.
 */
public class UserInfo {
    @SerializedName("Name")
    public String name;
    @SerializedName("Email")
    public String email;
    @SerializedName("Role")
    public String role;

}

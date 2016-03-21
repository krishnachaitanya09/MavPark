package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Token {
    @SerializedName("access_token")
    public String AccessToken;
    @SerializedName("userName")
    public String username;
    @SerializedName(".issued")
    public Date Issued;
    @SerializedName(".expires")
    public Date Expires;
    @SerializedName("error_description")
    public String Error;
}

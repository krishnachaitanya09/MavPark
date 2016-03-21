package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by krish on 3/21/2016.
 */
public class ErrorModel {
    @SerializedName("Message")
    public String Message;
    @SerializedName("ModelState")
    public Map<String, String[]> ModelState;
    @SerializedName("error_description")
    public String Error;
}

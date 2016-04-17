package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;


public class RememberParkingModel implements Serializable{
    @SerializedName("Id")
    public int Id;
    @SerializedName("Latitude")
    public String Latitude;
    @SerializedName("Longitude")
    public String Longitude;
    @SerializedName("ParkedDate")
    public Date ParkedDate;
}

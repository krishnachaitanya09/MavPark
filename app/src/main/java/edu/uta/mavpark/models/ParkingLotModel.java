package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by krish on 3/22/2016.
 */
public class ParkingLotModel implements Serializable {
    @SerializedName("Id")
    public String Id;
    @SerializedName("IsAvailable")
    public boolean IsAvailable;
    @SerializedName("Latitude")
    public double Latitude;
    @SerializedName("Longitude")
    public double Longitude;
    @SerializedName("LastUpdated")
    public Date LastUpdated;
    @SerializedName("Distance")
    public double Distance;
    @SerializedName("AvailableSpaces")
    public int AvailableSpaces;
    @SerializedName("ParkingSpaces")
    public ArrayList<ParkingSpaceModel> ParkingSpaces;
}

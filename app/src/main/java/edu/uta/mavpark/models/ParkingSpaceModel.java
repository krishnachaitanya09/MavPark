package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by krish on 3/22/2016.
 */
public class ParkingSpaceModel implements Serializable {
    @SerializedName("Id")
    public String Id;
    @SerializedName("ParkingLotId")
    public String ParkingLotId;
    @SerializedName("IsAvailable")
    public boolean IsAvailable;
    @SerializedName("LastUpdated")
    public Date LastUpdated;
}

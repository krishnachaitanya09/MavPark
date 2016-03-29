package edu.uta.mavpark.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;


public class PermitModel implements Serializable{
    @SerializedName("Id")
    public int Id;
    @SerializedName("UserId")
    public String UserId;
    @SerializedName("LicensePlateId")
    public String LicensePlateId;
    @SerializedName("ParkingLotId")
    public String ParkingLotId;
    @SerializedName("ParkingSpaceId")
    public String ParkingSpaceId;
    @SerializedName("FromDateTime")
    public Date FromDateTime;
    @SerializedName("ToDateTime")
    public Date ToDateTime;
}

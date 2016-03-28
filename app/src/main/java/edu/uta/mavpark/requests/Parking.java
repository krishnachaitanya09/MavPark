package edu.uta.mavpark.requests;

import java.util.ArrayList;

import edu.uta.mavpark.models.ParkingLotModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by krish on 3/22/2016.
 */
public interface Parking {

    @GET("ParkingLots")
    public Call<ArrayList<ParkingLotModel>> getParkingLots(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("fromTime") String fromTime, @Query("toTime") String toTime);
}

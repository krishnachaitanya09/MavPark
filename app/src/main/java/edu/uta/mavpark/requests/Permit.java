package edu.uta.mavpark.requests;

import java.util.ArrayList;

import edu.uta.mavpark.models.PermitModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by krish on 3/21/2016.
 */
public interface Permit {

    @GET("Reservation")
    public Call<ArrayList<PermitModel>> getPermits();

    @FormUrlEncoded
    @POST("Reservation")
    public Call<PermitModel> bookPermit(@Field("ParkingLotId") String parkingLotId, @Field("FromDateTime") String fromDateTime,
                               @Field("ToDateTime") String toDateTime, @Field("LicensePlateId") String licensePlateId);
}

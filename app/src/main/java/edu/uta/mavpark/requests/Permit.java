package edu.uta.mavpark.requests;

import java.util.ArrayList;

import edu.uta.mavpark.models.PermitModel;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by krish on 3/21/2016.
 */
public interface Permit {

    @GET("Reservation")
    public Call<ArrayList<PermitModel>> getPermits();
}

package edu.uta.mavpark.requests;

import java.util.ArrayList;

import edu.uta.mavpark.models.ReportModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by krish on 3/21/2016.
 */
public interface Report {

    @GET("Report")
    public Call<ArrayList<ReportModel>> getReports();

    @FormUrlEncoded
    @POST("Report")
    public Call<ReportModel> submitReport(@Field("ReportType") String reportType, @Field("Comments") String comments, @Field("ParkingLot") String parkingLot,
                                        @Field("LicensePlateId") String licensePlateId, @Field("ReportTime") String reportTime);
}

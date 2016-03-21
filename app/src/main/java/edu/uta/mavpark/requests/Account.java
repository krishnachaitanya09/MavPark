package edu.uta.mavpark.requests;

import edu.uta.mavpark.models.Token;
import edu.uta.mavpark.models.UserInfo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface Account {
    @FormUrlEncoded
    @POST("Token")
    public Call<Token> token(@Field("username") String username, @Field("password") String password,
                             @Field("grant_type") String grant_type);
    @FormUrlEncoded
    @POST("Account/Register")
    public Call<Void> register(@Field("name") String name, @Field("utaid") String utaId,
                               @Field("email") String email, @Field("password") String password,
                               @Field("confirmpassword") String confirmPassword,  @Field("role") String role);

    @GET("Account/UserInfo")
    public Call<UserInfo> getUserInfo();
}


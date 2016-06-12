package com.shift.hack.beacon.network;

import com.shift.hack.beacon.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClient {
    @GET("devices")
    Call<ResponseBody> getDevice(@Query("uuid") String uuid);

    @FormUrlEncoded
    @POST("auth/facebook")
    Call<User> auth(@Field("access_token") String accessToken);
}

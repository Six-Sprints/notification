package com.sixsprints.notification.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SmsInstaService {

  String BASE_URL = "http://app.smsinsta.in/vendorsms/";

  @GET("pushsms.aspx")
  Call<String> sendSms(@Query("clientid") String clientId, @Query("apikey") String apiKey,
    @Query("msisdn") String to, @Query("sid") String senderId,
    @Query("msg") String message, @Query("fl") int flag, @Query("gwid") int gwId);

}

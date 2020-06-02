package com.sixsprints.notification.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SmsInstaService {

  String BASE_URL = "http://sms.smsinsta.in/vb/";

  @GET("apikey.php")
  Call<String> sendSms(@Query("apikey") String apiKey, @Query("senderid") String senderId,
    @Query("number") String to, @Query("message") String message, @Query("route") int route);

}

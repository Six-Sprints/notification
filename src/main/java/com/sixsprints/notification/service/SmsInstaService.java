package com.sixsprints.notification.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SmsInstaService extends SmsApiService {

  String BASE_URL = "http://sms.smsinsta.in/vb/";

  @Override
  @GET("apikey.php")
  Call<String> sendSms(@Query("apikey") String apiKey, @Query("senderid") String senderId,
    @Query("number") String to, @Query("message") String message, @Query("templateid") String templateId,
    @Query("route") int route);

}

package com.sixsprints.notification.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TextlocalSmsService extends SmsApiService {

  String BASE_URL = "https://api.textlocal.in/";

  @Override
  @GET("send")
  Call<String> sendSms(@Query("apikey") String apiKey, @Query("sender") String senderId,
    @Query("numbers") String to, @Query("message") String message, @Query("templateid") String templateId,
    @Query("custom") int custom);

}

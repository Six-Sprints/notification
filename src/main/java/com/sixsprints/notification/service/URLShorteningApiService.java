package com.sixsprints.notification.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface URLShorteningApiService {

  String BASE_URL = "http://cutt.ly/api/";

  @GET("api.php")
  Call<String> sendSms(@Query("key") String apiKey, @Query("short") String url);

}

package com.sixsprints.notification.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SmsInstaService {

  String BASE_URL = "http://sms.smsinsta.in/vb/";

//   route = 3
  @GET("apikey.php")
  Call<String> sendSms(@Query("apikey") String apiKey, @Query("senderid") String senderId,
    @Query("number") String to, @Query("message") String message, @Query("route") int route);

//  String BASE_URL = "http://app.smsinsta.in/vendorsms/";

  // fl = 0, gwid = 2
//  @GET("pushsms.aspx")
//  Call<String> sendSms(@Query("clientid") String clientId, @Query("apikey") String apiKey,
//    @Query("sid") String senderId,
//    @Query("msisdn") String to, @Query("msg") String message, @Query("fl") int fl, @Query("gwid") int gwid);

}

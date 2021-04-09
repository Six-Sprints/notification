package com.sixsprints.notification.service;

import retrofit2.Call;

public interface SmsApiService {

  Call<String> sendSms(String apiKey, String senderId, String to, String message, String templateId, int extra);

}

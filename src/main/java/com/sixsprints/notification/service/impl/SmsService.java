package com.sixsprints.notification.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.enums.sms.SmsServiceOptions;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.SmsApiService;

import retrofit2.Call;
import retrofit2.Response;

public class SmsService implements NotificationService {

  private MessageAuthDto smsAuth;

  private SmsApiService smsApiService;

  public SmsService() {
    this(null);
  }

  public SmsService(MessageAuthDto smsAuth) {
    this(smsAuth, SmsServiceOptions.INSTA_SMS);
  }

  public SmsService(MessageAuthDto smsAuth, SmsServiceOptions smsService) {
    super();
    this.smsAuth = smsAuth;
    if (smsService == null) {
      smsService = SmsServiceOptions.INSTA_SMS;
    }
    smsApiService = (SmsApiService) ApiFactory.create(smsService.getSmsApiService(), smsService.getBaseUrl());
  }

  @Override
  public Future<String> sendMessage(MessageDto messageDto) {
    return sendMessage(smsAuth, messageDto);
  }

  @Override
  public Future<String> sendMessage(MessageAuthDto messageAuthDto, MessageDto messageDto) {
    return Executors.newSingleThreadExecutor().submit(
      () -> send(messageAuthDto, messageDto));
  }

  private String send(MessageAuthDto messageAuthDto, MessageDto messageDto) {
    if (messageAuthDto == null) {
      throw new IllegalArgumentException("SMS Auth cannot be null. Please create one before sending the SMS.");
    }
    try {

      Call<String> call = smsApiService.sendSms(messageAuthDto.getPassword(), messageAuthDto.getFrom(),
        cleanNumber(messageDto.getTo()), messageDto.getContent(), messageDto.getTemplateId(), 3);

      Response<String> response = call.execute();
      if (!response.isSuccessful()) {
        throw new IllegalArgumentException("Some problem happened in sending SMS. Please check your params");
      }
      return response.body();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  private static String cleanNumber(String number) {
    String original = number;
    if (isEmpty(number)) {
      return null;
    }
    number = number.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
    if (number.length() == 12 && allNumeric(number)) {
      number = "+" + number;
    }
    if (number.length() > 10 && number.startsWith("0")) {
      number = number.substring(1);
    }
    if (!number.startsWith("+")) {
      number = "+91" + number;
    }
    if (!allNumeric(number.substring(1))) {
      number = original;
    }
    return number.substring(1);
  }

  private static boolean isEmpty(String number) {
    return number == null || number.isEmpty();
  }

  private static boolean allNumeric(String string) {
    for (char c : string.toCharArray()) {
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }
}

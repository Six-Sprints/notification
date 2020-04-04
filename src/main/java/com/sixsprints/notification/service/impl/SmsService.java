package com.sixsprints.notification.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.SmsInstaService;

import retrofit2.Call;
import retrofit2.Response;

public class SmsService implements NotificationService {

  private MessageAuthDto smsAuth;

  private SmsInstaService smsInstaService;

  public SmsService() {
    this(null);
  }

  public SmsService(MessageAuthDto smsAuth) {
    super();
    this.smsAuth = smsAuth;
    smsInstaService = ApiFactory.create(SmsInstaService.class, SmsInstaService.BASE_URL);
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
      Call<String> call = smsInstaService.sendSms(messageAuthDto.getUsername(), messageAuthDto.getPassword(),
        cleanNumber(messageDto.getTo()), messageAuthDto.getFrom(), messageDto.getContent(), 0, 2);
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

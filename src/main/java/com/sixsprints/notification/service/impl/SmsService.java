package com.sixsprints.notification.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.SmsInstaService;

import retrofit2.Call;
import retrofit2.Response;

@Service("sms")
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
  @Async
  public void sendMessage(MessageDto messageDto) {
    if (smsAuth == null) {
      throw new IllegalArgumentException("SMS Auth cannot be null. Please create one before sending the SMS.");
    }
    sendMessage(smsAuth, messageDto);
  }

  @Override
  @Async
  public void sendMessage(MessageAuthDto messageAuthDto, MessageDto messageDto) {
    try {
      Call<String> call = smsInstaService.sendSms(messageAuthDto.getUsername(), messageAuthDto.getPassword(),
        cleanNumber(messageDto.getTo()), messageAuthDto.getFrom(), messageDto.getContent(), 0, 2);
      Response<String> response = call.execute();
      if (!response.isSuccessful()) {
        throw new IllegalArgumentException("Some problem happened in sending SMS. Please check your params");
      }
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  private static String cleanNumber(String number) {
    String original = number;
    if (StringUtils.isEmpty(number)) {
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

  private static boolean allNumeric(String string) {
    for (char c : string.toCharArray()) {
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }
}

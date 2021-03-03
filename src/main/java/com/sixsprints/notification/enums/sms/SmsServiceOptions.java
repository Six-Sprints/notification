package com.sixsprints.notification.enums.sms;

import com.sixsprints.notification.service.SmsInstaService;
import com.sixsprints.notification.service.TextlocalSmsService;

public enum SmsServiceOptions {

  INSTA_SMS(SmsInstaService.class, SmsInstaService.BASE_URL),
  TEXT_LOCAL(TextlocalSmsService.class, TextlocalSmsService.BASE_URL);

  private SmsServiceOptions(Class<?> smsApiService, String baseUrl) {
    this.smsApiService = smsApiService;
    this.baseUrl = baseUrl;
  }

  private Class<?> smsApiService;

  private String baseUrl;

  public Class<?> getSmsApiService() {
    return smsApiService;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

}

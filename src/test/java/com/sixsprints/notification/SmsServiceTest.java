package com.sixsprints.notification;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.enums.sms.SmsServiceOptions;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.SmsService;

import junit.framework.TestCase;

public class SmsServiceTest extends TestCase {

  private MessageAuthDto testAuth = MessageAuthDto.builder()
    .password("")
    .from("")
    .build();

  private NotificationService smsService = new SmsService(testAuth, SmsServiceOptions.INSTA_SMS);

  public void testShouldSendSms() throws InterruptedException, ExecutionException {
    MessageDto emailDto = MessageDto.builder().to("(+91)9810306710").content(
      "1234 is your confidential OTP for Logging in to your Proclinic Account.")
      .templateId("1207161779479384687")
      .build();
    Future<String> future = smsService.sendMessage(emailDto);

    System.out.println(future.get());
  }

}

package com.sixsprints.notification;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.SmsService;

import junit.framework.TestCase;

public class SmsServiceTest extends TestCase {

  private MessageAuthDto testAuth = MessageAuthDto.builder()
    .from("VIASMS")
    .password("c35cdbcb-3f4c-4810-b510-c40d424529fc")
    .username("d8ff2209-ce64-4a42-966c-2a3f64f44dee")
    .build();

  private NotificationService smsService = new SmsService(testAuth);

  public void testShouldSendSms() throws InterruptedException, ExecutionException {
    MessageDto emailDto = MessageDto.builder().to("(+91)9810306710").content("Test SMS")
      .build();
    Future<String> future = smsService.sendMessage(emailDto);

    System.out.println(future.get());
  }

}

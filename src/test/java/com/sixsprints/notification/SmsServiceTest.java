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

    String url = "https://cutt.ly/EnsEkQN";

    MessageDto messageDto = MessageDto.builder()
      .to("(+91)9810306710")
      .templateId("1207161467070888886")
      .subject("Prescription is ready")
      .content(
        "Hi Karan. Your prescription is ready and can be accessed via the below link:\n" + url)
      .build();

    Future<String> future = smsService.sendMessage(messageDto);

    System.out.println(future.get());
  }

}

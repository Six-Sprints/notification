package com.sixsprints.notification;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.SmsService;

import junit.framework.TestCase;

public class SmsServiceTest extends TestCase {

  private MessageAuthDto testAuth;

  private NotificationService smsService = new SmsService(testAuth);

  public void testShouldSendSms() {
    MessageDto emailDto = MessageDto.builder().to("(+91)9810306710").content("Test SMS")
      .build();
    smsService.sendMessage(emailDto);
  }

}

package com.sixsprints.notification;

import org.junit.jupiter.api.Test;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.SmsService;

public class SmsServiceTest extends ApplicationTests {

  private MessageAuthDto testAuth = MessageAuthDto.builder().from("SSPRNT")
    .password("c35cdbcb-3f4c-4810-b510-c40d424529fc").username("d8ff2209-ce64-4a42-966c-2a3f64f44dee").build();

  private NotificationService smsService = new SmsService(testAuth);

  @Test
  public void shouldSendSms() {
    MessageDto emailDto = MessageDto.builder().to("(+91)9810306710").content("Test SMS")
      .build();
    smsService.sendMessage(emailDto);
  }

}

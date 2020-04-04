package com.sixsprints.notification;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;

import junit.framework.TestCase;

public class EmailServiceTest extends TestCase {

  private NotificationService emailService;

  private MessageAuthDto testAuth;

  public void testShouldSendEmail() {

    MessageDto emailDto = MessageDto.builder().to("kgujral@gmail.com").subject("Test Email")
      .content("<b>TEST</b> Email! Support HTML content!")
      .build();

    emailService.sendMessage(testAuth, emailDto);

  }

}

package com.sixsprints.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;

public class EmailServiceTest extends ApplicationTests {

  @Autowired
  @Qualifier("email")
  private NotificationService emailService;

  @Autowired
  private MessageAuthDto testAuth;

  @Test
  public void shouldSendEmail() {

    MessageDto emailDto = MessageDto.builder().to("kgujral@gmail.com").subject("Test Email")
      .content("<b>TEST</b> Email! Support HTML content!")
      .build();

    emailService.sendMessage(testAuth, emailDto);

  }

}

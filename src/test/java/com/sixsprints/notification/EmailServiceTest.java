package com.sixsprints.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sixsprints.notification.dto.EmailAuthDto;
import com.sixsprints.notification.dto.EmailDto;
import com.sixsprints.notification.service.EmailService;

public class EmailServiceTest extends ApplicationTests {

  @Autowired
  private EmailService emailService;

  @Autowired
  private EmailAuthDto testAuth;

  @Test
  public void shouldSendEmail() {

    EmailDto emailDto = EmailDto.builder().to("kgujral@gmail.com").subject("Test Email")
      .content("<b>TEST</b> Email! Support HTML content!")
      .build();

    emailService.sendMail(testAuth, emailDto);

  }

}

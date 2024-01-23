package com.sixsprints.notification;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.EmailServiceSmtp;

public class EmailServiceTest {

  private MessageAuthDto testAuth = MessageAuthDto.builder()
    .from("")
    .fromEmail("")
    .hostName("")
    .username("")
    .password("")
    .sslEnabled(true)
    .build();

  private NotificationService emailService = new EmailServiceSmtp(testAuth);

  @Test
  public void testShouldSendEmail() throws InterruptedException, ExecutionException {

    MessageDto emailDto = MessageDto.builder()
      .to("kgujral@gmail.com")
      .subject("Test Email")
      .templateId("email")
      .templateValues(Map.of("otp", "123456"))
      .build();

    Future<String> sendMessage = emailService.sendMessage(testAuth, emailDto);
    System.out.println(sendMessage.get());

  }

}

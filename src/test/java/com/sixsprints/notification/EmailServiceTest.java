package com.sixsprints.notification;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.EmailServiceSmtp;

import junit.framework.TestCase;

public class EmailServiceTest extends TestCase {

  private MessageAuthDto testAuth = MessageAuthDto.builder()
    .from("")
    .hostName("")
    .password("")
    .username("")
    .sslEnabled(true)
    .build();

  private NotificationService emailService = new EmailServiceSmtp(testAuth);

  public void testShouldSendEmail() throws InterruptedException, ExecutionException {

    MessageDto emailDto = MessageDto.builder().to("kgujral@gmail.com").subject("Test Email")
      .content("<b>TEST3</b> Email! Support HTML content!")
      .build();

    Future<String> sendMessage = emailService.sendMessage(testAuth, emailDto);
    System.out.println(sendMessage.get());

  }

}

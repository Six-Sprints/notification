package com.sixsprints.notification.service.impl;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;

@Service("email")
public class EmailServiceSmtp implements NotificationService {

  private MessageAuthDto emailAuth;

  public EmailServiceSmtp() {
  }

  public EmailServiceSmtp(MessageAuthDto emailAuth) {
    super();
    this.emailAuth = emailAuth;
  }

  @Async
  @Override
  public void sendMessage(MessageDto emailDto) {
    if (emailAuth == null) {
      throw new IllegalArgumentException("Email Auth cannot be null. Please create one before sending the mail.");
    }
    sendMessage(emailAuth, emailDto);
  }

  @Async
  @Override
  public void sendMessage(MessageAuthDto emailAuthDto, MessageDto emailDto) {
    try {
      HtmlEmail email = emailClient(emailAuthDto);
      email.setFrom(emailAuthDto.getUsername(), emailAuthDto.getFrom());
      email.addTo(emailDto.getTo());
      email.setSubject(emailDto.getSubject());
      email.setHtmlMsg(emailDto.getContent());
      email.setTextMsg(emailDto.getContent());
      email.send();
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  private HtmlEmail emailClient(MessageAuthDto emailAuthDto) {
    HtmlEmail email = new HtmlEmail();
    email.setHostName(emailAuthDto.getHostName());
    email.setAuthenticator(new DefaultAuthenticator(emailAuthDto.getUsername(), emailAuthDto.getPassword()));
    email.setSSLOnConnect(emailAuthDto.isSslEnabled());
    email.setSslSmtpPort(emailAuthDto.getSslSmtpPort());
    return email;
  }

}

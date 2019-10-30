package com.sixsprints.notification.service.impl;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sixsprints.notification.dto.EmailAuthDto;
import com.sixsprints.notification.dto.EmailDto;
import com.sixsprints.notification.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

  private EmailAuthDto emailAuth;

  public EmailServiceImpl() {
  }

  public EmailServiceImpl(EmailAuthDto emailAuth) {
    super();
    this.emailAuth = emailAuth;
  }

  @Async
  @Override
  public void sendMail(EmailDto emailDto) {
    if (emailAuth == null) {
      throw new IllegalArgumentException("Email Auth cannot be null. Please create one before sending the mail.");
    }
    sendMail(emailAuth, emailDto);
  }

  @Async
  @Override
  public void sendMail(EmailAuthDto emailAuthDto, EmailDto emailDto) {
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

  private HtmlEmail emailClient(EmailAuthDto emailAuthDto) {
    HtmlEmail email = new HtmlEmail();
    email.setHostName(emailAuthDto.getHostName());
    email.setAuthenticator(new DefaultAuthenticator(emailAuthDto.getUsername(), emailAuthDto.getPassword()));
    email.setSSLOnConnect(emailAuthDto.isSslEnabled());
    email.setSslSmtpPort(emailAuthDto.getSslSmtpPort());
    return email;
  }

}

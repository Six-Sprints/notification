package com.sixsprints.notification.service.impl;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sixsprints.notification.dto.EmailAuthDto;
import com.sixsprints.notification.dto.EmailDto;
import com.sixsprints.notification.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

  @Async
  @Override
  public void sendMail(EmailAuthDto emailAuthDto, EmailDto emailDto) throws EmailException {
    HtmlEmail email = emailClient(emailAuthDto);
    email.setFrom(emailAuthDto.getUsername(), emailAuthDto.getFrom());
    email.addTo(emailDto.getTo());
    email.setSubject(emailDto.getSubject());
    email.setHtmlMsg(emailDto.getContent());
    email.setTextMsg(emailDto.getContent());
    email.send();
  }

  private HtmlEmail emailClient(EmailAuthDto emailAuthDto) {
    HtmlEmail email = new HtmlEmail();
    email.setHostName(emailAuthDto.getHostName());
    email.setAuthenticator(new DefaultAuthenticator(emailAuthDto.getUsername(), emailAuthDto.getPassword()));
    return email;
  }

}

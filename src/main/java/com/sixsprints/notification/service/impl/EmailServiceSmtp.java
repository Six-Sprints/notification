package com.sixsprints.notification.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;

public class EmailServiceSmtp implements NotificationService {

  private MessageAuthDto emailAuth;

  public EmailServiceSmtp() {
  }

  public EmailServiceSmtp(MessageAuthDto emailAuth) {
    super();
    this.emailAuth = emailAuth;
  }

  @Override
  public Future<String> sendMessage(MessageDto emailDto) {
    return sendMessage(emailAuth, emailDto);
  }

  @Override
  public Future<String> sendMessage(MessageAuthDto emailAuthDto, MessageDto emailDto) {
    return Executors.newSingleThreadExecutor().submit(() -> send(emailAuthDto, emailDto));
  }

  private String send(MessageAuthDto emailAuthDto, MessageDto emailDto) {
    if (emailAuthDto == null) {
      throw new IllegalArgumentException("Email Auth cannot be null. Please create one before sending the mail.");
    }
    try {
      // Create the email message
      String from = emailAuthDto.getFromEmail();
      HtmlEmail email = emailClient(emailAuthDto);
      email.setFrom(!isEmpty(from) ? from : emailAuthDto.getUsername(),
        emailAuthDto.getFrom());
      email.addTo(emailDto.getTo());
      email.setSubject(emailDto.getSubject());
      email.setMsg(emailDto.getContent());
      email.setHtmlMsg(emailDto.getContent());
      attach(emailDto, email);
      return email.send();
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  private void attach(MessageDto emailDto, MultiPartEmail email) throws MalformedURLException, EmailException {
    if (emailDto.getAttachment() == null || emailDto.getAttachment().getAttachmentUrl() == null
      || emailDto.getAttachment().getAttachmentUrl().isEmpty()) {
      return;
    }
    EmailAttachment attachment = new EmailAttachment();
    attachment.setURL(new URL(emailDto.getAttachment().getAttachmentUrl()));
    attachment.setDisposition(EmailAttachment.ATTACHMENT);
    attachment.setName(emailDto.getAttachment().getName());
    attachment.setDescription(emailDto.getAttachment().getDescription());
    email.attach(attachment);
  }

  private HtmlEmail emailClient(MessageAuthDto emailAuthDto) {
    HtmlEmail email = new HtmlEmail();
    email.setHostName(emailAuthDto.getHostName());
    email.setAuthenticator(new DefaultAuthenticator(emailAuthDto.getUsername(), emailAuthDto.getPassword()));
    email.setSSLOnConnect(emailAuthDto.isSslEnabled());
    email.setSslSmtpPort(emailAuthDto.getSslSmtpPort());
    email.setCharset("UTF-8");
    return email;
  }

  private boolean isEmpty(String string) {
    return string == null || string.isEmpty();
  }

}

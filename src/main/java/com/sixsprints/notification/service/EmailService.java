package com.sixsprints.notification.service;

import org.apache.commons.mail.EmailException;

import com.sixsprints.notification.dto.EmailAuthDto;
import com.sixsprints.notification.dto.EmailDto;

public interface EmailService {

  void sendMail(EmailAuthDto emailAuthDto, EmailDto emailDto) throws EmailException;

  void sendMail(EmailDto emailDto) throws EmailException;

}

package com.sixsprints.notification.service;

import com.sixsprints.notification.dto.EmailAuthDto;
import com.sixsprints.notification.dto.EmailDto;

public interface EmailService {

  void sendMail(EmailAuthDto emailAuthDto, EmailDto emailDto);

  void sendMail(EmailDto emailDto);

}

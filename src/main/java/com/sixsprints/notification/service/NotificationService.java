package com.sixsprints.notification.service;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;

public interface NotificationService {

  void sendMessage(MessageAuthDto emailAuthDto, MessageDto emailDto);

  void sendMessage(MessageDto emailDto);

}

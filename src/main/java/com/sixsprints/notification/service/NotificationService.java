package com.sixsprints.notification.service;

import java.util.concurrent.Future;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.dto.MessageDto;

public interface NotificationService {

  Future<String> sendMessage(MessageAuthDto messageAuthDto, MessageDto messageDto);

  Future<String> sendMessage(MessageDto messageDto);

}

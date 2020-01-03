package com.sixsprints.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

  private String to;

  private String subject;

  private String content;

}

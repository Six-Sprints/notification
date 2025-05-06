package com.sixsprints.notification.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

  private String to;

  private List<String> cc;

  private List<String> bcc;

  private String subject;

  private String content;

  private AttachmentDto attachment;

  private String templateId;
  
  private Object templateValues;

}

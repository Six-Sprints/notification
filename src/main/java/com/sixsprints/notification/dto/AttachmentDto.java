package com.sixsprints.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentDto {

  private String attachmentUrl;

  private String name;

  private String description;

}

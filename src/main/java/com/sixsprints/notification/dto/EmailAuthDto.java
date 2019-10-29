package com.sixsprints.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailAuthDto {

  private String from;

  private String hostName;

  private String username;

  private String password;

}

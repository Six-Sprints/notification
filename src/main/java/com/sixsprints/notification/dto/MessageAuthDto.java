package com.sixsprints.notification.dto;

import java.time.Duration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageAuthDto {

  private String from;

  private String fromEmail;

  private String hostName;

  private String username;

  private String password;

  private boolean sslEnabled;
  
  private Duration frequency;

  @Builder.Default
  private String sslSmtpPort = "465";

}

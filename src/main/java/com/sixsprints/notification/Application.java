package com.sixsprints.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sixsprints.notification.dto.EmailAuthDto;

@SpringBootApplication
@EnableAsync
public class Application {

  @Value(value = "${email.from}")
  private String from;

  @Value(value = "${email.hostname}")
  private String hostName;

  @Value(value = "${email.username}")
  private String username;

  @Value(value = "${email.password}")
  private String password;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public EmailAuthDto testAuth() {
    return EmailAuthDto.builder().from(from).hostName(hostName)
      .username(username).password(password).sslEnabled(true)
      .build();
  }

}

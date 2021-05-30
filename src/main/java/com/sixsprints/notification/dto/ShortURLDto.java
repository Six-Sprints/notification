package com.sixsprints.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortURLDto {

  private Integer status;

  private String fullLink;

  private String shortLink;

  private String title;

}

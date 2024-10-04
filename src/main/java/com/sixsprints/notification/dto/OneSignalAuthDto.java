package com.sixsprints.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneSignalAuthDto {

	@Builder.Default
	private String url = "https://onesignal.com/api/v1";

	private String appId;
	private String appKeyToken;
	private String userKeyToken;
	private String endPoint;

}

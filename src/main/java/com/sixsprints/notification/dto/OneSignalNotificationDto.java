package com.sixsprints.notification.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.onesignal.client.model.Notification;
import com.onesignal.client.model.StringMap;
import com.sixsprints.notification.enums.onesignal.NotificationTypeEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneSignalNotificationDto {

	private List<String> userSlugs;

	@Builder.Default
	private NotificationTypeEnum notificationTypes = NotificationTypeEnum.PUSH;

	// Content max limit 150 characters
	private StringMap contents;
	// Heading max limit 25-50 characters
	private StringMap headings;
	// Mobile Device URL within app
	private String app_url;
	// Web Device URL
	private String web_url;
	// Custom Data Object
	private Map<String, Object> customData;

	@Builder.Default
	private Long ttl = new Date().getTime();
	@Builder.Default
	private Integer priority = 5;

	@Builder.Default
	private boolean isIos = false;
	@Builder.Default
	private boolean isAndroid = false;
	@Builder.Default
	private boolean isAnyWeb = true;

	private List<Notification> notifications;

}

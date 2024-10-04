package com.sixsprints.notification.dto;

import java.util.List;

import com.onesignal.client.model.SubscriptionObject;
import com.onesignal.client.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneSignalUserDto {

	public static final String EXTERNAL_ID = "external_id";

	private String slug;
	private String email;
	private List<SubscriptionObject> subscriptions;
	User oneSignalUser;

}

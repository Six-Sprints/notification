package com.sixsprints.notification.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.onesignal.client.ApiClient;
import com.onesignal.client.ApiException;
import com.onesignal.client.Configuration;
import com.onesignal.client.api.DefaultApi;
import com.onesignal.client.auth.HttpBearerAuth;
import com.onesignal.client.model.CreateNotificationSuccessResponse;
import com.onesignal.client.model.CreateSubscriptionRequestBody;
import com.onesignal.client.model.InlineResponse201;
import com.onesignal.client.model.Notification;
import com.onesignal.client.model.Notification.TargetChannelEnum;
import com.onesignal.client.model.PlayerNotificationTargetIncludeAliases;
import com.onesignal.client.model.SubscriptionObject;
import com.onesignal.client.model.User;
import com.sixsprints.notification.dto.OneSignalAuthDto;
import com.sixsprints.notification.dto.OneSignalNotificationDto;
import com.sixsprints.notification.dto.OneSignalUserDto;
import com.sixsprints.notification.enums.onesignal.NotificationTypeEnum;
import com.sixsprints.notification.service.OneSignalConfigureAndPushService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OneSignalConfigureAndPushServiceImpl implements OneSignalConfigureAndPushService {

	private static DefaultApi api;
	private OneSignalAuthDto oneSignalAuthDto;

	public OneSignalConfigureAndPushServiceImpl() {
	}

	public OneSignalConfigureAndPushServiceImpl(OneSignalAuthDto oneSignalAuthDto) {
		super();
		this.oneSignalAuthDto = oneSignalAuthDto;
	}

	private DefaultApi getOneSignalApiInstance() {
		if (ObjectUtils.isEmpty(api)) {
			log.info("getOneSignalApiInstance(): New Instance.");
			// Recommended to create multiple instance per request
			ApiClient defaultClient = Configuration.getDefaultApiClient();
			HttpBearerAuth appKey = (HttpBearerAuth) defaultClient.getAuthentication("app_key");
			appKey.setBearerToken(oneSignalAuthDto.getAppKeyToken());
			HttpBearerAuth userKey = (HttpBearerAuth) defaultClient.getAuthentication("user_key");
			userKey.setBearerToken(oneSignalAuthDto.getUserKeyToken());
			return new DefaultApi(defaultClient);
		} else {
			log.info("getOneSignalApiInstance(): OLD Instance.");
			return api;
		}
	}

	@Override
	public OneSignalUserDto getOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("getOneSignalUser(): {}", userDto);
		try {
			User oneSignalUser = getOneSignalApiInstance().fetchUser(oneSignalAuthDto.getAppId(),
					OneSignalUserDto.EXTERNAL_ID, userDto.getSlug());
			userDto.setOneSignalUser(oneSignalUser);
			log.info("getOneSignalUser(): {}", oneSignalUser.toJson());
		} catch (ApiException e) {
			log.info("getOneSignalUser(): Error {}", e);
			e.printStackTrace();
		}
		return userDto;
	}

	@Override
	public OneSignalUserDto createOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("createOneSignalUser(): {}", userDto);
		List<SubscriptionObject> subscriptionObjects = new ArrayList<>();

		for (SubscriptionObject requestedSubscription : userDto.getSubscriptions()) {
			if (!ObjectUtils.isEmpty(requestedSubscription.getType())
					&& StringUtils.isNotBlank(requestedSubscription.getToken())) {
				requestedSubscription.setEnabled(true);
				subscriptionObjects.add(requestedSubscription);
			}
		}

		User user = new User();
		user.setSubscriptions(subscriptionObjects);
		user.setIdentity(Map.of(OneSignalUserDto.EXTERNAL_ID, userDto.getSlug()));
		try {
			User oneSignalUser = getOneSignalApiInstance().createUser(oneSignalAuthDto.getAppId(), user);
			userDto.setOneSignalUser(oneSignalUser);
			userDto.setSubscriptions(oneSignalUser.getSubscriptions());
			log.info("createOneSignalUser(): {}", oneSignalUser.toJson());
		} catch (ApiException e) {
			log.info("createOneSignalUser(): Error {}", e);
			e.printStackTrace();
		}
		return userDto;

	}

	@Override
	public OneSignalUserDto updateOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("updateOneSignalUser(): {}", userDto);
		List<SubscriptionObject> subscriptionObjects = new ArrayList<>();

		for (SubscriptionObject requestedSubscription : userDto.getSubscriptions()) {
			if (!ObjectUtils.isEmpty(requestedSubscription.getType())
					&& StringUtils.isNotBlank(requestedSubscription.getToken())) {
				subscriptionObjects.add(requestedSubscription);
			}
		}

		if (!ObjectUtils.isEmpty(subscriptionObjects)) {
			OneSignalUserDto oneSignalUserDto = getOneSignalUser(userDto);
			List<SubscriptionObject> userCurrentSubscriptions = oneSignalUserDto.getSubscriptions();
			for (SubscriptionObject newSubscriptionObject : subscriptionObjects) {
				SubscriptionObject alreadySubscribedObject = userCurrentSubscriptions.stream()
						.filter(e -> e.getType().equals(newSubscriptionObject.getType())).findFirst().orElse(null);
				if (!ObjectUtils.isEmpty(alreadySubscribedObject)
						&& !alreadySubscribedObject.getToken().equals(newSubscriptionObject.getToken())) {
					log.info("updateOneSignalUser():deleteSubscription(): Found alreadySubscribedObject {}",
							alreadySubscribedObject.toJson());
					try {
						getOneSignalApiInstance().deleteSubscription(oneSignalAuthDto.getAppId(),
								alreadySubscribedObject.getId());
						System.out.println("Deleted Old Token");
						log.info("updateOneSignalUser():deleteSubscription(): Deleted Found alreadySubscribedObject {}",
								alreadySubscribedObject.toJson());
					} catch (ApiException e) {
						log.info("updateOneSignalUser():deleteSubscription(): Error {}", e);
						e.printStackTrace();
					}
				}
				if (ObjectUtils.isEmpty(alreadySubscribedObject) || (!ObjectUtils.isEmpty(alreadySubscribedObject)
						&& !alreadySubscribedObject.getToken().equals(newSubscriptionObject.getToken()))) {
					newSubscriptionObject.setEnabled(true);
					CreateSubscriptionRequestBody createSubscriptionRequestBody = new CreateSubscriptionRequestBody();
					createSubscriptionRequestBody.setSubscription(newSubscriptionObject);
					try {
						log.info("updateOneSignalUser():createSubscription(): Creating New Subscription {}",
								newSubscriptionObject.toJson());
						InlineResponse201 result = getOneSignalApiInstance().createSubscription(
								oneSignalAuthDto.getAppId(), OneSignalUserDto.EXTERNAL_ID, userDto.getSlug(),
								createSubscriptionRequestBody);
						System.out.println("Added New Token");
						log.info("updateOneSignalUser():createSubscription(): Created New Subscription {}",
								result.toJson());
					} catch (ApiException e) {
						log.info("updateOneSignalUser():createSubscription(): Error {}", e);
						e.printStackTrace();
					}
				}
			}
		}
		return userDto;

	}

	@Override
	public void deleteOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("deleteOneSignalUser(): {}", userDto);
		try {
			getOneSignalApiInstance().deleteUser(oneSignalAuthDto.getAppId(), OneSignalUserDto.EXTERNAL_ID,
					userDto.getSlug());
			log.info("deleteOneSignalUser(): Deleted Successfull If Exists");
		} catch (ApiException e) {
			log.info("deleteOneSignalUser(): Error {}", e);
			e.printStackTrace();
		}
	}

	@Override
	public OneSignalNotificationDto createOneSignalNotification(OneSignalNotificationDto notificationDto)
			throws ApiException {
		log.info("createOneSignalNotification(): {}", notificationDto);
		// Setting up the notification
		Notification notification = createNotification(notificationDto);
		log.info("createOneSignalNotification(): {}", notification);

		if (!ObjectUtils.isEmpty(notification)) {
			// Sending the request
			CreateNotificationSuccessResponse response = getOneSignalApiInstance().createNotification(notification);
			// Checking the result
			if (!ObjectUtils.isEmpty(response.getErrors())) {
				log.info("createOneSignalNotification(): Errors {}", response.getErrors().toJson());
			} else {
				notificationDto.setNotifications(List.of(notification));
				log.info("createOneSignalNotification(): Success {}", response.toJson());
			}
		}
		return notificationDto;
	}

	private Notification createNotification(OneSignalNotificationDto notificationDto) throws ApiException {
		if (notificationDto.getNotificationTypes().equals(NotificationTypeEnum.PUSH)) {
			Notification notification = new Notification();
			notification.setAppId(oneSignalAuthDto.getAppId());
			notification.setIsChrome(true);
			notification.setIsFirefox(true);
			notification.setIsSafari(true);
			notification.setIsAnyWeb(true);
			notification.setIsAndroid(true);
			notification.setIsIos(true);
			PlayerNotificationTargetIncludeAliases includeAliases = new PlayerNotificationTargetIncludeAliases();
			includeAliases.setAliasLabel(List.of(OneSignalUserDto.EXTERNAL_ID));
			notification.setIncludeAliases(includeAliases);
			notification.setIncludeExternalUserIds(notificationDto.getUserSlugs());
			notification.targetChannel(TargetChannelEnum.PUSH);
			notification.setIncludedSegments(Arrays.asList(new String[] { "Subscribed Users" }));
			notification.setHeadings(notificationDto.getHeadings());
			notification.setContents(notificationDto.getContents());
			notification.setWebUrl(notificationDto.getWeb_url());
			notification.setAppUrl(notificationDto.getApp_url());
			notification.setPriority(notificationDto.getPriority());
			return notification;
		} else if (notificationDto.getNotificationTypes().equals(NotificationTypeEnum.IN_APP)
				|| notificationDto.getNotificationTypes().equals(NotificationTypeEnum.EMAIL)
				|| notificationDto.getNotificationTypes().equals(NotificationTypeEnum.SMS)) {
			log.error("Non Configured Notification");
		}
		return null;
	}

}

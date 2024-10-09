package com.sixsprints.notification.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.onesignal.client.model.NotificationWithMeta;
import com.onesignal.client.model.PropertiesObject;
import com.onesignal.client.model.SubscriptionObject;
import com.onesignal.client.model.UpdateSubscriptionRequestBody;
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
			if (StringUtils.isNotEmpty(oneSignalAuthDto.getAppKeyToken())) {
				HttpBearerAuth appKey = (HttpBearerAuth) defaultClient.getAuthentication("app_key");
				appKey.setBearerToken(oneSignalAuthDto.getAppKeyToken());
			} else if (StringUtils.isNotEmpty(oneSignalAuthDto.getUserKeyToken())) {
				HttpBearerAuth userKey = (HttpBearerAuth) defaultClient.getAuthentication("user_key");
				userKey.setBearerToken(oneSignalAuthDto.getUserKeyToken());
			}
			return new DefaultApi(defaultClient);
		} else {
			log.info("getOneSignalApiInstance(): OLD Instance.");
			return api;
		}
	}

	@Override
	public OneSignalUserDto getOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("getOneSignalUser(): User Slug {}", userDto.getSlug());
		try {
			User oneSignalUser = getOneSignalApiInstance().fetchUser(oneSignalAuthDto.getAppId(),
					OneSignalUserDto.EXTERNAL_ID, userDto.getSlug());
			userDto.setOneSignalUser(oneSignalUser);
			log.info("getOneSignalUser(): User Slug {}, One Signal User Id {}", userDto.getSlug(),
					userDto.getOneSignalUser().getIdentity());
		} catch (Exception ex) {
			log.info("getOneSignalUser(): User Slug {}, One Signal User Not Found", userDto.getSlug());
		}
		return userDto;
	}

	@Override
	public OneSignalUserDto createOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("createOneSignalUser(): User Slug {}", userDto.getSlug());
		List<SubscriptionObject> subscriptionObjects = new ArrayList<>();

		userDto = getOneSignalUser(userDto);
		if (!ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
			throw new ApiException("User Already Exists with Slug " + userDto.getSlug());
		}

		if (!ObjectUtils.isEmpty(userDto.getSubscriptions())) {
			for (SubscriptionObject requestedSubscription : userDto.getSubscriptions()) {
				if (!ObjectUtils.isEmpty(requestedSubscription.getType())
						&& StringUtils.isNotBlank(requestedSubscription.getToken())) {
					requestedSubscription.setEnabled(true);
					subscriptionObjects.add(requestedSubscription);
				}
			}
		}

		log.info("createOneSignalUser(): User Slug {}, Subscriptions {}", userDto.getSlug(), subscriptionObjects);
		User user = new User();
		user.setSubscriptions(subscriptionObjects);
		user.setIdentity(Map.of(OneSignalUserDto.EXTERNAL_ID, userDto.getSlug()));
		PropertiesObject propertiesObject = new PropertiesObject();
		propertiesObject.setTags(Map.of(OneSignalUserDto.USER_SLUG, userDto.getSlug()));
		user.setProperties(propertiesObject);
		User oneSignalUser = getOneSignalApiInstance().createUser(oneSignalAuthDto.getAppId(), user);
		if (ObjectUtils.isEmpty(oneSignalUser)) {
			throw new ApiException("Unable to create User with Slug {}" + userDto.getSlug());
		}
		userDto.setOneSignalUser(oneSignalUser);
		userDto.setSubscriptions(oneSignalUser.getSubscriptions());
		log.info("createOneSignalUser(): User Slug {}, User OneSignal Id {}", userDto.getSlug(),
				userDto.getOneSignalUser().getIdentity());
		if (!ObjectUtils.isEmpty(subscriptionObjects) && ObjectUtils.isEmpty(userDto.getSubscriptions())) {
			throw new ApiException("Unable to create subscription For User with Slug {}" + userDto.getSlug());
		}
		return userDto;
	}

	@Override
	public OneSignalUserDto updateOneSignalUser(OneSignalUserDto userDto, boolean deleteOtherSameTypeSubscriptions)
			throws ApiException {
		log.info("updateOneSignalUser(): User Slug {}", userDto.getSlug());
		List<SubscriptionObject> subscriptionObjects = new ArrayList<>();

		userDto = getOneSignalUser(userDto);
		if (ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
			log.info("updateOneSignalUser(): User Slug {} User Does Not Exist Create New User", userDto.getSlug());
			userDto = createOneSignalUser(userDto);
		} else {
			log.info("updateOneSignalUser(): User Slug {} User Exists Updating User with OneSignal Id {}",
					userDto.getSlug(), userDto.getOneSignalUser().getIdentity());

			if (!ObjectUtils.isEmpty(userDto.getSubscriptions())) {
				for (SubscriptionObject requestedSubscription : userDto.getSubscriptions()) {
					if (!ObjectUtils.isEmpty(requestedSubscription.getType())
							&& StringUtils.isNotBlank(requestedSubscription.getToken())) {
						subscriptionObjects.add(requestedSubscription);
					}
				}
			}

			if (!ObjectUtils.isEmpty(subscriptionObjects)) {
				List<SubscriptionObject> userCurrentSubscriptions = new ArrayList<>();
				if (!ObjectUtils.isEmpty(userDto.getOneSignalUser())
						&& !ObjectUtils.isEmpty(userDto.getOneSignalUser().getSubscriptions())) {
					userCurrentSubscriptions = userDto.getOneSignalUser().getSubscriptions();
					log.info("updateOneSignalUser(): User Slug {}, Existing OneSignal Subscriptions {}",
							userDto.getSlug(), userCurrentSubscriptions);
				}

				for (SubscriptionObject newSubscriptionObject : subscriptionObjects) {
					log.info("updateOneSignalUser(): User Slug {}, Checking For Subscription {}", userDto.getSlug(),
							newSubscriptionObject);
					List<SubscriptionObject> alreadySubscribedObjects = userCurrentSubscriptions.stream()
							.filter(e -> e.getType().equals(newSubscriptionObject.getType()))
							.collect(Collectors.toList());
					if (ObjectUtils.isEmpty(alreadySubscribedObjects)) {
						log.info("updateOneSignalUser(): User Slug {}, Creating Fresh Subscription {}",
								userDto.getSlug(), newSubscriptionObject);
						// No Subscription For Current Device Type Add New
						createOneSignalUserSubscription(userDto, newSubscriptionObject);
					} else {
						boolean alreadyFound = false;
						for (SubscriptionObject alreadySubscribedObject : alreadySubscribedObjects) {
							if (!alreadySubscribedObject.getToken().equals(newSubscriptionObject.getToken())) {
								if (deleteOtherSameTypeSubscriptions) {
									log.info("updateOneSignalUser(): User Slug {}, Deleting Old Subscription {}",
											userDto.getSlug(), alreadySubscribedObject);
									// Deleting Existing Subscription
									deleteOneSignalUserSubscription(userDto, alreadySubscribedObject);
								} else {
									log.info("updateOneSignalUser(): User Slug {}, Disabling Old Subscription {}",
											userDto.getSlug(), alreadySubscribedObject);
									// Disable Existing Subscription
									alreadySubscribedObject.setEnabled(false);
									updateOneSignalUserSubscription(userDto, alreadySubscribedObject);
								}
							} else if (alreadySubscribedObject.getToken().equals(newSubscriptionObject.getToken())
									&& !alreadySubscribedObject.getEnabled()) {
								log.info("updateOneSignalUser(): User Slug {}, Enabling Old Disabled Subscription {}",
										userDto.getSlug(), newSubscriptionObject);
								// Enable Old Subscription if It is in disabled state
								alreadySubscribedObject.setEnabled(true);
								updateOneSignalUserSubscription(userDto, alreadySubscribedObject);
								alreadyFound = true;
							} else if (alreadySubscribedObject.getToken().equals(newSubscriptionObject.getToken())
									&& alreadySubscribedObject.getEnabled()) {
								log.info("updateOneSignalUser(): User Slug {}, Already Enabled Subscription Found {}",
										userDto.getSlug(), alreadySubscribedObject);
								alreadyFound = true;
							}
						}
						if (!alreadyFound) {
							log.info("updateOneSignalUser(): User Slug {}, Creating Fresh Subscription {}",
									userDto.getSlug(), newSubscriptionObject);
							// Add New Subscription
							createOneSignalUserSubscription(userDto, newSubscriptionObject);
						}
					}

				}
				addDelay();
				userDto = getOneSignalUser(userDto);
				log.info("updateOneSignalUser(): User Slug {}, One Signal User After Delay {}", userDto.getSlug(),
						userDto.getOneSignalUser());
			} else {
				log.info("updateOneSignalUser(): User Slug {}, No Valid Subscriptions To Update {}", userDto.getSlug(),
						userDto.getSubscriptions());
			}
		}
		return userDto;
	}

	@Override
	public void deleteOneSignalUser(OneSignalUserDto userDto) throws ApiException {
		log.info("deleteOneSignalUser(): User Slug {}", userDto.getSlug());
		userDto = getOneSignalUser(userDto);
		if (ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
			log.info("deleteOneSignalUser(): User Slug {} User Does Not Exist", userDto.getSlug());
		} else {
			getOneSignalApiInstance().deleteUser(oneSignalAuthDto.getAppId(), OneSignalUserDto.EXTERNAL_ID,
					userDto.getSlug());
			log.info("deleteOneSignalUser(): Deleted Successfull If Exists");
		}
	}

	@Override
	public OneSignalNotificationDto createOneSignalNotification(OneSignalNotificationDto notificationDto)
			throws ApiException {
		log.info("deleteOneSignalUser() OneSignalNotificationDto: Heading {}, Custom Data {}",
				notificationDto.getHeadings().getEn(), notificationDto.getCustomData());
		// Setting up the notification
		Notification notification = createNotification(notificationDto);
		log.info("deleteOneSignalUser() Notification: Heading {}, Custom Data {}", notification.getHeadings().getEn(),
				notification.getData());

		if (!ObjectUtils.isEmpty(notification)) {
			// Sending the request
			CreateNotificationSuccessResponse response = getOneSignalApiInstance().createNotification(notification);
			// Checking the result
			if (!ObjectUtils.isEmpty(response.getErrors())) {
				log.info("createOneSignalNotification(): Errors {}", response.getErrors().toJson());
			} else {
				notification.setId(response.getId());
				notificationDto.setNotifications(List.of(notification));
				log.info("createOneSignalNotification(): Success {}", response.toJson());
			}
		} else {
			log.info("createOneSignalNotification(): EMPTY {}", notification);
		}
		return notificationDto;
	}

	@SuppressWarnings("unused")
	private void deleteOneSignalUserSubscription(OneSignalUserDto userDto, SubscriptionObject subscriptionObject) {
		log.info("deleteOneSignalUserSubscription() Found alreadySubscribedObject {}", subscriptionObject.toJson());
		try {
			getOneSignalApiInstance().deleteSubscription(oneSignalAuthDto.getAppId(), subscriptionObject.getId());
			System.out.println("Deleted Old Token");
			log.info("deleteOneSignalUserSubscription() Deleted Found alreadySubscribedObject {}",
					subscriptionObject.toJson());
		} catch (ApiException e) {
			log.info("deleteOneSignalUserSubscription() Error {}", e);
			e.printStackTrace();
		}
	}

	private SubscriptionObject createOneSignalUserSubscription(OneSignalUserDto userDto,
			SubscriptionObject subscriptionObject) {
		try {
			if (!ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
				log.info(
						"createOneSignalUserSubscription() Trying to Create New Subscription User Slug {} User One Signal Id {} Subscrion Token {}",
						userDto.getSlug(), userDto.getOneSignalUser().getIdentity(), subscriptionObject.getToken());
				CreateSubscriptionRequestBody createSubscriptionRequestBody = new CreateSubscriptionRequestBody();
				subscriptionObject.setEnabled(true);
				createSubscriptionRequestBody.setSubscription(subscriptionObject);
				log.info("createOneSignalUserSubscription() Creating New Subscription {}", subscriptionObject.toJson());
				InlineResponse201 result = getOneSignalApiInstance().createSubscription(oneSignalAuthDto.getAppId(),
						OneSignalUserDto.EXTERNAL_ID, userDto.getSlug(), createSubscriptionRequestBody);
				log.info("createOneSignalUserSubscription() Created New Subscription {}", result.toJson());
				return result.getSubscription();
			} else {
				log.info(
						"createOneSignalUserSubscription() Trying to Create New Subscription User Slug {} No User Exists",
						userDto.getSlug());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private SubscriptionObject updateOneSignalUserSubscription(OneSignalUserDto userDto,
			SubscriptionObject subscriptionObject) {
		try {
			if (!ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
				log.info(
						"updateOneSignalUserSubscription() Trying to Update Subscription User Slug {} User One Signal Id {} Subscrion Token {}",
						userDto.getSlug(), userDto.getOneSignalUser().getIdentity(), subscriptionObject.getToken());
				UpdateSubscriptionRequestBody updateSubscriptionRequestBody = new UpdateSubscriptionRequestBody();
				updateSubscriptionRequestBody.setSubscription(subscriptionObject);
				getOneSignalApiInstance().updateSubscription(oneSignalAuthDto.getAppId(), subscriptionObject.getId(),
						updateSubscriptionRequestBody);
				log.info("updateOneSignalUserSubscription() Update Found alreadySubscribedObject {}",
						subscriptionObject.toJson());
				return subscriptionObject;
			} else {
				log.info(
						"updateOneSignalUserSubscription() Trying to Update Subscription User Slug {} No User Exists or Subscription Found",
						userDto.getSlug());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void printAllSegments(CreateNotificationSuccessResponse response) throws ApiException {
		NotificationWithMeta notificationWithMeta = getOneSignalApiInstance()
				.getNotification(oneSignalAuthDto.getAppId(), response.getId());
		log.info("Notification Error Status {}", notificationWithMeta.getErrored());
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
			notification.androidVisibility(1);
			notification.setHuaweiVisibility(1);
			notification.setIncludeExternalUserIds(notificationDto.getUserSlugs());
			notification.targetChannel(TargetChannelEnum.PUSH);
//			notification.setIncludedSegments(Arrays.asList(new String[] { "Active Subscriptions" }));
			notification.setHeadings(notificationDto.getHeadings());
			notification.setContents(notificationDto.getContents());
			notification.setWebUrl(notificationDto.getWeb_url());
			notification.setAppUrl(notificationDto.getApp_url());
			notification.setData(notificationDto.getCustomData());
			notification.setPriority(notificationDto.getPriority());
			return notification;
		} else if (notificationDto.getNotificationTypes().equals(NotificationTypeEnum.IN_APP)
				|| notificationDto.getNotificationTypes().equals(NotificationTypeEnum.EMAIL)
				|| notificationDto.getNotificationTypes().equals(NotificationTypeEnum.SMS)) {
			log.error("Non Configured Notification");
		}
		return null;
	}

	private void addDelay() {
		try {
			Thread.sleep(5000);
		} catch (Exception ex) {
		}
	}

}

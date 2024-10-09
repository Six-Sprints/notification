package com.sixsprints.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.jknack.handlebars.internal.lang3.ObjectUtils;
import com.onesignal.client.ApiException;
import com.onesignal.client.model.Notification;
import com.onesignal.client.model.StringMap;
import com.onesignal.client.model.SubscriptionObject;
import com.onesignal.client.model.SubscriptionObject.TypeEnum;
import com.sixsprints.notification.dto.OneSignalAuthDto;
import com.sixsprints.notification.dto.OneSignalNotificationDto;
import com.sixsprints.notification.dto.OneSignalUserDto;
import com.sixsprints.notification.enums.onesignal.NotificationTypeEnum;
import com.sixsprints.notification.service.OneSignalConfigureAndPushService;
import com.sixsprints.notification.service.impl.OneSignalConfigureAndPushServiceImpl;

@TestMethodOrder(OrderAnnotation.class)
public class OneSignalUserConfigurationNotificationTest {

	private OneSignalAuthDto oneSignalAuthDto = OneSignalAuthDto.builder().appId(null).appKeyToken(null)
			.userKeyToken(null).build();
	private OneSignalUserDto userDtoOne = OneSignalUserDto.builder().slug("TEST-USR00000001").email("user1@jptokyo.com")
			.subscriptions(null).build();

	private OneSignalConfigureAndPushService configureAndPushService = new OneSignalConfigureAndPushServiceImpl(
			oneSignalAuthDto);

	@Test
	@Order(1)
	public void deleteTestUsers() throws ApiException {
		userDtoOne = configureAndPushService.getOneSignalUser(userDtoOne);
		configureAndPushService.deleteOneSignalUser(userDtoOne);
		assertEquals(1, 1);
	}

	@Test
	@Order(2)
	public void createUserWithSubscription() throws ApiException {
		List<SubscriptionObject> subscriptions = new ArrayList<>();
		SubscriptionObject subscriptionObject = new SubscriptionObject();
		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.ANDROIDPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken(TypeEnum.ANDROIDPUSH.name() + ".Token1");
		subscriptions.add(subscriptionObject);

		userDtoOne.setSubscriptions(subscriptions);
		userDtoOne = configureAndPushService.createOneSignalUser(userDtoOne);
		assertNotEquals(userDtoOne.getOneSignalUser(), null);
	}

	@Test
	@Order(3)
	public void updateUserWithNewSubscription() throws ApiException {
		List<SubscriptionObject> oldsubscriptions = new ArrayList<>();
		SubscriptionObject subscriptionObject = new SubscriptionObject();
		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.ANDROIDPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken(TypeEnum.ANDROIDPUSH.name() + ".Token1");
		oldsubscriptions.add(subscriptionObject);
		List<SubscriptionObject> subscriptions = new ArrayList<>();
		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.CHROMEPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken("CHROMEWEBTOKEN");
		subscriptions.add(subscriptionObject);
		oldsubscriptions.add(subscriptionObject);

		userDtoOne.setSubscriptions(subscriptions);
		userDtoOne = configureAndPushService.updateOneSignalUser(userDtoOne, false);

		if (!ObjectUtils.isEmpty(userDtoOne.getOneSignalUser().getSubscriptions())) {
			for (SubscriptionObject requestSubscriptionObject : oldsubscriptions) {
				SubscriptionObject oneSignalSubscriptionObject = userDtoOne.getOneSignalUser().getSubscriptions()
						.stream().filter(e -> e.getType().equals(requestSubscriptionObject.getType())
								&& e.getToken().equals(requestSubscriptionObject.getToken()))
						.findFirst().orElse(null);
				if (ObjectUtils.isEmpty(oneSignalSubscriptionObject)) {
					assertEquals(requestSubscriptionObject.getToken(), null);
				} else if (oneSignalSubscriptionObject.getEnabled()) {
					assertEquals(requestSubscriptionObject.getToken(), oneSignalSubscriptionObject.getToken());
				}
			}
		} else {
			assertEquals(oldsubscriptions.size(), 0);
		}
	}

	@Test
	@Order(4)
	public void updateUserWithOldSubscriptionUpdate() throws ApiException {
		List<SubscriptionObject> oldsubscriptions = new ArrayList<>();
		SubscriptionObject subscriptionObject = new SubscriptionObject();
		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.ANDROIDPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken(TypeEnum.ANDROIDPUSH.name() + ".Token1");
		oldsubscriptions.add(subscriptionObject);

		List<SubscriptionObject> subscriptions = new ArrayList<>();
		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.CHROMEPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken("CHROMEWEBTOKEN-Updated");
		subscriptions.add(subscriptionObject);
		oldsubscriptions.add(subscriptionObject);

		userDtoOne.setSubscriptions(subscriptions);
		userDtoOne = configureAndPushService.updateOneSignalUser(userDtoOne, false);

		if (!ObjectUtils.isEmpty(userDtoOne.getOneSignalUser().getSubscriptions())) {
			for (SubscriptionObject requestSubscriptionObject : oldsubscriptions) {
				SubscriptionObject oneSignalSubscriptionObject = userDtoOne.getOneSignalUser().getSubscriptions()
						.stream().filter(e -> e.getType().equals(requestSubscriptionObject.getType())
								&& e.getToken().equals(requestSubscriptionObject.getToken()))
						.findFirst().orElse(null);
				if (ObjectUtils.isEmpty(oneSignalSubscriptionObject)) {
					assertEquals(requestSubscriptionObject.getToken(), null);
				} else if (oneSignalSubscriptionObject.getEnabled()) {
					assertEquals(requestSubscriptionObject.getToken(), oneSignalSubscriptionObject.getToken());
				}
			}
		} else {
			assertEquals(oldsubscriptions.size(), 0);
		}
	}

	@Test
	@Order(5)
	public void sendPUSHNotification() throws ApiException {
		System.err.println("Notification Create sendPUSHNotification()");
		StringMap stringMapHeading = new StringMap();
		stringMapHeading.en("This is sample heading.");
		StringMap stringMapContent = new StringMap();
		stringMapContent.en("This is sample content.");
		OneSignalNotificationDto notificationDto = OneSignalNotificationDto.builder().headings(stringMapHeading)
				.contents(stringMapContent)
//				.app_url(
//						"https://dev.oditly.jptc.link/settings/organisation/manage-units-details?mode=view&slug=UNT00000132")
//				.web_url("https://youtube.com")
				.notificationTypes(NotificationTypeEnum.PUSH).customData(Map.of("slug", userDtoOne.getSlug()))
				.userSlugs(List.of(userDtoOne.getSlug())).build();
		notificationDto = configureAndPushService.createOneSignalNotification(notificationDto);
		if (ObjectUtils.isEmpty(notificationDto.getNotifications())) {
			assertEquals(1, 0);
		} else {
			for (Notification notification : notificationDto.getNotifications()) {
				assertNotEquals(notification.getId(), null);
			}
		}
	}

	@Test
	@Order(6)
	public void deleteUserWithSubscriptionCleanUp() throws ApiException {
		userDtoOne = configureAndPushService.getOneSignalUser(userDtoOne);
		if (!ObjectUtils.isEmpty(userDtoOne.getOneSignalUser())) {
			configureAndPushService.deleteOneSignalUser(userDtoOne);
		}
		assertEquals(1, 1);
	}

}

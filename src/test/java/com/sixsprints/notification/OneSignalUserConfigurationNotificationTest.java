package com.sixsprints.notification;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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

	private OneSignalAuthDto oneSignalAuthDto = OneSignalAuthDto.builder().appId("").appKeyToken("").userKeyToken("")
			.build();
	private OneSignalUserDto userDtoOne = OneSignalUserDto.builder().slug("USR0000001").email("user1@jptokyo.com")
			.subscriptions(null).build();
	@SuppressWarnings("unused")
	private OneSignalUserDto userDtoTwo = OneSignalUserDto.builder().slug("USR0000002").email("user2@jptokyo.com")
			.subscriptions(null).build();

	private OneSignalConfigureAndPushService configureAndPushService = new OneSignalConfigureAndPushServiceImpl(
			oneSignalAuthDto);

	@Test
	@Order(1)
	public void deleteUserWithSubscription() {
		try {
			OneSignalUserDto getUser = configureAndPushService.getOneSignalUser(userDtoOne);
			if (ObjectUtils.isEmpty(getUser.getOneSignalUser())) {
				configureAndPushService.deleteOneSignalUser(userDtoOne);
				System.out.println("User Deleted");
				assertNotEquals(userDtoOne.getOneSignalUser(), null);
			} else {
				System.out.println("User NotFound");
			}
		} catch (Exception ex) {
			System.out.println("User Already Deleted" + ex);
		}
	}

	@Test
	@Order(2)
	public void createUserWithSubscription() throws ApiException, InterruptedException {
		List<SubscriptionObject> subscriptions = new ArrayList<>();
		SubscriptionObject subscriptionObject = new SubscriptionObject();
//		subscriptionObject.setType(TypeEnum.FIREFOXPUSH);
//		subscriptionObject.setNotificationTypes(null);
//		subscriptionObject.setToken("FIREFOXWEBTOKEN");
//		subscriptions.add(subscriptionObject);
//		subscriptionObject = new SubscriptionObject();
//		subscriptionObject.setType(TypeEnum.SAFARILEGACYPUSH);
//		subscriptionObject.setNotificationTypes(null);
//		subscriptionObject.setToken("SAFARILEGACYWEBTOKEN");
//		subscriptions.add(subscriptionObject);

		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.ANDROIDPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken("");
		subscriptions.add(subscriptionObject);
		OneSignalUserDto userDto = OneSignalUserDto.builder().slug("USR0000001").email("user@jptokyo.com")
				.subscriptions(subscriptions).build();
		userDto = configureAndPushService.createOneSignalUser(userDto);
		if (ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
			System.err.println("User Failed To Add With Subscriptions");
		} else {
			System.out.println("User Added With Subscriptions" + userDto.getOneSignalUser().toJson());
		}
		assertNotEquals(userDto.getOneSignalUser(), null);
		Thread.sleep(5000);
	}

	@Test
	@Order(3)
	public void updateUserWithSubscription() throws ApiException, InterruptedException {
		List<SubscriptionObject> subscriptions = new ArrayList<>();
		SubscriptionObject subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.CHROMEPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken("CHROMEWEBTOKEN");
		subscriptions.add(subscriptionObject);
		subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.SAFARIPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken("SAFARIWEBTOKEN");
		subscriptions.add(subscriptionObject);
		OneSignalUserDto userDto = OneSignalUserDto.builder().slug("USR0000001").email("user@jptokyo.com")
				.subscriptions(subscriptions).build();
		userDto = configureAndPushService.updateOneSignalUser(userDto);
		if (ObjectUtils.isEmpty(userDto.getOneSignalUser())) {
			System.err.println("User Failed To Update With New Subscriptions");
		} else {
			System.out.println("User Updated With New Subscriptions" + userDto.getOneSignalUser().toJson());
		}
		assertNotEquals(userDto.getOneSignalUser(), null);
		Thread.sleep(5000);
	}

	@Test
	@Order(4)
	public void updateUserWithOldSubscriptionUpdate() throws ApiException {
		List<SubscriptionObject> subscriptions = new ArrayList<>();
		SubscriptionObject subscriptionObject = new SubscriptionObject();
		subscriptionObject.setType(TypeEnum.SAFARIPUSH);
		subscriptionObject.setNotificationTypes(null);
		subscriptionObject.setToken("SAFARIWEBTOKEN-UPDATED");
		subscriptions.add(subscriptionObject);
		OneSignalUserDto userDto = OneSignalUserDto.builder().slug("USR0000001").email("user@jptokyo.com")
				.subscriptions(subscriptions).build();
		userDto = configureAndPushService.updateOneSignalUser(userDto);

		OneSignalUserDto updatedUserInfo = configureAndPushService.getOneSignalUser(userDto);
		SubscriptionObject updatedSubscription = updatedUserInfo.getSubscriptions().stream()
				.filter(e -> e.getToken().equals("CHROMEWEBTOKEN") && e.getType().equals(TypeEnum.CHROMEPUSH))
				.findFirst().orElse(null);
		if (ObjectUtils.isEmpty(updatedSubscription)) {
			System.out.println("Old Subscription updateUserWithOldSubscriptionUpdate Deleted");
		} else {
			System.err.println("Old Subscription updateUserWithOldSubscriptionUpdate Not Deleted");
		}
		assertNotEquals(userDto.getOneSignalUser(), null);
	}

	@Test
	@Order(5)
	public void sendPUSHNotification() throws ApiException, InterruptedException {
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
				.notificationTypes(NotificationTypeEnum.PUSH).customData(Map.of("slug", "USR0000001"))
				.userSlugs(List.of("USR00000164")).build();
		notificationDto = configureAndPushService.createOneSignalNotification(notificationDto);
		if (ObjectUtils.isEmpty(notificationDto.getNotifications())) {
			System.out.println("Notification Not Created");
		} else {
			System.err.println("Notification Created ");
			for (Notification notification : notificationDto.getNotifications()) {
				System.err.println("Notification Created " + notification.getAppId() + notification.getExternalId()
						+ notification.getIsAndroid());
			}
		}
		assertNotEquals(notificationDto.getNotifications(), null);
		Thread.sleep(5000);
	}

	@Test
	@Order(6)
	public void deleteUserWithSubscriptionCleanUp() {
		try {
			OneSignalUserDto getUser = configureAndPushService.getOneSignalUser(userDtoOne);
			if (!ObjectUtils.isEmpty(getUser.getOneSignalUser())) {
				configureAndPushService.deleteOneSignalUser(userDtoOne);
				System.out.println("User Deleted Clean Up Process");
				assertNotEquals(userDtoOne.getOneSignalUser(), null);
			} else {
				System.out.println("User NotFound Clean Up Process");
			}
			Thread.sleep(5000);
		} catch (Exception ex) {
			System.out.println("User Already Deleted Clean Up Process" + ex);
		}
	}

}

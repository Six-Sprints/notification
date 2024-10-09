package com.sixsprints.notification.service;

import com.onesignal.client.ApiException;
import com.sixsprints.notification.dto.OneSignalNotificationDto;
import com.sixsprints.notification.dto.OneSignalUserDto;

public interface OneSignalConfigureAndPushService {

	OneSignalUserDto getOneSignalUser(OneSignalUserDto userDto) throws ApiException;

	OneSignalUserDto createOneSignalUser(OneSignalUserDto userDto) throws ApiException;

	OneSignalUserDto updateOneSignalUser(OneSignalUserDto userDto, boolean deleteOtherSameTypeSubscriptions)
			throws ApiException;

	void deleteOneSignalUser(OneSignalUserDto userDto) throws ApiException;

	OneSignalNotificationDto createOneSignalNotification(OneSignalNotificationDto notificationDto) throws ApiException;

}

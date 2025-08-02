package com.splitwise.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.NotificationPreferences;
import com.splitwise.model.User;

@Repository
public interface NotificationPreferenceRepository extends CrudRepository<NotificationPreferences, Integer> {

	NotificationPreferences  findByUserAndNotificationEventType(User u, NotificationEventType eventType);

}

package com.splitwise.model;

import com.splitwise.enums.NotificationEventType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notification_preferences")
@Data
public class NotificationPreferences {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private User user;
	
	@Enumerated(EnumType.STRING)
	private NotificationEventType notificationEventType;
	
	private boolean isEmailEnabled;
	
	private boolean isSmsEnabled;
}

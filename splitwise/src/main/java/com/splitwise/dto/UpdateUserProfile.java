package com.splitwise.dto;

import java.util.HashMap;
import java.util.Map;

import com.splitwise.enums.NotificationEventType;

import lombok.Data;

@Data
public class UpdateUserProfile {
	private String email;
	private String pic;
	private String name;
	private String password;
	private String newPassword;

	private Map<NotificationEventType, Boolean> preferences = new HashMap<>();
}

package com.splitwise.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GroupResponse {

	int groupId;
	String groupName;
	LocalDateTime createdAt;
	
	public GroupResponse(int groupId, String groupName, LocalDateTime createdAt) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.createdAt = createdAt;
	}
}

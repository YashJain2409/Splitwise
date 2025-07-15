package com.splitwise.dto;

import com.splitwise.model.User;

import lombok.Data;

@Data
public class GroupMemberResponse {

	private int userId;
	private String name;
	private String email;
	private String pic;
	
	public GroupMemberResponse(User user) {
		this.userId = user.getUserId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.pic = user.getProfilePic();
	}
}

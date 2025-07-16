package com.splitwise.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddGroupMemberRequest {
	List<Integer> userIds;
}

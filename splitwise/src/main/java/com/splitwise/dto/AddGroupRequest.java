package com.splitwise.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddGroupRequest {
    String name;
    int createdBy;
    List<Integer> groupMemberIds;
}

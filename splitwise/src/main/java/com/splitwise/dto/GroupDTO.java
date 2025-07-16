package com.splitwise.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {
    String name;
    int createdBy;
    List<Integer> groupMemberIds;
}

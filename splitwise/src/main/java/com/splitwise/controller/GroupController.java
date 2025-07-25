package com.splitwise.controller;

import com.splitwise.dto.AddGroupMemberRequest;
import com.splitwise.dto.AddGroupRequest;
import com.splitwise.dto.GroupMemberResponse;
import com.splitwise.dto.GroupResponse;
import com.splitwise.exception.ApplicationException;
import com.splitwise.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/Group")
@RestController
public class GroupController {

    @Autowired
    GroupService groupService;

    @PostMapping("/Create")
    public ResponseEntity<String> CreateGroup(@RequestBody AddGroupRequest addGroupRequest) {
        groupService.CreateGroup(addGroupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Group created successfully.");
    }

    @DeleteMapping("/Delete/{groupId}")
    public ResponseEntity<String> DeleteGroup(@PathVariable int groupId) {
        groupService.deleteGroupById(groupId);
        return ResponseEntity.status(HttpStatus.OK).body("Group deleted successfully");
    }
    
    

    @PostMapping("/{groupId}")
    public ResponseEntity<String> UpdateGroup(@RequestBody Map<String,String> req,@PathVariable("groupId") int groupId) {
        if(!req.containsKey("name") || req.get("name").isEmpty())
            throw new ApplicationException("0000","Group name cannot be empty", HttpStatus.BAD_REQUEST);
        String name = req.get("name");
        groupService.UpdateGroup(name,groupId);
        return ResponseEntity.status(HttpStatus.OK).body("Updated group successfully");

    }
    
    @PostMapping("/{groupId}/members")
    public ResponseEntity<String> AddMembers(@PathVariable int groupId, @RequestBody AddGroupMemberRequest addGroupMemberRequest) {
    	 if(addGroupMemberRequest.getUserIds() == null || addGroupMemberRequest.getUserIds().isEmpty())
             throw new ApplicationException("0000","please add atleast one user", HttpStatus.BAD_REQUEST);
        groupService.addMembersToGroup(groupId,addGroupMemberRequest);
        return ResponseEntity.ok("Members added successfully");
    }
    
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<String> removeMembers(@PathVariable int groupId,@PathVariable int userId) {
    	groupService.removeMembers(groupId,userId);
    	return ResponseEntity.ok("Member removed succesfully");
    }
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> listAllMembers(@PathVariable int groupId){
    	List<GroupMemberResponse> members = groupService.listAllMembers(groupId);
    	return ResponseEntity.ok(members);
    }
    
    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<GroupResponse>> listAllGroups(@PathVariable int userId) {
    	List<GroupResponse> groups = groupService.getGroupForUser(userId);
    	return ResponseEntity.ok(groups);
    }

}

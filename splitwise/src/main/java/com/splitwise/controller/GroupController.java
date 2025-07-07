package com.splitwise.controller;

import com.splitwise.dto.Members;
import com.splitwise.dto.GroupDTO;
import com.splitwise.dto.Members;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Group;
import com.splitwise.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<String> CreateGroup(@RequestBody GroupDTO createGroupDTO) {
        if(createGroupDTO.getName().isEmpty())
            throw new ApplicationException("0000","Group name cannot be empty", HttpStatus.BAD_REQUEST);
        List<Integer> groupMembers = createGroupDTO.getGroupMemberIds();
        
        if(groupMembers != null && groupMembers.size() < 2) {
            throw new ApplicationException("0001","Users must be greater than 2",HttpStatus.BAD_REQUEST);
        }
        groupService.CreateGroup(createGroupDTO);
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
    
    @PostMapping("AddMembers/{groupId}")
    public ResponseEntity<String> AddMembers() {
        // TODO : Add members to group
    	return null;
    }

}

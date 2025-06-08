package com.splitwise.controller;

import com.splitwise.dto.Members;
import com.splitwise.dto.GroupDTO;
import com.splitwise.dto.Members;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Group;
import com.splitwise.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/Group")
@RestController
public class GroupController {

    @Autowired
    GroupService groupService;

    @PostMapping("/Create")
    public ResponseEntity<Group> CreateGroup(@RequestBody GroupDTO createGroupDTO) {
        if(createGroupDTO.getName().isEmpty())
            throw new ApplicationException("0000","Group name cannot be empty", HttpStatus.BAD_REQUEST);
        List<Members> attributes = createGroupDTO.getAttributes();
        if(attributes.size() < 2) {
            throw new ApplicationException("0001","Users must be greater than 2",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(groupService.CreateGroup(createGroupDTO),HttpStatus.CREATED);
    }

    @DeleteMapping("/Delete/{groupId}")
    public ResponseEntity<String> DeleteGroup(@PathVariable int groupId) {
        return groupService.deleteGroupById(groupId);
    }

    @PostMapping("/{groupId}")
    public ResponseEntity<Group> UpdateGroup(@RequestBody GroupDTO groupDTO,@PathVariable("groupId") int groupId) {
        if(groupDTO.getName().isEmpty())
            throw new ApplicationException("0000","Group name cannot be empty", HttpStatus.BAD_REQUEST);
        List<Members> attributes = groupDTO.getAttributes();
        if(attributes.size() < 2) {
            throw new ApplicationException("0001","Users must be greater than 2",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(groupService.UpdateGroup(groupDTO,groupId),HttpStatus.OK);

    }
}

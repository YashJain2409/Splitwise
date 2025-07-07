package com.splitwise.service;

import com.splitwise.dto.Members;
import com.splitwise.dto.GroupDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Group;
import com.splitwise.model.GroupMember;
import com.splitwise.model.User;
import com.splitwise.repository.GroupMemberRepository;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

	final GroupRepository groupRepository;


    final UserRepository userRepository;
    
    final GroupMemberRepository groupMemberRepository;
    
    public void CreateGroup(GroupDTO createGroupDTO) {
    	User creator = userRepository.findById(createGroupDTO.getCreatedBy()).orElseThrow();
        Group g = new Group();
        g.setGroupName(createGroupDTO.getName());
        g.setCreatedBy(creator);
        g.setCreatedAt(LocalDateTime.now());
        
        groupRepository.save(g);
        
        
        
        for(int userIds : createGroupDTO.getGroupMemberIds()) {
        	User u = userRepository.findById(userIds).orElseThrow();

            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(g);
            groupMember.setUser(u);
            groupMemberRepository.save(groupMember);
            
        }
        
    }

    public void deleteGroupById(int groupId) {
    	Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        group.setDeleted(true);
        groupRepository.save(group);
    }

    public void UpdateGroup(String name, int groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if(group != null) {
            group.setGroupName(name);
            groupRepository.save(group);
        }
        else
        	throw new ApplicationException("0000","Group does not exist", HttpStatus.NOT_FOUND);
    }
}

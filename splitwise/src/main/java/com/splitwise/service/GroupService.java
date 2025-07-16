package com.splitwise.service;

import com.splitwise.dto.Members;
import com.splitwise.dto.AddGroupMemberRequest;
import com.splitwise.dto.GroupDTO;
import com.splitwise.dto.GroupMemberResponse;
import com.splitwise.dto.GroupResponse;
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

	public void addMembersToGroup(int groupId,AddGroupMemberRequest addGroupMemberRequest) {
		Group g = groupRepository.findById(groupId).orElseThrow();
		List<User> users = (List<User>) userRepository.findAllById(addGroupMemberRequest.getUserIds());
		for(User u : users) {
			boolean alreadyExists = groupMemberRepository.existsByGroupAndUser(g,u);
			if(alreadyExists)
				continue;
			GroupMember groupMember = new GroupMember();
			groupMember.setGroup(g);
			groupMember.setUser(u);
			groupMemberRepository.save(groupMember);
		}
		
	}

	public List<GroupMemberResponse> listAllMembers(int groupId) {
		Group g = groupRepository.findById(groupId).orElseThrow();
		List<GroupMember> members = groupMemberRepository.findByGroup(g);
		return members.stream().map((m) -> new GroupMemberResponse(m.getUser())).toList();
	}

	public void removeMembers(int groupId, int userId) {
		Group g = groupRepository.findById(groupId).orElseThrow();
		User u = userRepository.findById(userId).orElseThrow();
		GroupMember gm = groupMemberRepository.findByGroupAndUser(g,u);
		groupMemberRepository.delete(gm);
	}

	public List<GroupResponse> getGroupForUser(int userId) {
		User u = userRepository.findById(userId).orElseThrow();
		List<GroupMember> memberships = groupMemberRepository.findByUser(u);
		return memberships.stream().map(gm -> gm.getGroup()).filter(g -> !g.isDeleted()).map(g -> new GroupResponse(g.getGroupId(),g.getGroupName(),g.getCreatedAt())).toList();
	}
}

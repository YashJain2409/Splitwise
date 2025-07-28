package com.splitwise.service;

import com.splitwise.dao.GroupDAO;
import com.splitwise.dao.GroupMemberDAO;
import com.splitwise.dao.UserDAO;
import com.splitwise.dto.AddGroupMemberRequest;
import com.splitwise.dto.AddGroupRequest;
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

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

	final GroupRepository groupRepository;

    final UserRepository userRepository;
    
    final GroupMemberDAO groupMemberDAO;
	final UserDAO userDAO;
	final GroupDAO groupDAO;
	final ModelMapper modelMapper;

	@Transactional
    public void CreateGroup(AddGroupRequest addGroupRequest) {

		if(addGroupRequest.getName().isEmpty())
			throw new ApplicationException("0000","Group name cannot be empty", HttpStatus.BAD_REQUEST);

		List<Integer> groupMembers = addGroupRequest.getGroupMemberIds();

		if(groupMembers != null && groupMembers.size() < 2) {
			throw new ApplicationException("0001","At least two users should be there",HttpStatus.BAD_REQUEST);
		}

    	User creator = modelMapper.map(userDAO.findById(addGroupRequest.getCreatedBy()),User.class);
        Group group = new Group();
        group.setGroupName(addGroupRequest.getName());
        group.setCreatedBy(creator);
        group.setCreatedAt(LocalDateTime.now());
		group =  groupDAO.saveGroup(group);

		if(addGroupRequest.getGroupMemberIds()!=null && !addGroupRequest.getGroupMemberIds().isEmpty()){
			List<GroupMember> groupMemberList = new ArrayList<>();
			for(int userIds : addGroupRequest.getGroupMemberIds()) {
				User u = modelMapper.map(userDAO.findById(userIds),User.class);
				GroupMember groupMember = new GroupMember();
				groupMember.setGroup(group);
				groupMember.setUser(u);
				groupMemberList.add(groupMember);
			}

			groupMemberDAO.saveGroupMembers(groupMemberList);

		}

        
    }


	@Transactional
    public void deleteGroupById(int groupId) {
    	Group group = groupDAO.findById(groupId);
        group.setDeleted(true);
		groupDAO.saveGroup(group);
    }

	@Transactional
    public void UpdateGroup(String name, int groupId) {
        Group group = groupDAO.findById(groupId);
        if(group != null) {
            group.setGroupName(name);
            groupDAO.saveGroup(group);
        }
    }

	@Transactional
	public void addMembersToGroup(int groupId,AddGroupMemberRequest addGroupMemberRequest) {
		Group g = groupDAO.findById(groupId);

		List<User> users = (List<User>) userRepository.findAllById(addGroupMemberRequest.getUserIds());

		if(!users.isEmpty()){

			List<GroupMember> groupMemberList = new ArrayList<>();
			for(User u : users) {
				boolean alreadyExists = groupMemberDAO.existsByGroupAndUser(g,u);
				if(alreadyExists)
					continue;
				GroupMember groupMember = new GroupMember();
				groupMember.setGroup(g);
				groupMember.setUser(u);
				groupMemberList.add(groupMember);
			}

			if(!groupMemberList.isEmpty()){
				groupMemberDAO.saveGroupMembers(groupMemberList);
			}
		}
	}

	public List<GroupMemberResponse> listAllMembers(int groupId) {
		Group g = groupDAO.findById(groupId);
		List<GroupMember> members = groupMemberDAO.findByGroup(g);
		return members.stream().map((m) -> new GroupMemberResponse(m.getUser())).toList();
	}

	@Transactional
	public void removeMembers(int groupId, int userId) {
		Group g = groupRepository.findById(groupId).orElseThrow();
		User u = userRepository.findById(userId).orElseThrow();
		GroupMember gm = groupMemberDAO.findByGroupAndUser(g,u);
		groupMemberDAO.deleteGroupMember(gm);
	}

	public List<GroupResponse> getGroupForUser(int userId) {
		User u = userRepository.findById(userId).orElseThrow();
		List<GroupMember> memberships = groupMemberDAO.findByUser(u);
		return memberships.stream().map(gm -> gm.getGroup()).filter(g -> !g.isDeleted()).map(g -> new GroupResponse(g.getGroupId(),g.getGroupName(),g.getCreatedAt())).toList();
	}
}

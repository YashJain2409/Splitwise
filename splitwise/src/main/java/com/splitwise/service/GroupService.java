package com.splitwise.service;

import com.splitwise.dto.Members;
import com.splitwise.dto.GroupDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Group;
import com.splitwise.model.User;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;
    public Group CreateGroup(GroupDTO createGroupDTO) {
        Group g = new Group();
        g.setGroupName(createGroupDTO.getName());
        List<String> emails = new ArrayList<>();
        for(Members a : createGroupDTO.getAttributes()) {
            emails.add(a.getEmail());
        }
        List<User> users = userRepository.findByEmailIn(emails);
//        g.setUsers(users);
        return groupRepository.save(g);
    }

    public ResponseEntity<String> deleteGroupById(int groupId) {
        boolean isFound = groupRepository.existsById(groupId);
        if(!isFound) {
            throw new ApplicationException("0000","Group does not exist", HttpStatus.NOT_FOUND);
        }
        groupRepository.deleteById(groupId);
        return new ResponseEntity<>("SUCCESS",HttpStatus.OK);
    }

    public Group UpdateGroup(GroupDTO groupDTO, int groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if(group != null) {
            group.setGroupName(groupDTO.getName());
            List<String> emails = new ArrayList<>();
            for(Members a : groupDTO.getAttributes()) {
                emails.add(a.getEmail());
            }
            List<User> users = userRepository.findByEmailIn(emails);
//            group.setUsers(users);
            return groupRepository.save(group);
        }
        throw new ApplicationException("0000","Group does not exist", HttpStatus.NOT_FOUND);
    }
}

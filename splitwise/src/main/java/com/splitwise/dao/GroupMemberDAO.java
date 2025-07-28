package com.splitwise.dao;

import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Group;
import com.splitwise.model.GroupMember;
import com.splitwise.model.User;
import com.splitwise.repository.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMemberDAO {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    public List<GroupMember> saveGroupMembers(List<GroupMember> groupMember){


        try{
         return (List<GroupMember>) groupMemberRepository.saveAll(groupMember);
        }
        catch (Exception e){
            throw new ApplicationException("Something went wrong while creating group", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean existsByGroupAndUser(Group group, User user){

        return groupMemberRepository.existsByGroupAndUser(group,user);
    }

    public List<GroupMember> findByGroup(Group g){

        return groupMemberRepository.findByGroup(g);
    }

    public GroupMember findByGroupAndUser(Group g, User u) {

        return groupMemberRepository.findByGroupAndUser(g,u);
    }

    public boolean deleteGroupMember(GroupMember groupMember){

        try {
            groupMemberRepository.delete(groupMember);
        }
        catch (Exception e){
            throw new ApplicationException("Something went wrong while removing group member",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return true;
    }

    public List<GroupMember> findByUser(User u) {

        return groupMemberRepository.findByUser(u);
    }
}

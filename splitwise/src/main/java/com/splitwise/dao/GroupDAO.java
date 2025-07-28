package com.splitwise.dao;

import com.splitwise.dto.AddGroupRequest;
import com.splitwise.dto.GroupDTO;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Group;
import com.splitwise.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GroupDAO {

    @Autowired
    private GroupRepository groupRepository;

    public Group findById(int groupId){

        return groupRepository.findById(groupId).orElseThrow(()-> new ApplicationException("Group is not exist for group ID ::"+groupId,HttpStatus.NOT_FOUND ));
    }

    public Group saveGroup(Group group){

        try{
            return groupRepository.save(group);
        }
        catch (Exception e){
            throw new ApplicationException("Something went wrong while creating group. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

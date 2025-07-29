package com.splitwise.dao;

import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Split;
import com.splitwise.repository.SplitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SplitDAO {

    @Autowired
    private SplitRepository splitRepository;

    public List<Split> saveSplits(List<Split> splits){

        try{
            return (List<Split>) splitRepository.saveAll(splits);
        }
        catch (Exception e){
            throw new ApplicationException("Something went wrong while create split", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

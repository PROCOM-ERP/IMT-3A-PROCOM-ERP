package com.example.orderservice.service;

import com.example.orderservice.model.ProgressStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProgressStatusService {

    private final List<ProgressStatus> allProgressStatus = Arrays.stream(ProgressStatus.values()).toList();

    public List<ProgressStatus> getAllProgressStatus()
    {
        return allProgressStatus;
    }

    public void isValidProgressStatus(Integer progressStatus)
            throws DataIntegrityViolationException
    {
        if (progressStatus < 1 || progressStatus > allProgressStatus.size())
            throw new DataIntegrityViolationException("Invalid order progress status provided");
    }

}
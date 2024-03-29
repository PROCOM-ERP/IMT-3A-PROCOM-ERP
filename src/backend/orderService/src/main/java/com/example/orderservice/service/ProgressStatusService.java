package com.example.orderservice.service;

import com.example.orderservice.model.ProgressStatus;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Getter
public class ProgressStatusService {

    private final List<ProgressStatus> allProgressStatus = Arrays.stream(ProgressStatus.values()).toList();

    public ProgressStatus getProgressStatusById(Integer id)
            throws DataIntegrityViolationException
    {
        isValidProgressStatus(id);
        return allProgressStatus.get(id-1);
    }

    public void isValidProgressStatus(Integer progressStatus)
            throws DataIntegrityViolationException
    {
        if (progressStatus < 1 || progressStatus > allProgressStatus.size())
            throw new DataIntegrityViolationException("Invalid order progress status provided");
    }

}
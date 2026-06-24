package com.nic.nerie.t_assignmenttest.service;

import com.nic.nerie.t_assignmenttest.model.T_Assignmenttest;
import com.nic.nerie.t_assignmenttest.repository.T_AssignmenttestRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class T_AssignmenttestService {
    private final T_AssignmenttestRepository tAssignmenttestRepository;

    @Autowired
    public T_AssignmenttestService(T_AssignmenttestRepository tAssignmenttestRepository) {
        this.tAssignmenttestRepository = tAssignmenttestRepository;
    }

    @Transactional(readOnly = true)
    public List<T_Assignmenttest> getAssignmentList(@NotNull @NotBlank String usercode) {
        try {
            return tAssignmenttestRepository.getAssignmentListByUsercode(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving T_Assignmenttest list by usercode " + usercode, ex);
        }
    }

    @Transactional(readOnly = true)
    public T_Assignmenttest getAssignmentDetails(@NotNull @NotBlank String fid) {
        try {
            Optional<T_Assignmenttest> assignment = tAssignmenttestRepository.getAssignmentDetailsById(fid);
            return assignment.orElse(null); // or throw an exception if not found
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving T_Assignmenttest by fid " + fid, ex);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String uploadAssignment(@NotNull T_Assignmenttest assignment) {
        try {
            // Generate new ID if not provided
            if (assignment.getAssignmenttestid() == null || assignment.getAssignmenttestid().isEmpty()) {
                Integer maxId = tAssignmenttestRepository.findMaxAssignmentTestId();
                int newId = (maxId != null) ? maxId + 1 : 1;
                assignment.setAssignmenttestid(String.valueOf(newId));
            }

            T_Assignmenttest savedAssignment = tAssignmenttestRepository.save(assignment);
            return savedAssignment.getAssignmenttestid();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }
}

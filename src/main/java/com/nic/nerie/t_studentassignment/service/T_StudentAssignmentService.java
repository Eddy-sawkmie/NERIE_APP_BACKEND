package com.nic.nerie.t_studentassignment.service;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_assignmenttest.model.T_Assignmenttest;
import com.nic.nerie.t_studentassignment.dto.StudentBySubDTO;
import com.nic.nerie.t_studentassignment.model.T_StudentAssignment;
import com.nic.nerie.t_studentassignment.repository.T_StudentAssignmentRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class T_StudentAssignmentService {
    private final T_StudentAssignmentRepository tStudentAssignmentRepository;

    @Autowired
    public T_StudentAssignmentService(T_StudentAssignmentRepository tStudentAssignmentRepository) {
        this.tStudentAssignmentRepository = tStudentAssignmentRepository;
    }

    @Transactional(readOnly = true)
    public Boolean existsByStudentassignmentid (@NotNull @NotBlank String studentassignmentid) {
        try {
            return tStudentAssignmentRepository.existsById(studentassignmentid.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking T_StudentAssignment entity by studentassignmentid " + studentassignmentid, ex);
        }
    }

    public List<Object[]> findAssignmentDetailsByUsercode(String usercode) {
        return tStudentAssignmentRepository.findAssignmentDetailsByUsercode(usercode);
    }

//    public List<Object[]> getSubmitAssignmentList(String usercode) {
//        return tStudentAssignmentRepository.getSubmitAssignmentList(usercode);
//    }
public List<Object[]> getSubmitAssignmentList(String usercode, String semphase) {
    return tStudentAssignmentRepository.getSubmitAssignmentList(usercode, semphase);
}

    public T_StudentAssignment getAssignmentSubmissionDetails(String studentassignmentid) {
        Optional<T_StudentAssignment> tStudentAssignment = tStudentAssignmentRepository
                .findByStudentassignmentid(studentassignmentid);

        if (tStudentAssignment.isPresent())
            return tStudentAssignment.get();

        return null;
    }

    public List<T_StudentAssignment> getSubmittedAssignments(@NotNull @NotBlank String assignmentTestId) {
        try {
            return tStudentAssignmentRepository.getSubmittedAssignmentsByAssignmentTestId(assignmentTestId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getSubmittedAssignments by assignmentTestId:" + assignmentTestId, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getSubmittedAssignmentsStudentsName(@NotNull @NotBlank String assignmentTestId) {
        try {
            return tStudentAssignmentRepository.getSubmittedAssignmentStudents(assignmentTestId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Submitted Assignments by assignmentTestId " + assignmentTestId, ex);
        } 
    }

    public T_StudentAssignment getStudentAssignmentDocument(String fid, String sid) {
        Optional<T_StudentAssignment> assignment = tStudentAssignmentRepository
                .findStudentAssignmentDocument(fid, sid);

        return assignment.orElse(null); // or throw exception if not found
    }

    @Transactional(readOnly = true)
    public T_StudentAssignment getStudentAssignmentByAssignmentidAndUsercode(@NotNull @NotBlank String assignmentid, @NotNull @NotBlank String usercode) {
        try {
            Optional<T_StudentAssignment> studentAssignment = tStudentAssignmentRepository
                    .findByAssignmentidAndUsercode(assignmentid.trim(), usercode.trim());
            return studentAssignment.orElse(null); // Return null if not found
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching T_StudentAssignment entity", ex);
        }
    }

    @Transactional
    public String saveStudentAssignmentMarks(@NotNull @NotBlank String studentassignmentid, @NotNull @NotBlank String mark) {
        try {
            // Find the existing assignment
            Optional<T_StudentAssignment> optionalAssignment = tStudentAssignmentRepository
                    .findByStudentassignmentid(studentassignmentid);

            if (!optionalAssignment.isPresent()) {
                return "-1"; // Assignment not found
            }

            // Parse and validate the mark
            float markValue = Float.parseFloat(mark);
            if (markValue < 0) {
                return "-1"; // Invalid mark value
            }

            // Update and save the entity
            T_StudentAssignment assignment = optionalAssignment.get();
            assignment.setAssignmentmark(markValue);
            tStudentAssignmentRepository.save(assignment);

            return "1"; // Success
        } catch (NumberFormatException e) {
            return "-1"; // Invalid number format
        }
    }

    @Transactional
    public void deleteAssignmentMarkById(String studentassignmentid) {
        try {
            if (studentassignmentid != null && !studentassignmentid.trim().isEmpty()) {
                Optional<T_StudentAssignment> optionalAssignment = tStudentAssignmentRepository
                        .findByStudentassignmentid(studentassignmentid);

                if (optionalAssignment.isPresent()) {
                    T_StudentAssignment assignment = optionalAssignment.get();
                    assignment.setAssignmentmark(null);
                    tStudentAssignmentRepository.save(assignment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = false)
    public T_StudentAssignment saveStudentAssignment(@NotNull T_StudentAssignment assignment) {
        try {
            if (assignment.getStudentassignmentid() == null || assignment.getStudentassignmentid().isEmpty()) 
                assignment.setStudentassignmentid(getNextAssignmentid());

            return tStudentAssignmentRepository.save(assignment);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_StudentAssignment entity", ex);
        }
    }

    @Transactional(readOnly = true)
    private String getNextAssignmentid() {
        try {
            Integer lastUsedAssignmentid = tStudentAssignmentRepository.getLastUsedAssignmentid();
            return lastUsedAssignmentid != null ? String.valueOf(lastUsedAssignmentid + 1) : "1";
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching last used assignment ID", ex);
        }
    }

    @Transactional
    public String uploadStudentAssignment(MultipartFile file,
                                          String assignmentTestId,
                                          String usercode) {
        try {
            T_StudentAssignment studentAssignment =
                    tStudentAssignmentRepository.findByUsercodeAndAssignmentTestId(usercode, assignmentTestId);

            if (studentAssignment != null) {
                // Update existing assignment
                studentAssignment.setReldoc(file.getBytes());
                studentAssignment.setUploaddate(new Date());
            } else {
                // Create new assignment
                studentAssignment = new T_StudentAssignment();

                Integer maxId = tStudentAssignmentRepository.findMaxStudentAssignmentId();
                int newId = (maxId == null) ? 1 : maxId + 1;
                studentAssignment.setStudentassignmentid(String.valueOf(newId));

                T_Assignmenttest assignmentTest = new T_Assignmenttest();
                assignmentTest.setAssignmenttestid(assignmentTestId);
                studentAssignment.setAssignmenttestid(assignmentTest);

                MT_Userlogin studentUser = new MT_Userlogin();
                studentUser.setUsercode(usercode);
                studentAssignment.setUsercode(studentUser);

                studentAssignment.setReldoc(file.getBytes());
                studentAssignment.setUploaddate(new Date());
            }

            T_StudentAssignment savedEntity = tStudentAssignmentRepository.save(studentAssignment);
            return savedEntity.getStudentassignmentid();

        } catch (Exception ex) {
            throw new RuntimeException("Error Saving/Uploading Assignment in T_StudentAssignment: ", ex);
        }
    }

    public List<StudentBySubDTO>getStudentsBySubject(String subjectcode){
            List<Object[]> list = tStudentAssignmentRepository.getStudentsBySubject(subjectcode);
            return list.stream().map(obj -> new StudentBySubDTO(
                obj[0] != null ? obj[0].toString() : "",
                obj[1] != null ? obj[1].toString() : "",
                obj[2] != null ? obj[2].toString() : ""
            )).toList();
    }

}

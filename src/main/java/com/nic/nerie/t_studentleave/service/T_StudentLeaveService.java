package com.nic.nerie.t_studentleave.service;

import com.nic.nerie.t_studentleave.model.T_StudentLeave;
import com.nic.nerie.t_studentleave.repository.T_StudentLeaveRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class T_StudentLeaveService {
    private final T_StudentLeaveRepository tStudentLeaveRepository;

    @Autowired
    public T_StudentLeaveService(T_StudentLeaveRepository tStudentLeaveRepository) {
        this.tStudentLeaveRepository = tStudentLeaveRepository;
    }

    public T_StudentLeave findByStudentleaveid(String studentleaveid) {
        Optional<T_StudentLeave> studentLeave = tStudentLeaveRepository.findByStudentleaveid(studentleaveid);

        if (studentLeave.isPresent())
            return studentLeave.get();

        return null;
    }

    public List<T_StudentLeave> getOwnLeaveApplicationsCurrentSem(String studentid, String isshortterm, String semPhaseId) {
        try {
            return tStudentLeaveRepository.getOwnLeaveApplicationsCurrentSem(studentid, isshortterm, semPhaseId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching current semester student leave applications", ex);
        }
    }

    public List<T_StudentLeave> getOwnLeaveApplications(String studentid) {
        try {
            return tStudentLeaveRepository.getByStudentid(studentid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching student leave applications", ex);
        }
    }

    @Transactional(readOnly = false)
    public T_StudentLeave saveLeaveApplication(T_StudentLeave tStudentLeave) {
        try {
            return tStudentLeaveRepository.save(tStudentLeave);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_StudentLeave entity", ex);
        }
    }

    public List<T_StudentLeave> getAllStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getAllStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting All Student Leave Applications.", ex);
        }
    }

    /*
     * Without Office Code
    public List<T_StudentLeave> getPStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getPStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Principal Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getPMStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getPMStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Principal Male Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getPFStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getPFStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Principal Female Student Leave Applications.", ex);
        }
    }
    */

    public List<T_StudentLeave> getPStudentLeaveApplications(String officecode) {
        try {
            return tStudentLeaveRepository.getPStudentLeaveApplications(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Principal Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getPMStudentLeaveApplications(String officecode) {
        try {
            return tStudentLeaveRepository.getPMStudentLeaveApplications(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Principal Male Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getPFStudentLeaveApplications(String officecode) {
        try {
            return tStudentLeaveRepository.getPFStudentLeaveApplications(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Principal Female Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getMStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getMStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Male Warden Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getFStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getFStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Female Warden Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getDStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getDStudentLeaveApplications();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Dean Student Leave Applications.", ex);
        }
    }

    public List<T_StudentLeave> getCWStudentLeaveApplications() {
        try {
            return tStudentLeaveRepository.getCWStudentLeaveApplications();

        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Getting Chief Warden Student Leave Applications.", ex);
        }
    }

    @Transactional(readOnly = false)
    public T_StudentLeave saveStudentLeave(@NotNull T_StudentLeave studentLeave) {
        try {
            if (studentLeave.getStudentleaveid() == null || studentLeave.getStudentleaveid().isBlank())
                studentLeave.setStudentleaveid(generateNextStudentleaveid());
            else
                System.out.println("[Test] it didn't work");

            return tStudentLeaveRepository.save(studentLeave);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_StudentLeave entity", ex);
        }
    }

    // @Transactional
    // public String uploadStudentLeaveApplication(@NotNull T_StudentLeave sl) {
    //     try {
    //         if (sl.getStudentleaveid() == null || sl.getStudentleaveid().isEmpty()) {
    //             Integer maxId = tStudentLeaveRepository.findMaxStudentLeaveId();
    //             if (maxId == null) {
    //                 maxId = 0; // Start from 0 so first ID will be 1
    //             }
    //             sl.setStudentleaveid(String.valueOf(maxId + 1));
    //         }

    //         T_StudentLeave savedLeave = tStudentLeaveRepository.save(sl);
    //         return savedLeave.getStudentleaveid();
    //     } catch (Exception e) {
    //         System.out.println("E::uploadStudentLeaveApplication::" + e);
    //         return "-1";
    //     }
    // }

    @Transactional
    public String uploadStudentLeaveApplication(@NotNull T_StudentLeave sl) {
        try {
            // No custom ID logic needed! 
            // If ID is null -> Database Sequence assigns it (INSERT).
            // If ID exists -> Hibernate simply updates the record (UPDATE).
            T_StudentLeave savedLeave = tStudentLeaveRepository.save(sl);
            
            return savedLeave.getStudentleaveid();
        } catch (Exception e) {
            System.out.println("E::uploadStudentLeaveApplication::" + e);
            return "-1";
        }
    }

    public List<Object[]> getFilteredStudentLeaveList(@NotBlank String status,
                                                      @NotBlank String fystart,
                                                      @NotBlank String fyend,
                                                      String sphaseid, String semester,
                                                      @NotBlank String course,
                                                      @NotBlank String approvedstatus) {
        List<Object[]> sla = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;

        try {
            // Handle date parsing
            if (fystart.equals("all") && fyend.equals("all")) {
                startDate = dateFormat.parse("2000-01-01");
                endDate = dateFormat.parse("2100-12-31");
            } else {
                fystart = fystart + "-01";
                fyend = fyend + "-01";
                startDate = dateFormat.parse(fystart);
                endDate = dateFormat.parse(fyend);
            }

            // Filter logic based on status and approval status
            if ("shortterm".equalsIgnoreCase(status)) {
                if ("APPROVED".equalsIgnoreCase(approvedstatus)) {
                    if ("All".equalsIgnoreCase(sphaseid)) {
                        sla = tStudentLeaveRepository.findApprovedStudentLeavesByCourseAndDateRange(course, startDate, endDate);
                    } else {
                        sla = tStudentLeaveRepository.findStudentLeaveDetailsByCourseDateAndPhase(course, startDate, endDate, sphaseid);
                    }
                } else if ("NOTAPPROVED".equalsIgnoreCase(approvedstatus)) {
                    sla = tStudentLeaveRepository.findNotApprovedStudentLeavesByCourseAndDateRange(course, startDate, endDate);
                } else {
                    if ("All".equalsIgnoreCase(sphaseid)) {
                        sla = tStudentLeaveRepository.findStudentLeaveDetailsByCourseAndDateRange(course, startDate, endDate);
                    } else {
                        sla = tStudentLeaveRepository.findStudentLeaveDetailsByCourseDateAndPhase(course, startDate, endDate, sphaseid);
                    }
                }
            } else if ("longterm".equalsIgnoreCase(status)) {
                if ("APPROVED".equalsIgnoreCase(approvedstatus)) {
                    sla = tStudentLeaveRepository.findApprovedStudentLeavesByCourseDateAndSemester(course, startDate, endDate);
                } else if ("NOTAPPROVED".equalsIgnoreCase(approvedstatus)) {
                    sla = tStudentLeaveRepository.findUnapprovedStudentLeavesByCourseAndDate(course, startDate, endDate);
                } else {
                    if ("All".equalsIgnoreCase(semester)) {
                        sla = tStudentLeaveRepository.findStudentLeavesWithSemesterByCourseAndDateRange(course, startDate, endDate);
                    } else {
                        sla = tStudentLeaveRepository.findStudentLeavesByCourseDateAndSemester(course, startDate, endDate, semester);
                    }
                }
            } else if ("all".equalsIgnoreCase(status)) {
                if ("APPROVED".equalsIgnoreCase(approvedstatus)) {
                    sla = tStudentLeaveRepository.findApprovedStudentLeavesByDateRange(startDate, endDate);
                } else if ("NOTAPPROVED".equalsIgnoreCase(approvedstatus)) {
                    sla = tStudentLeaveRepository.findUnapprovedStudentLeavesByDateRange(startDate, endDate);
                } else {
                    sla = tStudentLeaveRepository.findStudentLeavesByDateRange(startDate, endDate);
                }
            }
            return sla;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessResourceFailureException("Error Getting Student Leave List Report.", e);
        }
    }

    public T_StudentLeave getStudentLeaveDetails(@NotNull @NotBlank String fid) {
        try {
            Optional<T_StudentLeave> result = tStudentLeaveRepository.findStudentLeaveById(fid);
            return result.orElse(null);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching student leave details with student leave ID: " + fid, ex);
        }
    }

    @Transactional(readOnly = true)
    private String generateNextStudentleaveid() {
        try {
            Integer lastUsedStudentleaveid = tStudentLeaveRepository.findLastUsedStudentleaveid();

            System.out.println("[test] studentleaveid " + lastUsedStudentleaveid);

            return lastUsedStudentleaveid != null ? String.valueOf(lastUsedStudentleaveid + 1) : "1";
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error generating next studentleaveid", ex);
        }
    }

    public Long countLeaveDaysWithoutWeekends(String studentid, String isshortterm, String semPhaseId) {
        try {
            Long count = tStudentLeaveRepository.countLeaveDaysWithoutWeekends(studentid, isshortterm, semPhaseId);
            return count != null ? count : 0L;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error counting leave days without weekends", ex);
        }
    }

    @Transactional
    public boolean deleteStudentLeaveApplication(@NotBlank String studentLeaveId) {
        try {
            Optional<T_StudentLeave> studentLeave = tStudentLeaveRepository.findByStudentleaveid(studentLeaveId);

            if (studentLeave.isPresent()) {
                int rowsDeleted = tStudentLeaveRepository.deleteByStudentleaveid(studentLeaveId);
                return rowsDeleted > 0;
            }
            return false;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error deleting student leave application with ID: " + studentLeaveId, ex);
        }
    }

}

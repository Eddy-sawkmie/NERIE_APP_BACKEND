package com.nic.nerie.t_participantattendance.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import com.nic.nerie.t_participantattendance.model.T_ParticipantAttendance;
import com.nic.nerie.t_participantattendance.repository.T_ParticipantAttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Validated
public class T_ParticipantAttendanceService {

    private static final Logger log = LoggerFactory.getLogger(T_ParticipantAttendanceService.class);

    private final T_ParticipantAttendanceRepository attendanceRepository;

    @Autowired
    public T_ParticipantAttendanceService(T_ParticipantAttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAttendance(
            @NotBlank(message = "Phase ID cannot be blank") String phaseId,
            @NotBlank(message = "User code cannot be blank") String usercode) {
        log.debug("Fetching attendance details for phaseId: {} and usercode: {}", phaseId, usercode);
        try {
            return attendanceRepository.findParticipantAttendanceDetails(phaseId, usercode);
        } catch (Exception e) {
            log.error("Error fetching participant attendance details for phaseId {} usercode {}: {}", phaseId, usercode, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramsForParticipantAttendance(@NotNull @NotBlank String usercode, @NotNull @NotBlank String officecode, @NotNull @NotBlank String urole){
        try {
            switch (urole) {
                case "A":
                    return getProgramsForParticipantAttendanceRoleA(officecode);
                case "U": 
                    return getProgramsForParticipantAttendanceRoleU(usercode, officecode);
                default:
                    return getProgramsForParticipantAttendanceDefault(usercode);
            }
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Error fetching programs for participant attendance", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramsForParticipantAttendanceRoleA(@NotNull @NotBlank String officecode) {
        return attendanceRepository.findProgramsForParticipantAttendanceRoleA(officecode);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramsForParticipantAttendanceRoleU(@NotNull @NotBlank String usercode, @NotNull @NotBlank String officecode) {
        return attendanceRepository.findProgramsForParticipantAttendanceRoleU(officecode, usercode);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramsForParticipantAttendanceDefault(@NotNull @NotBlank String usercode) {
        return attendanceRepository.findProgramsForParticipantAttendanceDefault(usercode);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveOrUpdateAttendance(
            String userCode,
            String phaseid,
            String programtimetablecode,
            List<String> applicationCodes) {
        try {
            this.deleteParticipantAttendanceByProgramTimetableCodeAndPhaseId(programtimetablecode, phaseid);

            if (applicationCodes == null) {
                applicationCodes = List.of();
            }

            for (String applicationCode : applicationCodes) {
                this.insertParticipantAttendance(userCode, applicationCode, phaseid, programtimetablecode);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save/update attendance", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertParticipantAttendance(
            @NotNull @NotBlank String entryUserCode,
            @NotNull @NotBlank String applicationcode,
            @NotNull @NotBlank String phaseId,
            @NotNull @NotBlank String programTimeTableCode) {
        try {
            int rowsAffected = attendanceRepository.insertParticipantAttendance(
                    entryUserCode, applicationcode, phaseId, programTimeTableCode);

            if (rowsAffected != 1) {
                throw new IllegalStateException("Failed to insert participant attendance for application code: " + applicationcode);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to insert participant attendance", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteParticipantAttendanceByProgramTimetableCodeAndPhaseId(
            @NotNull @NotBlank String programtimetablecode,
            @NotNull @NotBlank String phaseid) {
        try {
            attendanceRepository.deleteByProgramTimetableCodeAndPhaseId(programtimetablecode, phaseid);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete participant attendance", e);
        }
    }
}

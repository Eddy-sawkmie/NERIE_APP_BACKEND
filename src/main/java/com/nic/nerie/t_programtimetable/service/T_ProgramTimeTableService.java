package com.nic.nerie.t_programtimetable.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nic.nerie.t_programtimetable.model.T_ProgramTimeTable;
import com.nic.nerie.t_programtimetable.repository.T_ProgramTimeTableRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class T_ProgramTimeTableService {

    private final T_ProgramTimeTableRepository repository;

    @Autowired
    public T_ProgramTimeTableService(T_ProgramTimeTableRepository repository) {
        this.repository = repository;
    }

    public List<Object[]> getParticipantTimetable(@NotNull @NotBlank String phaseid, @NotNull @NotBlank String usercode) {
        try {
            return repository.findParticipantTimetable(phaseid, usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving participant timetable", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramSessions(@NotNull @NotBlank String phaseid, @NotNull @NotBlank String programday) {
        try {
            Short day = Short.valueOf(programday);
            return repository.findProgramSessions(phaseid, day);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid program day format: " + programday);
        }
    }

    public Optional<T_ProgramTimeTable> findById(String timetableCode) {
        return repository.findById(timetableCode);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getProgramsTimetableByUserrole(@NotNull @NotBlank String usercode, @NotNull @NotBlank String officecode,
        @NotNull @NotBlank String userrole) {
        try {
            switch (userrole) {
                case "A":
                    return repository.getProgramsTimetableByOfficecode(officecode);
                case "U":
                    return repository.getProgramsTimetableByOfficecodeAndUsercode(officecode, usercode);
                default:
                    return repository.getProgramsTimetableByUsercodeAndEndDate(usercode);
            }
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching program timetable by user role", ex);
        }
    }

    public boolean checkProgramTimetableClashExistence(@NotNull T_ProgramTimeTable programTimeTable) {
        return repository.existsProgramTimetableClash(programTimeTable.getPhaseid().getPhaseid(), programTimeTable.getProgramdate(), programTimeTable.getMtvenuerooms().getRoomcode(), 
        programTimeTable.getStarttime(), programTimeTable.getEndtime(), programTimeTable.getProgramtimetablecode());
    }

    public boolean checkProgramTimetableExistence(@NotNull T_ProgramTimeTable programTimeTable) {
        return repository.existsProgramTimetable(programTimeTable.getPhaseid().getPhaseid(), programTimeTable.getProgramday(), 
            programTimeTable.getStarttime(), programTimeTable.getEndtime(), programTimeTable.getProgramtimetablecode());
    }

    @Transactional
    public Boolean saveProgramTimeTable(@NotNull T_ProgramTimeTable newProgramTimeTable, List<String> rpslnos) {
        if (newProgramTimeTable.getProgramtimetablecode() == null || newProgramTimeTable.getProgramtimetablecode().isBlank())
            newProgramTimeTable.setProgramtimetablecode(generateNewProgramtimetablecode());
    
        try {
            repository.save(newProgramTimeTable);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_ProgramTimeTable entity | Exception = " + ex);
        }

        if (rpslnos != null) {
            repository.deleteMTProgramttResourcePersonEntryByProgramtimetablecode(newProgramTimeTable.getProgramtimetablecode());

            try {
                for (String rpslno : rpslnos) {
                    repository.createMTProgramttResourcePersonEntryByProgramtimetablecode(newProgramTimeTable.getProgramtimetablecode(),
                     rpslno);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error saving mt_programttresourceperson tuple | Exception = " + ex);
            }
        }

        return true;
    }

    private String generateNewProgramtimetablecode() {
        Integer lastUsedCode = repository.getLastUsedProgramtimetablecode();
        return lastUsedCode == null ? "1" : String.valueOf(lastUsedCode + 1);
    }

    public List<T_ProgramTimeTable> getSubjectDaysByPhaseId(@NotNull @NotBlank String phaseid) {
        try {
            return repository.getSubjectDaysByPhaseId(phaseid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error getting phaseId" + phaseid, ex);
        }
    }
}
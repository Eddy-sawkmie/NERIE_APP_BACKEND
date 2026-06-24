package com.nic.nerie.mt_program_members.service;

import com.nic.nerie.mt_program_members.model.MT_ProgramMembers;
import com.nic.nerie.mt_program_members.repository.MT_ProgramMembersRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class MT_ProgramMembersService {
    private final MT_ProgramMembersRepository mtProgramMembersRepository;

    public MT_ProgramMembersService(MT_ProgramMembersRepository mtProgramMembersRepository) {
        this.mtProgramMembersRepository = mtProgramMembersRepository;
    }

    public List<MT_ProgramMembers> getProgramMembers(String programCode, String phaseId) {
        return mtProgramMembersRepository.getProgramMembers(programCode,phaseId);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void insertProgramMembersFromArraylist(@NotNull @NotEmpty List<String> coordinators, @NotNull @NotBlank String programcode, 
        @NotNull @NotBlank String phaseid) {
        boolean headCoordinatorSaved = false;

        try {
            for (String coordinator : coordinators) {
                if (!headCoordinatorSaved) {
                    // first coordinator in coordinators list is the head co-ordinator
                    mtProgramMembersRepository.createProgramMembersEntry(programcode, coordinator, phaseid, "1", "0");
                    headCoordinatorSaved = true;
                } else
                    mtProgramMembersRepository.createProgramMembersEntry(programcode, coordinator, phaseid, "0", "0");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error creating m_program_memebers entry", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteByProgramcode(@NotNull @NotBlank String programcode) {
        try {
            if (mtProgramMembersRepository.deleteByProgramcode(programcode.trim()) == 0)
                throw new Exception();
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting MT_ProgramMembers entity by programcode " + programcode, ex);
        }
    }

    public List<Object[]> getMembersByProgramAndPhase(@NotNull @NotBlank String pcode,
                                                      @NotNull @NotBlank String phaseid) {
        try {
            return mtProgramMembersRepository.findMembersByProgramAndPhase(pcode, phaseid);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String setCoordinatorAsLocalCoordinator(List<String> programMemberIds) {
        try {
            if (programMemberIds == null || programMemberIds.isEmpty()) {
                return "-1";
            }

            boolean atLeastOneSuccess = false;

            for (String idStr : programMemberIds) {
                if (idStr != null && !idStr.trim().isEmpty()) {
                    try {
                        Integer programMemberId = Integer.parseInt(idStr.trim());

                        int rowsUpdated = mtProgramMembersRepository.setAsLocalCoordinator(programMemberId);

                        if(rowsUpdated > 0) {
                            atLeastOneSuccess = true;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid Program Member ID skipped: " + idStr);
                    }
                }
            }

            if (atLeastOneSuccess) {
                return "1"; // Success
            } else {
                return "-1"; // Failure (no records updated)
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    @Transactional(readOnly = true)
    public MT_ProgramMembers getHeadCoordinator(@NotNull @NotBlank String programCode,
                                                @NotNull @NotBlank String phaseId) {
        try {
            return mtProgramMembersRepository.findHeadCoordinator(programCode, phaseId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Head Coordinator with programCode: " + programCode + " phaseId: " + phaseId, ex);
        }
    }
}

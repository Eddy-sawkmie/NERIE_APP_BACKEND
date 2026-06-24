package com.nic.nerie.t_participantfeedbacks.service;

import com.nic.nerie.t_participantfeedbacks.model.T_ParticipantFeedbacks;
import com.nic.nerie.t_participantfeedbacks.repository.T_ParticipantFeedbacksRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

@Service
@Validated
public class T_ParticipantFeedbacksService {
    private final T_ParticipantFeedbacksRepository tParticipantFeedbacksRepository;

    @Autowired
    public T_ParticipantFeedbacksService(T_ParticipantFeedbacksRepository tParticipantFeedbacksRepository) {
        this.tParticipantFeedbacksRepository = tParticipantFeedbacksRepository;
    }

    @Transactional
    public String saveOverallFeedback(@NotNull T_ParticipantFeedbacks tpfeedback) {
        try {
            if (tpfeedback.getPfeedbackno() == null || tpfeedback.getPfeedbackno().isEmpty()) {
                Integer maxId = tParticipantFeedbacksRepository.findMaxPfeedbackNo();
                int newId = (maxId == null) ? 1 : maxId + 1;
                tpfeedback.setPfeedbackno(String.valueOf(newId));
            }

            if (tpfeedback.getEntrydate() == null) {
                tpfeedback.setEntrydate(new Date());
            }

            T_ParticipantFeedbacks savedFeedback = tParticipantFeedbacksRepository.save(tpfeedback);
            return savedFeedback.getPfeedbackno();

        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid feedback input: " + ex.getMessage(), ex);
        } catch (DataAccessResourceFailureException ex) {
            throw new DataAccessResourceFailureException("Database access error while saving feedback." , ex);
        }  catch (Exception ex) {
            throw new RuntimeException("Failed to save participant feedback due to internal error.", ex);
        }
    }

    public T_ParticipantFeedbacks getFeedbackByPhaseIdAndUserCode(@NotNull @NotBlank String phaseid,
                                                                  @NotNull @NotBlank String usercode) {
        try {
            return tParticipantFeedbacksRepository.getByPhaseIdAndUserCode(phaseid, usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Failed to fetch participant feedback by phase id and usercode.", ex);
        }
    }

    public List<T_ParticipantFeedbacks> getFeedbacksByPhaseId(@NotNull @NotBlank String phaseid) {
        try {
            return tParticipantFeedbacksRepository.findFeedbacksByPhaseId(phaseid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Failed to fetch Feedbacks by phase id.", ex);
        }
    }
}

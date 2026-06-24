package com.nic.nerie.t_participantanswerkey_preposttest.service;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_participantanswerkey_preposttest.model.T_ParticipantAnswerKey_PrePostTest;
import com.nic.nerie.t_participantanswerkey_preposttest.repository.T_ParticipantAnswerKey_PrePostTestRepository;
import com.nic.nerie.t_phase_preposttest.model.T_Phase_PrePostTest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class T_ParticipantAnswerKey_PrePostTestService {
    private final T_ParticipantAnswerKey_PrePostTestRepository tParticipantAnswerKeyPrePostTestRepository;

    @Autowired
    public T_ParticipantAnswerKey_PrePostTestService(T_ParticipantAnswerKey_PrePostTestRepository tParticipantAnswerKeyPrePostTestRepository) {
        this.tParticipantAnswerKeyPrePostTestRepository = tParticipantAnswerKeyPrePostTestRepository;
    }

    @Transactional
    public boolean saveNewParticipantAnswerKey(@NotNull T_ParticipantAnswerKey_PrePostTest ptest) {
        try {
            Integer maxPaid = tParticipantAnswerKeyPrePostTestRepository.findMaxPaid();
            int newPaid = (maxPaid != null) ? maxPaid + 1 : 1;
            ptest.setPaid(String.valueOf(newPaid));

            tParticipantAnswerKeyPrePostTestRepository.save(ptest);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to save ParticipantAnswerKey_PrePostTest", ex);
        }
    }

    public List<T_ParticipantAnswerKey_PrePostTest> getAllParticipantPrePostTestAnswers(@NotNull MT_Userlogin user,
                                                                                        @NotNull T_Phase_PrePostTest test,
                                                                                        @NotNull @NotBlank String testtype) {
        try {
            return tParticipantAnswerKeyPrePostTestRepository.findAllParticipantPrePostTestAnswers(user.getUsercode(), test.getTestid(), testtype);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DataAccessResourceFailureException("Failed to fetch all participant pre-post test answers", ex);
        }
    }
}

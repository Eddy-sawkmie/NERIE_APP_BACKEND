package com.nic.nerie.t_participants.service;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_participants.dto.ParticipantRecordDTO;
import com.nic.nerie.t_participants.model.T_Participants;
import com.nic.nerie.t_participants.repository.T_ParticipantsRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class T_ParticipantsService {
    private final T_ParticipantsRepository tParticipantsRepository;

    @Autowired
    public T_ParticipantsService(T_ParticipantsRepository tParticipantsRepository) {
        this.tParticipantsRepository = tParticipantsRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public boolean saveParticipantDetails(@NotNull @NotBlank String usercode, @NotNull MT_Userlogin mtUserLogin) {
        // if (usercode == null || usercode.trim().isEmpty() || mtUserLogin == null) {
        //     return false;
        // }
        try {
            T_Participants participant = new T_Participants();
            participant.setUsercode(usercode);
            participant.setMtuserlogin(mtUserLogin);
            tParticipantsRepository.save(participant);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error saving T_Participant entity");
        }
    }

    @Transactional(readOnly = true)
    public T_Participants getSpecificParticipant(@NotNull @NotBlank String usercode) {
        try {
            Optional<T_Participants> participant = tParticipantsRepository.findByUsercode(usercode);
            return participant.orElse(null);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving T_Participants by usercode " + usercode, ex);
        }
    }

    public List<Object[]> getProgramParticipants(@NotNull @NotBlank String phaseid) {
        return tParticipantsRepository.getProgramParticipants(phaseid);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean insertOrUpdateParticipantsByCC(@NotNull T_Participants participant) {
        try {
            String usercode = participant.getUsercode();
            String stateCode = participant.getMstatesparticipant().getStatecode();
            String registeredBy = participant.getMtuserlogin().getUsercode();
            
            if (tParticipantsRepository.findByUsercode(usercode).isEmpty()) {
                tParticipantsRepository.insertParticipant(usercode, stateCode, registeredBy);
            } else {
                tParticipantsRepository.updateParticipant(usercode, stateCode, registeredBy);
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Object[]> checkAndGetParticipantDetails(@NotNull @NotBlank String userid) {
        return tParticipantsRepository.findParticipantDetailsByUserid(userid);
    }

    public Page<ParticipantRecordDTO> getParticipants(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.isBlank()) {
            return tParticipantsRepository.getParticipants(pageable);
        }
        return tParticipantsRepository.searchParticipants(search, pageable);
    }
}
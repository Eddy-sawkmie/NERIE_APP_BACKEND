package com.nic.nerie.m_participantofficetypes.service;

import com.nic.nerie.m_participantofficetypes.model.M_ParticipantOfficeTypes;
import com.nic.nerie.m_participantofficetypes.repository.M_ParticipantOfficeTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class M_ParticipantOfficeTypesService {
    private final M_ParticipantOfficeTypesRepository participantOfficeTypesRepository;

    @Autowired
    public M_ParticipantOfficeTypesService (M_ParticipantOfficeTypesRepository participantOfficeTypesRepository) {
        this.participantOfficeTypesRepository = participantOfficeTypesRepository;
    }

    public List<M_ParticipantOfficeTypes> getAllParticipantOfficeType() {
        return participantOfficeTypesRepository.findAllOrderedByCode();
    }

    public Optional<M_ParticipantOfficeTypes> findById(String officeTypeCode) {
        return participantOfficeTypesRepository.findById(officeTypeCode);
    }
}

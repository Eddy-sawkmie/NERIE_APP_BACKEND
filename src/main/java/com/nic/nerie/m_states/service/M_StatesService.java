package com.nic.nerie.m_states.service;

import com.nic.nerie.m_states.model.M_States;
import com.nic.nerie.m_states.repository.M_StatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class M_StatesService {
    private final M_StatesRepository statesRepository;

    @Autowired
    public M_StatesService(M_StatesRepository statesRepository) {
        this.statesRepository = statesRepository;
    }

    public List<M_States> getAllStates() {
        return statesRepository.findAllByOrderByStatename();
    }

    public Optional<M_States> findById(String stateCode) {
        return statesRepository.findById(stateCode);
    }
}
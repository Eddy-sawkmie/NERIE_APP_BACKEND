package com.nic.nerie.m_minorities.service;

import com.nic.nerie.m_minorities.model.M_Minorities;
import com.nic.nerie.m_minorities.repository.M_MinoritiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class M_MinoritiesService {
    private final M_MinoritiesRepository minoritiesRepository;

    @Autowired
    public M_MinoritiesService(M_MinoritiesRepository minoritiesRepository) {
        this.minoritiesRepository = minoritiesRepository;
    }

    public List<M_Minorities> getAllMinorities() {
        return minoritiesRepository.findAllByOrderByMinoritycode();
    }

    public Optional<M_Minorities> findById(String minorityCode) {
        return minoritiesRepository.findById(minorityCode);
    }
}
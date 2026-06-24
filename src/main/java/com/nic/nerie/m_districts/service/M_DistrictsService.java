package com.nic.nerie.m_districts.service;

import com.nic.nerie.m_districts.model.M_Districts;
import com.nic.nerie.m_districts.repository.M_DistrictsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class M_DistrictsService {
    private final M_DistrictsRepository districtsRepository;

    @Autowired
    public M_DistrictsService(M_DistrictsRepository districtsRepository) {
        this.districtsRepository = districtsRepository;
    }

    public List<M_Districts> getStateDistrict(String statecode) {
        return districtsRepository.findByStatecodeOrderByDistrictname(statecode);
    }

    public Optional<M_Districts> findById(String districtCode) {
        return districtsRepository.findById(districtCode);
    }
}
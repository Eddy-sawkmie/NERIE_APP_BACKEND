package com.nic.nerie.m_districts.repository;

import com.nic.nerie.m_districts.model.M_Districts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface M_DistrictsRepository extends JpaRepository<M_Districts, String> {
    @Query("SELECT d FROM M_Districts d WHERE d.mstates.statecode = :statecode ORDER BY d.districtname")
    List<M_Districts> findByStatecodeOrderByDistrictname(String statecode);
}
package com.nic.nerie.m_minorities.repository;

import com.nic.nerie.m_minorities.model.M_Minorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface M_MinoritiesRepository extends JpaRepository<M_Minorities, String> {
    List<M_Minorities> findAllByOrderByMinoritycode();
}
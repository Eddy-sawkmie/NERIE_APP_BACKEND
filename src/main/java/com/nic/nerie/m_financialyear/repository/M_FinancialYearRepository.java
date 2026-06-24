package com.nic.nerie.m_financialyear.repository;

import com.nic.nerie.m_financialyear.model.M_FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface M_FinancialYearRepository extends JpaRepository<M_FinancialYear, String> {
    @Query(value = "SELECT fyid, fyname, fystart, fyend, fyvalue, " +
            "CASE " +
            "  WHEN CURRENT_DATE BETWEEN fystart AND fyend THEN 1 " +
            "  WHEN CURRENT_DATE < fystart THEN 2 " +
            "  ELSE 0 " +
            "END as current_status " +
            "FROM nerie.m_financialyear " +
            "ORDER BY CAST(fyid as INT) DESC", nativeQuery = true)
    List<Object[]> getAllFinancialYear();

    @Query(value = "SELECT * FROM nerie.m_financialyear, nerie.mt_userlogin u WHERE u.usercode = :usercode", nativeQuery = true)
    List<Object[]> getFinancialYearByUsercode(@Param("usercode") String usercode);

    @Query("SELECT MAX(CAST(f.fyid AS int)) FROM M_FinancialYear f")
    Integer getMaxFyId();
}

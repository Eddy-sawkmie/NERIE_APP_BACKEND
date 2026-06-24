package com.nic.nerie.mt_la_usermapping.repository;

import com.nic.nerie.mt_la_usermapping.model.MT_LeaveApplication_UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MT_LeaveApplication_UserMappingRepository extends JpaRepository<MT_LeaveApplication_UserMapping, Integer> {
    @Query(value = "SELECT larolecode FROM mt_la_usermapping WHERE usercode = :uid", nativeQuery = true)
    Optional<Integer> findRoleCodeByUserCode(@Param("uid") String userCode);

    @Query(value = "SELECT MAX(CAST(lausermapcode AS INTEGER)) FROM mt_la_usermapping", nativeQuery = true)
    Optional<Integer> findMaxLausermapcode();

    @Query(value = "SELECT * FROM mt_la_usermapping WHERE usercode = :usercode", nativeQuery = true)
    Optional<MT_LeaveApplication_UserMapping> findByUsercode(@Param("usercode") String usercode);
}

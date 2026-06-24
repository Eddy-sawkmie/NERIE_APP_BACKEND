package com.nic.nerie.audittrail.repository;

import com.nic.nerie.audittrail.model.Audittrail;
import com.nic.nerie.audittrail.model.Audittrail_Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudittrailRepository extends JpaRepository<Audittrail, Audittrail_Id> {
    @Query("SELECT a FROM Audittrail a ORDER BY a.id.entrydate DESC")
    Page<Audittrail> findAllOrderByEntryDateDesc(Pageable pageable);

    @Query("SELECT a FROM Audittrail a WHERE " +
            "LOWER(a.id.userid) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.id.actiontaken) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.id.pageurl) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.id.ipaddress) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "ORDER BY a.id.entrydate DESC")
    Page<Audittrail> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}

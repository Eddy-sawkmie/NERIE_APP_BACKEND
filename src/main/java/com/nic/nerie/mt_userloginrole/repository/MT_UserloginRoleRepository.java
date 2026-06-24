package com.nic.nerie.mt_userloginrole.repository;

import com.nic.nerie.mt_userloginrole.model.MT_UserloginRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MT_UserloginRoleRepository extends JpaRepository<MT_UserloginRole, Integer> {
    Optional<MT_UserloginRole> findByRoleCode(String roleCode);

    @Query(value = "SELECT * FROM nerie.mt_userloginrole WHERE role_code IN ('A', 'Z', 'C', 'F')", nativeQuery = true)
    List<MT_UserloginRole> getRolesForProcessMapping();

    @Modifying
    @Query(value = "DELETE FROM nerie.mt_role_process WHERE role_id = :roleid", nativeQuery = true)
    void deleteRoleProcessMappings(@Param("roleid") Integer roleid);

    @Modifying
    @Query(value = "INSERT INTO nerie.mt_role_process (role_id, processcode) VALUES (:roleid, :processcode)", nativeQuery = true)
    void insertRoleProcessMapping(@Param("roleid") Integer roleid, @Param("processcode") Integer processcode);

    @Query(value = "SELECT processcode FROM nerie.mt_role_process WHERE role_id = :roleid", nativeQuery = true)
    List<Integer> getProcessCodesByRole(@Param("roleid") Integer roleid);

    @Query(value = "SELECT role_id FROM nerie.mt_userloginrole WHERE role_name = :rolename", nativeQuery = true)
    Integer findRoleIdByRoleName(@Param("rolename") String rolename);

}
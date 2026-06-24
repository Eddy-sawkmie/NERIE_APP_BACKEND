package com.nic.nerie.mt_userloginrole.service;

import com.nic.nerie.mt_userloginrole.model.MT_UserloginRole;
import com.nic.nerie.mt_userloginrole.repository.MT_UserloginRoleRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class MT_UserloginRoleService {
    private final MT_UserloginRoleRepository mtUserloginRoleRepository;

    @Autowired
    public MT_UserloginRoleService(MT_UserloginRoleRepository mtUserloginRoleRepository) {
        this.mtUserloginRoleRepository = mtUserloginRoleRepository;
    }

    public MT_UserloginRole findByRoleCode(@NotNull @NotBlank String roleCode) {
        try {
            Optional<MT_UserloginRole> role = mtUserloginRoleRepository.findByRoleCode(roleCode);
            return role.isPresent() ? role.get() : null;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("rolecode cannot be null or blank", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching MT_UserloginRole entity for role code: " + roleCode, ex);
        }
    }

    public List<MT_UserloginRole> getRolesForProcessMapping() {
        try {
            return mtUserloginRoleRepository.getRolesForProcessMapping();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving roles for process mapping", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public void saveRoleProcessMapping(@NotNull Integer roleId, @NotNull List<Integer> processCodes) {
        try {
            // Delete old mappings
            mtUserloginRoleRepository.deleteRoleProcessMappings(roleId);

            // Insert new mappings
            for (Integer processcode : processCodes) {
                mtUserloginRoleRepository.insertRoleProcessMapping(roleId, processcode);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error saving role-process mapping for roleId: " + roleId, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Integer> getProcessCodesByRole(@NotNull Integer roleId) {
        try {
            return mtUserloginRoleRepository.getProcessCodesByRole(roleId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving process codes for roleId: " + roleId, ex);
        }
    }

    @Transactional(readOnly = true)
    public int[] getProcessCodesByRoleName(@NotBlank String rolename) {

        try {

            Integer roleId = mtUserloginRoleRepository.findRoleIdByRoleName(rolename);

            if (roleId == null) {
                return new int[0];
            }

            List<Integer> processCodes = mtUserloginRoleRepository.getProcessCodesByRole(roleId);

            // 3️Convert List<Integer> → int[]
            return processCodes.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Error retrieving process codes for role name: " + rolename, ex);
        }
    }
}

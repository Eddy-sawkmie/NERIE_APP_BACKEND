package com.nic.nerie.m_departments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_departments.repository.M_DepartmentsRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

@Service
public class M_DepartmentsService {
    private final M_DepartmentsRepository mDepartmentsRepository;

    @Autowired
    public M_DepartmentsService(M_DepartmentsRepository mDepartmentsRepository) {
        this.mDepartmentsRepository = mDepartmentsRepository;
    }

    @Transactional(readOnly = true)
    public M_Departments getDepartmentByDepartmentcode(@NotNull @NotBlank String departmentcode) {
        departmentcode = departmentcode.trim();

        try {
            Optional<M_Departments> departmentOptional = mDepartmentsRepository.findById(departmentcode);
            return departmentOptional.isPresent() ? departmentOptional.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching M_Departments entity", ex);
        }
    }

    public Boolean checkDepartmentExists(@NotNull @NotBlank String departmentcode) {
        return mDepartmentsRepository.existsById(departmentcode);
    }

    public List<M_Departments> getDepartmentList(@NotBlank String officecode) {
        try {
            return mDepartmentsRepository.findAllOrderByDepartmentcodeAscAndDepartmentnameAsc(officecode);
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving M_Departments list", ex);
        }
    }

    public boolean checkDepartmentExist(M_Departments mdept) {
        if (mdept.getDepartmentcode() == null || mdept.getDepartmentcode().trim().isEmpty()) {
            // Check only by name
            Optional<M_Departments> existing = mDepartmentsRepository.getDepartmentByName(mdept.getDepartmentname());
            return existing.isPresent();
        } else {
            // Exclude current department by code
            Optional<M_Departments> existing = mDepartmentsRepository.getDepartmentByNameExcludingCode(
                    mdept.getDepartmentname(), mdept.getDepartmentcode());
            return existing.isPresent();
        }
    }

    public boolean saveDepartmentDetails(M_Departments mdept) {
        try {
            // Only generate ID if departmentcode is null or empty
            if (mdept.getDepartmentcode() == null || mdept.getDepartmentcode().trim().isEmpty()) {
                Integer maxCode = mDepartmentsRepository.getMaxDepartmentCode();
                Integer newCode = (maxCode == null) ? 1 : maxCode + 1;
                mdept.setDepartmentcode(newCode.toString());
            }

            mDepartmentsRepository.save(mdept);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public M_Departments getDepartmentByCode(@NotNull @NotBlank String departmentcode) {
        try {
            Optional<M_Departments> department = mDepartmentsRepository.getDepartmentByCode(departmentcode);
            return department.orElse(null);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Department by code: departmentcode: " + departmentcode, ex);
        }
    }

    public List<M_Departments> getDepartments(@NotBlank String officecode) {
        try {
            return mDepartmentsRepository.findAllDepartments(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getDepartments with officecode: " + officecode, ex);
        }
    }

    @Transactional(readOnly = true)
    public String getDepartmentsCount(String officecode) {
        try {
            return String.valueOf(mDepartmentsRepository.getDepartmentsCount(officecode));
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Error retrieving departments count", e);
        }
    }
}

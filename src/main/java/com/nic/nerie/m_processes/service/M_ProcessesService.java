package com.nic.nerie.m_processes.service;

import com.nic.nerie.m_processes.model.M_Processes;
import com.nic.nerie.m_processes.repository.M_ProcessesRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class M_ProcessesService {
    private final M_ProcessesRepository mProcessesRepository;

    @Autowired
    public M_ProcessesService(M_ProcessesRepository mProcessesRepository) {
        this.mProcessesRepository = mProcessesRepository;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProcessesForUserNavigation(@NotNull @NotBlank String usercode) {
        try {
            return mProcessesRepository.getProcessesForUserNavigation(usercode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving processes for navigation", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPrincipalProcesses() {
        try {
            return mProcessesRepository.getPrincipalProcesses();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Principal processes", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAllProcesses() {
        try {
            return mProcessesRepository.getAllProcesses();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving all processes", ex);
        }
    }

    /*
     * This method retrieves Local-admin processes
     * @params usercode to perform matching
     * @returns List of Local-admin processes
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public List<Object[]> getLocalAdminProcesses(@NotNull @NotBlank String usercode) {
        try {
            return mProcessesRepository.getLocalAdminProcesses(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Local-admin processes", ex);
        }
    }

    public List<Object[]> getUserProcesses(@NotNull @NotBlank String usercode) {
        return mProcessesRepository.getUserProcesses(usercode);
    }

    public List<Integer> getMenuProcesses(@NotNull @NotBlank int mainmenucode) {
        try {
            return mProcessesRepository.findProcessCodesByMainMenuCode(mainmenucode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Menu Process by process code: " + mainmenucode, ex);
        }
    }

    @Transactional
    public String createUserProcess(@NotNull @NotBlank String usercode, @NotNull @NotBlank int processcode) {
        try {
            mProcessesRepository.insertUserProcess(usercode, processcode);
            return "1";
        } catch (Exception e) {
            return "-1";
        }
    }

    /*
     * This method checks if the user with usercode can perform the process with processcode
     * @param usercode The id of the user
     * @param processcode The id of the process
     * @returns Boolean specifying if the user can perform the process
     * @throws DataAccessResourceFailureException for data store access errors
     */
    @Transactional(readOnly = true)
    public Boolean isProcessGranted(@NotNull @NotBlank String usercode, @NotNull Integer processcode) {
        try {
            return mProcessesRepository.userProcessExists(usercode.trim(), processcode) > 0;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking process grant by usercode " + usercode, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<M_Processes> getActualAllProcesses() {
        try {
            return mProcessesRepository.getActualAllProcessesNative();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving actual all processes", ex);
        }
    }
}

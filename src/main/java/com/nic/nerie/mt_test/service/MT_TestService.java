package com.nic.nerie.mt_test.service;

import com.nic.nerie.mt_test.model.MT_Test;
import com.nic.nerie.mt_test.repository.MT_TestRepository;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MT_TestService {
    private final MT_TestRepository mtTestRepository;
    private final MT_UserloginService mtUserloginService;

    @Autowired
    public MT_TestService(MT_TestRepository mtTestRepository, MT_UserloginService mtUserloginService) {
        this.mtTestRepository = mtTestRepository;
        this.mtUserloginService = mtUserloginService;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTestList(String usercode) {
        try {
            return mtTestRepository.getTestList(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving MT_Test list by usercode " + usercode, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTestsBySubjectAndUser(@NotNull @NotBlank String usercode, @NotNull @NotBlank String subjectcode) {
        try {
            return mtTestRepository.getTestsBySubjectAndUser(usercode, subjectcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving MT_Test list for user " + usercode + " and subject " + subjectcode, ex);
        }
    }

    public String createtests(MT_Test testDetail, String usercode) {
        try {
            // Validate usercode
            if (usercode == null || usercode.isEmpty()) {
                return "-1"; // Invalid input
            }

            // Set entry date
            Date entrydate = new Date();
            testDetail.setEntrydate(entrydate);

            // Get logged-in user
            MT_Userlogin login = mtUserloginService.getUserloginFromAuthentication();
            if (login == null) {
                return "-1"; // Unauthorized
            }

            // Set usercode from authenticated user
            testDetail.setUsercode(login);

            // Generate testid if not present
            if (testDetail.getTestid() == null || testDetail.getTestid().isEmpty()) {
                Integer maxId = mtTestRepository.getMaxTestId();
                int newId = (maxId == null) ? 1 : maxId + 1;
                testDetail.setTestid(String.valueOf(newId));
            }

            MT_Test savedTest = mtTestRepository.saveAndFlush(testDetail);

            return (savedTest != null) ? "1" : "-1";

        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    @Transactional(readOnly = true)
    public String getFullMark(@NotNull @NotBlank String testid) {
        try {
            return mtTestRepository.getFullMarkByTestid(testid.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving fullmark of MT_Test by testid " + testid, ex);
        }
    }
    @Transactional
    public void deleteTest(String testid) {
        if (!mtTestRepository.existsById(testid)) {
            throw new IllegalArgumentException("Test not found: " + testid);
        }
        mtTestRepository.deleteById(testid);
    }

}

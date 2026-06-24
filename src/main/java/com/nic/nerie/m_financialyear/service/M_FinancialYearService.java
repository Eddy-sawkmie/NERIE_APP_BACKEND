package com.nic.nerie.m_financialyear.service;

import com.nic.nerie.m_financialyear.model.M_FinancialYear;
import com.nic.nerie.m_financialyear.repository.M_FinancialYearRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class M_FinancialYearService {
    private final M_FinancialYearRepository mFinancialYearRepository;

    @Autowired
    public M_FinancialYearService(M_FinancialYearRepository mFinancialYearRepository) {
        this.mFinancialYearRepository = mFinancialYearRepository;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getfy() {
        try {
            return mFinancialYearRepository.getAllFinancialYear();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving all M_FinancialYear", ex);
        }
    }

    public List<Object[]> getFyByUsercode(@NotNull @NotBlank String usercode) {
        return mFinancialYearRepository.getFinancialYearByUsercode(usercode);
    }

    @Transactional
    public void saveFinancialYear(M_FinancialYear fy) {
        try {
            // --- NEW: ID Generation Logic ---
            if (fy.getFyid() == null || fy.getFyid().isEmpty()) {
                Integer maxId = mFinancialYearRepository.getMaxFyId();
                
                // If maxId is null, the table is empty, so we start at 1. Otherwise, add 1.
                int nextId = (maxId == null) ? 1 : maxId + 1;
                
                // Convert back to String and set it
                fy.setFyid(String.valueOf(nextId));
            }
            
            // Check if dates are provided to avoid NullPointerExceptions
            if (fy.getFystart() != null && fy.getFyend() != null) {
                Date start = fy.getFystart();
                Date end = fy.getFyend();

                // Define the date formatters we need
                SimpleDateFormat fullYearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat shortYearFormat = new SimpleDateFormat("yy");
                SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

                // 1. Format fyname (e.g., "2021-22")
                String startYear = fullYearFormat.format(start);       // "2021"
                String endYearShort = shortYearFormat.format(end);     // "22"
                fy.setFyname(startYear + "-" + endYearShort);

                // 2. Format fyvalue (e.g., "2021-04##2022-03")
                String startFormatted = yearMonthFormat.format(start); // "2021-04"
                String endFormatted = yearMonthFormat.format(end);     // "2022-03"
                fy.setFyvalue(startFormatted + "##" + endFormatted);
            }
            
            // Save to database
            mFinancialYearRepository.save(fy);

        } catch (Exception ex) {
             throw new DataAccessResourceFailureException("Error saving M_FinancialYear", ex);
        }
    }
}

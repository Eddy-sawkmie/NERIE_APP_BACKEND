package com.nic.nerie.m_holidays.service;

import com.nic.nerie.m_holidays.model.M_Holidays;
import com.nic.nerie.m_holidays.repository.M_HolidaysRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

@Service
@Validated
public class M_HolidaysService {
    private final M_HolidaysRepository mHolidaysRepository;

    @Autowired
    public M_HolidaysService(M_HolidaysRepository mHolidaysRepository) {
        this.mHolidaysRepository = mHolidaysRepository;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getapproveCCFY(@NotNull @NotBlank String usercode) {
        try {
            return mHolidaysRepository.getapproveCCFY(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error executing getapproveCCFY", ex); // I genuinely don't know what it retrieves...
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getFYHolidayList(@NotNull @NotBlank String finyearstart, @NotNull @NotBlank String finyearend) {
        try {
            return mHolidaysRepository.getFYHolidayList(finyearstart, finyearend);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving holiday list by financial years", ex);
        }
    }

    /*
     * This method saves M_Holidays instance to database
     * @params newHoliday The M_Holidays instance to save or update
     * @returns The newly saved M_Holidays instance
     * @throws RuntimeExceptions for any errors
     */
    @Transactional(readOnly = false)
    public M_Holidays saveHolidayDetails(@NotNull M_Holidays newHoliday) {
        try {
            return mHolidaysRepository.save(newHoliday);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_Holidays entity", ex);
        }
    }

    /*
     * This method removes M_Holidays instance from database by holidaydate
     * @params holidaydate The date to identify M_Holidays
     * @returns void
     * @throws RuntimeException for any database errors
     */
    @Transactional
    public void removeHolidayDetailsByHolidaydate(@NotNull Date holidaydate) {
        try {
            mHolidaysRepository.deleteById(holidaydate);
        } catch (Exception ex) {
            throw new RuntimeException("Error removing M_Holidays entity by holidaydate", ex);
        }
    }
}

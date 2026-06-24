package com.nic.nerie.m_holidays.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.nic.nerie.m_holidays.model.M_Holidays;

public interface M_HolidaysRepository extends JpaRepository<M_Holidays, Date> {
        @Query(value = "SELECT DISTINCT " +
                        "CASE WHEN TO_CHAR(C.startdate, 'MM') >= '01' AND TO_CHAR(C.startdate, 'MM') <= '03' " +
                        "THEN (DATE_PART('year', C.startdate) - 1) || '-04##' || TO_CHAR(C.startdate, 'yyyy') || '-03' "
                        +
                        "WHEN TO_CHAR(C.startdate, 'MM') >= '04' AND TO_CHAR(C.startdate, 'MM') <= '12' " +
                        "THEN TO_CHAR(C.startdate, 'yyyy') || '-04##' || (DATE_PART('year', C.startdate) + 1) || '-03' END AS key, "
                        +
                        "CASE WHEN TO_CHAR(C.startdate, 'MM') >= '01' AND TO_CHAR(C.startdate, 'MM') <= '03' " +
                        "THEN (DATE_PART('year', C.startdate) - 1) || '-' || TO_CHAR(C.startdate, 'yy') " +
                        "WHEN TO_CHAR(C.startdate, 'MM') >= '04' AND TO_CHAR(C.startdate, 'MM') <= '12' " +
                        "THEN TO_CHAR(C.startdate, 'yyyy') || '-' || SUBSTRING((DATE_PART('year', C.startdate) + 1) || '', 3) END AS value "
                        +
                        "FROM nerie.mt_programdetails C, nerie.m_programs P, nerie.mt_program_members PC " +
                        "WHERE P.programcode = PC.programcode AND PC.usercode = :usercode AND C.finalized = 'Y' AND C.closed = 'N' "
                        +
                        "ORDER BY value", nativeQuery = true)
        List<Object[]> getapproveCCFY(@Param("usercode") String usercode);

        @Query(value = "SELECT holidaydate, holidayreason FROM m_holidays " +
                        "WHERE TO_CHAR(holidaydate, 'yyyy-MM') >= :finyearstart " +
                        "AND TO_CHAR(holidaydate, 'yyyy-MM') <= :finyearend " +
                        "ORDER BY holidaydate, holidayreason", nativeQuery = true)
        List<Object[]> getFYHolidayList(@Param("finyearstart") String finyearstart,
                        @Param("finyearend") String finyearend);
}

package com.nic.nerie.m_activities.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.m_activities.model.M_Activities;

public interface M_ActivitiesRepository extends JpaRepository<M_Activities, String> {
    @Query(value = "SELECT * FROM m_activities WHERE phaseid = :phaseid", nativeQuery = true)
    List<M_Activities> findActivitiesByPhaseId(@Param("phaseid") String phaseid);

    @Query("select max(cast(activityid as int)) from M_Activities")
    Integer getLastUsedActivityId();
}

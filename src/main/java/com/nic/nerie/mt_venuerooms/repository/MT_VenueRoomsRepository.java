package com.nic.nerie.mt_venuerooms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.mt_venuerooms.model.MT_VenueRooms;

public interface MT_VenueRoomsRepository extends JpaRepository<MT_VenueRooms, String> {
    @Query("FROM MT_VenueRooms vr WHERE vr.mvenues.moffices.officecode = :officecode ORDER BY vr.mvenues.venuename, vr.roomname")
    List<MT_VenueRooms> findByOfficecodeOrderByVenueAndRoom(@Param("officecode") String officecode);
    
    @Query(value = """
        SELECT r.roomcode AS key, r.roomname AS value 
        FROM nerie.mt_venuerooms r 
        WHERE r.venuecode = :venuecode ORDER BY r.roomcode
    """, nativeQuery = true)
    List<Object[]> getRoomcodeRoomnameByVenuecode(@Param("venuecode") String venuecode);

    @Query("SELECT CASE WHEN COUNT(vr) > 0 THEN true ELSE false END " +
            "FROM MT_VenueRooms vr " +
            "WHERE UPPER(vr.roomname) = :roomname " +
            "AND vr.mvenues.venuecode = :venuecode " +
            "AND (:roomcode IS NULL OR vr.roomcode != :roomcode)")
    boolean existsByRoomnameAndVenuecodeAndNotRoomcode(@Param("roomname") String roomname, @Param("venuecode") String venuecode,
                                                       @Param("roomcode") String roomcode);

    @Query("SELECT MAX(CAST(vr.roomcode AS int)) FROM MT_VenueRooms vr")
    Integer getLastUsedRoomcode();
}

package com.nic.nerie.mt_venuerooms.service;

import com.nic.nerie.mt_venuerooms.model.MT_VenueRooms;
import com.nic.nerie.mt_venuerooms.repository.MT_VenueRoomsRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class MT_VenueRoomsService {
    private final MT_VenueRoomsRepository mtVenueRoomsRepository;

    @Autowired
    public MT_VenueRoomsService(MT_VenueRoomsRepository mtVenueRoomsRepository) {
        this.mtVenueRoomsRepository = mtVenueRoomsRepository;
    }

    /*
     * This method checks MT_VenueRooms existence by roomcode 
     * @params roomcode The roomcode of MT_VenueRooms for comparison 
     * @returns A boolean specifying the existence
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public boolean existsByRoomcode(@NotNull @NotBlank String roomcode) {
        try {
            return mtVenueRoomsRepository.existsById(roomcode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking MT_VenueRooms existence by roomcode", ex);
        }
    }

    /*
     * This method retrieves MT_VenueRooms entity by roomcode 
     * @params roomcode The roomcode of MT_VenueRooms for comparison 
     * @returns The retrieved MT_VenueRooms if found otherwise null 
     * @throws DataAccessResourceFailureException for data access errors
     */
    public MT_VenueRooms findByRoomcode(String roomcode) {
        try {
            Optional<MT_VenueRooms> mtVenueRoom = mtVenueRoomsRepository.findById(roomcode);
            return mtVenueRoom.isPresent() ? mtVenueRoom.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving MT_VenueRooms entity by roomcode", ex);
        }
    }

    public List<MT_VenueRooms> getAllOfficeVenueRooms(String officecode) {
        return mtVenueRoomsRepository.findByOfficecodeOrderByVenueAndRoom(officecode);
    }

    /*
     * This method retrieves roomcode and roomname of MT_VenueRooms by venuecode
     * @params venuecode The venuecode to perform matching
     * @returns A list of roomcode and roomname as Object array
     * @throws DataAccessResourceFailureException for data access errors 
     */
    public List<Object[]> getRoomcodeRoomnameByVenuecode(@NotNull @NotBlank String venuecode) {
        try {
            return mtVenueRoomsRepository.getRoomcodeRoomnameByVenuecode(venuecode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving roomcode, roomname by venuecode", ex);
        }
    }

    /*
     * This method checks MT_VenueRooms existence by its instance
     * @params mvenueroom The MT_VenueRooms entity for comparison
     * @returns A boolean specifying the existence
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public boolean checkVenueRoomExist(@NotNull MT_VenueRooms mvenueroom) {
        try {
            String roomcode = mvenueroom.getRoomcode();
            String roomname = mvenueroom.getRoomname().trim().toUpperCase();
            String venuecode = mvenueroom.getMvenues().getVenuecode();
            
            return mtVenueRoomsRepository.existsByRoomnameAndVenuecodeAndNotRoomcode(roomname, venuecode, roomcode.isEmpty() ? null : roomcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking MT_VenueRooms existence", ex);
        }
    }

    /*
     * This method generates new roomcode (only for creation) and saves it
     * @params newVenueRooms The new MT_VenueRooms to be created or updated
     * @returns The newly saved MT_VenueRooms entity
     * @throws RuntimeException for any errors
     */
    @Transactional(readOnly = false)
    public MT_VenueRooms saveVenueRoomsDetails(@NotNull MT_VenueRooms newVenueRoom) {
        try {
            if (newVenueRoom.getRoomcode() == null || newVenueRoom.getRoomcode().isBlank())
                newVenueRoom.setRoomcode(generateNewRoomcode());
            
            return mtVenueRoomsRepository.save(newVenueRoom);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating MT_VenueRooms entity", ex);
        }
    }

    /*
     * This method generates the new roomcode for MT_VenueRooms creation
     * @returns The new roomcode as String
     * @throws RuntimeException for any errors
     */
    @Transactional(readOnly = true)
    private String generateNewRoomcode() {
        try {
            Integer lastRooomcodeUsed = mtVenueRoomsRepository.getLastUsedRoomcode();
            return lastRooomcodeUsed == null ? "1" : String.valueOf(lastRooomcodeUsed + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating new roomcode", ex);
        }
    }
}

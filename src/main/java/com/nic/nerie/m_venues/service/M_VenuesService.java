package com.nic.nerie.m_venues.service;

import com.nic.nerie.m_venues.model.M_Venues;
import com.nic.nerie.m_venues.repository.M_VenuesRepository;

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
public class M_VenuesService {
    private final M_VenuesRepository mVenuesRepository;

    @Autowired
    public M_VenuesService(M_VenuesRepository mVenuesRepository) {
        this.mVenuesRepository = mVenuesRepository;
    }

    /*
     * This method checks M_Venues existence by venuecode
     * @params venuecode Of the target entity
     * @returns Boolean specifying existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByVenuecode(@NotNull @NotBlank String venuecode) {
        try {
            return mVenuesRepository.existsById(venuecode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Venues existence by venuecode " + venuecode, ex);
        }
    }

    @Transactional(readOnly = true)
    public M_Venues findByVenuecode(@NotNull @NotBlank String venuecode) {
        try {
            Optional<M_Venues> venue = mVenuesRepository.findByVenuecode(venuecode.trim());
            return venue.isPresent() ? venue.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Venues of venuecode " + venuecode, ex);
        }
    }

    /*
     * This method returns a list of mt_programvenue and m_venue information acc to phaseid
     * @param phaseid The phaseid used for matching
     * @return A list of Object array
     * @throws DataAccessResourceFailureExceptino for database access errors
     */
    @Transactional(readOnly = true)
    public List<Object[]> getByPhaseid(@NotNull @NotBlank String phaseid) {
        try {
            return mVenuesRepository.getByPhaseid(phaseid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving mt_programvenue and m_venues by phaseid", ex);
        }
    } 

    /*
     * This method returns a list of M_Venues by matching officecode and ordered acc to venuename
     * @param officecode The officecode to be matched
     * @return A list of M_Venues
     * @throws DataAccessResourceFailureException for database access errors
     */
    @Transactional(readOnly = true)
    public List<M_Venues> getAllOfficeVenues(@NotNull @NotBlank String officecode) {
        try {
            return mVenuesRepository.findByOfficecodeOrderByVenuename(officecode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Venues list by officecode " + officecode, ex);
        }
    }

    /*
     * This method checks if M_Venue entity already exists in db by venuename, officecode, venuecode
     * @param venuename The venue's name to be matched
     * @param officecode The venue's officecode to be matched
     * @param venuecode The venue's venuecode to be matched
     * @return A boolean specifying if venue exists or not
     * @throws DataAccessResourceFailureException for database access error
     */
    @Transactional(readOnly = true)
    public boolean checkVenueExist(String venuename, String officecode, String venuecode) {
        try {
            return mVenuesRepository.existsByVenuenameOfficecodeAndVenuecode(venuename, officecode, venuecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Venue existence by venuename, officecode and venuecode", ex);
        }
    }

    /*
     * This method sets venuecode for new M_Venues entity and save it
     * @params venue The new M_Venues entity to be saved
     * @returns The saved M_Venues entity
     * @throws RuntimeException for any errors
     */
    @Transactional
    public M_Venues saveVenueDetails(@NotNull M_Venues venue) {
        try {
            if (venue.getVenuecode() == null || venue.getVenuecode().isBlank())
                venue.setVenuecode(generateNewVenueCode());

            return mVenuesRepository.save(venue);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_Venues entity", ex);
        }
    }

    /*
     * This methods generates the next venuecode for M_Venue entity for creation
     * @returns The new venuecode as String
     * @throws RuntimeException for any errors
     */
    @Transactional(readOnly = true)
    private String generateNewVenueCode() {
        try {
            Integer lastVenueCodeUsed = mVenuesRepository.findMaxVenuecode();
            return lastVenueCodeUsed == null ? "1" : String.valueOf(lastVenueCodeUsed + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating new venue code", ex);
        }
    }
}

package com.nic.nerie.mt_programdetails.service;

import com.nic.nerie.m_coursecategories.model.M_CourseCategories;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.repository.M_PhasesRepository;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.m_programs.repository.M_ProgramsRepository;
import com.nic.nerie.mt_program_members.service.MT_ProgramMembersService;
import com.nic.nerie.mt_programdetails.model.MT_ProgramDetails;
import com.nic.nerie.mt_programdetails.repository.MT_ProgramDetailsRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class MT_ProgramDetailsService {
    private final MT_ProgramDetailsRepository mtProgramDetailsRepository;
    private final MT_ProgramMembersService mtProgramMembersService;
    private final M_PhasesService mPhasesService;
    private final M_ProgramsRepository mProgramsRepository;
    private final M_PhasesRepository mPhasesRepository;

    @Autowired
    public MT_ProgramDetailsService(
            MT_ProgramDetailsRepository mtProgramDetailsRepository,
            MT_ProgramMembersService mtProgramMembersService,
            M_PhasesService mPhasesService,
            M_ProgramsRepository mProgramsRepository, M_PhasesRepository mPhasesRepository) {
        this.mtProgramDetailsRepository = mtProgramDetailsRepository;
        this.mtProgramMembersService = mtProgramMembersService;
        this.mPhasesService = mPhasesService;
        this.mProgramsRepository = mProgramsRepository;
        this.mPhasesRepository = mPhasesRepository;
    }

    @Transactional(readOnly = true)
    public Boolean existsByPhaseid(@NotNull @NotBlank String phaseid) {
        try {
            return mtProgramDetailsRepository.existsByPhaseid(phaseid.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking existence of phaseid " + phaseid, ex);
        }
    }

    public List<Object[]> getFinancialYearsByOfficeCode(String officecode) {
        return mtProgramDetailsRepository.findDistinctFinancialYearsByOfficeCode(officecode);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramDaysByPhaseid(@NotNull @NotBlank String phaseid) {
        try {
            return mtProgramDetailsRepository.getProgramDaysByPhaseid(phaseid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving program days by phaseid " + phaseid, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgramTimetableDetailsByPhaseidAndProgramday(@NotNull @NotBlank String phaseid, @NotNull @NotBlank String programday) {
        try {
            return mtProgramDetailsRepository.getProgramTimetableDetailsByPhaseidAndProgramday(phaseid.trim(), Integer.valueOf(programday.trim()));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving program timetable details by phaseid " + phaseid + " and programday " + programday, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public MT_ProgramDetails saveProgramDetails(@NotNull MT_ProgramDetails mtProgramDetails) {
        if (mtProgramDetails.getProgramdetailid() == null || mtProgramDetails.getProgramdetailid().isBlank())
            mtProgramDetails.setProgramdetailid(generateNextProgramdetailid());

        try {
            return mtProgramDetailsRepository.save(mtProgramDetails);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving MT_ProgramDetails entity | Exception = " + ex);
        }
    }

    @Transactional(readOnly = true)
    public String getProgramdetailsidByProgramcodeAndPhaseid(@NotNull @NotBlank String programcode, @NotNull @NotBlank String phaseid) {
        try {
            Integer programdetailsid = mtProgramDetailsRepository.getProgramdetailidByProgramcodeAndPhaseid(programcode, phaseid);
            return programdetailsid != null ? String.valueOf(programdetailsid) : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retreiving M_ProgramDetails's id by programcode " + programcode + " phaseid " + phaseid, ex);
        }
    }

    @Transactional(readOnly = true)
    public String generateNextProgramdetailid() {
        try {
            Integer lastUsedId = mtProgramDetailsRepository.getLastUsedProgramdetailid();
            return lastUsedId == null ? "1" : String.valueOf(lastUsedId + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating next programdeatilid", ex);
        }
    }

    public List<Object[]> getFYofunclosecourse(String officecode) {
        return mtProgramDetailsRepository.findDistinctFYOfUnclosedCourseByOfficeCode(officecode);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean closePhase(@NotNull @NotBlank String phaseId, @NotBlank @NotBlank String closingReport) {
        try {
            if (mtProgramDetailsRepository.closePhase(phaseId, closingReport) > 0)
                return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // Unused method
    @Transactional(rollbackFor = Exception.class)
    public Boolean reopenPhase(@NotNull @NotBlank String programdetailid) {
        try {
            if (mtProgramDetailsRepository.reopenPhase(programdetailid) > 0)
                return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return false;
    }       

    @Transactional(readOnly = true)
    public MT_ProgramDetails getProgramDetailsByProgramdetailid(@NotNull @NotBlank String programdetailid) {
        try {
            Optional<MT_ProgramDetails> programDetails = mtProgramDetailsRepository.findById(programdetailid.trim());
            return programDetails != null ? programDetails.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving MT_ProgramDetails by programdetailid " + programdetailid, ex);
        }
    }

    public List<MT_ProgramDetails> getProgramDetailsByProgramCode(@NotNull @NotBlank String programCode) {
        try {
            return mtProgramDetailsRepository.getProgramDetailsByProgramCode(programCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error Fetching Get Program Details By Program Code: " + programCode, ex);
        }
    }

    public List<Object[]> listMcoursesforRPFY(@NotBlank String usercode, @NotBlank String officecode, @NotBlank String fystart, @NotBlank String fyend, @NotBlank String urole) {

        if ("A".equals(urole) || "Z".equals(urole)) {
            return mtProgramDetailsRepository.findForRoleAorZ(officecode, fystart, fyend);
        } else {
            return mtProgramDetailsRepository.findForMember(officecode, usercode, fystart, fyend);
        }
    }

    public List<Object[]> getReportFinancialYearLA(@NotBlank String officecode) {
        return mtProgramDetailsRepository.getReportFinancialYearLA(officecode);
    }

    public List<Object[]> listProgramsForTimeTable(@NotBlank String usercode, @NotBlank String officecode, @NotBlank String urole) {
        if ("A".equalsIgnoreCase(urole)) {
            return mtProgramDetailsRepository.findProgramsForTimeTableByOffice(officecode);
        } else if ("U".equalsIgnoreCase(urole)) {
            return mtProgramDetailsRepository.findProgramsForTimeTableEnteredByUser(officecode, usercode);
        } else {
            return mtProgramDetailsRepository.findProgramsForTimeTableByMember(usercode);
        }
    }

    public List<Object[]> getOngoingProgramList(Integer coursetype, Integer limit, Integer offset) {
        try {
            // Validate parameters
            if (coursetype == null) {
                coursetype = 0; // default value
            }
            if (limit == null || limit <= 0) {
                limit = 10; // default limit
            }
            if (offset == null || offset < 0) {
                offset = 0; // default offset
            }

            return mtProgramDetailsRepository.findOngoingPrograms(coursetype, limit, offset);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching ongoing programs: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getUpcomingProgramList(Integer coursetype, Integer limit, Integer offset) {
        try {
            // Validate parameters
            if (coursetype == null) {
                coursetype = 0; // default value
            }
            if (limit == null || limit <= 0) {
                limit = 10; // default limit
            }
            if (offset == null || offset < 0) {
                offset = 0; // default offset
            }

            return mtProgramDetailsRepository.findUpcomingPrograms(coursetype, limit, offset);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching upcoming programs: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getCompletedProgramList(Integer coursetype, Integer limit, Integer offset) {
        try {
            // Validate parameters
            if (coursetype == null) {
                coursetype = 0; // default value
            }
            if (limit == null || limit <= 0) {
                limit = 10; // default limit
            }
            if (offset == null || offset < 0) {
                offset = 0; // default offset
            }

            return mtProgramDetailsRepository.findCompletedPrograms(coursetype, limit, offset);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching completed programs: " + e.getMessage(), e);
        }
    }

    public Integer getCountUpcomingProgram() {
        try {
            return mtProgramDetailsRepository.countUpcomingPrograms();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching upcoming program count: " + e.getMessage(), e);
        }
    }

    public Integer getCountOngoingProgram() {
        try {
            return mtProgramDetailsRepository.countOngoingPrograms();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching ongoing program count: " + e.getMessage(), e);
        }
    }

    public Integer getCountCompletedProgram() {
        try {
            return mtProgramDetailsRepository.countCompletedPrograms();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching completed program count: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getDashboardOngoing(String uc, String oc, String ur) {
        try {
            String userParam = ur.equals("A") ? "ALL" : uc;
            return mtProgramDetailsRepository.getDashboardOngoing(userParam, oc);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching dashboard ongoing", ex);
        }
    }

    public List<Object[]> getDashboardUpcoming(String uc, String oc, String ur) {
        try {
            String userParam = ur.equals("A") ? "ALL" : uc;
            return mtProgramDetailsRepository.getDashboardUpcoming(userParam, oc);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching dashboard upcoming", ex);
        }
    }

    public List<Object[]> getCompletedProgDetailsForPublic(String oc) {
        try {
            return mtProgramDetailsRepository.getCompletedProgDetailsForPublic(oc);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching completed ProgDetails For Public with office code: " + oc, ex);
        }
    }

    public List<Object[]> getLandingDashboardOngoing() {
        try {
            return mtProgramDetailsRepository.getLandingDashboardOngoing();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching landing ongoing programs", ex);
        }
    }

    public List<Object[]> getLandingDashboardUpcoming() {
        try {
            return mtProgramDetailsRepository.getLandingDashboardUpcoming();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching landing upcoming programs", ex);
        }
    }

    public List<Object[]> getLandingCompletedProgDetails() {
        try {
            return mtProgramDetailsRepository.getLandingCompletedProgDetails();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching landing completed programs", ex);
        }
    }

    public List<Object[]> getLandingCompletedProgDetailsByFy(String fystart, String fyend){
         try {
            return mtProgramDetailsRepository.getLandingCompletedProgDetailsByFy(fystart,fyend);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching landing completed programs by FY", ex);
        }
    }

    public List<Object[]> getMoreOngoingProgramList(Integer coursetype) {
        try {
            // Default value if coursetype is null
            if (coursetype == null) {
                coursetype = 0;
            }

            return mtProgramDetailsRepository.findMoreOngoingPrograms(coursetype);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching more ongoing programs: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getMoreUpcomingProgramList(Integer coursetype) {
        try {
            // Set default value if coursetype is null
            if (coursetype == null) {
                coursetype = 0;
            }

            return mtProgramDetailsRepository.findMoreUpcomingPrograms(coursetype);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching more upcoming programs: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getMoreCompletedProgramList(Integer coursetype) {
        try {
            // Set default value if coursetype is null
            if (coursetype == null) {
                coursetype = 0;
            }

            return mtProgramDetailsRepository.findMoreCompletedPrograms(coursetype);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching more completed programs: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = false)
    public void approveProgram(@NotNull MT_ProgramDetails programDetails) {
        try {
            mtProgramDetailsRepository.approveProgram(
                programDetails.getFinalized(),
                programDetails.getApprovalletter(), 
                programDetails.getApprovaldate(), 
                programDetails.getMtuserloginapproval().getUsercode(), 
                programDetails.getProgramdetailid());
        } catch (Exception ex) {
            throw new RuntimeException("Error approving program by principal-director", ex);
        }
    }

    @Transactional(readOnly = false)
    public void rejectProgram(@NotNull MT_ProgramDetails programDetails) {
        try {
            mtProgramDetailsRepository.rejectProgram(
                programDetails.getFinalized(),
                programDetails.getRejectionletter(), 
                programDetails.getRejectiondate(), 
                programDetails.getRejectionremarks(),
                programDetails.getMtuserloginrejection().getUsercode(),
                programDetails.getProgramdetailid());
        } catch (Exception ex) {
            throw new RuntimeException("Error rejecting program by principal-director", ex);
        }
    }

    @Transactional(readOnly = false)
    public void deleteProgramAndRelatedEntities(@NotNull @NotBlank String programcode) {
        try {
           mtProgramMembersService.deleteByProgramcode(programcode);
           mtProgramDetailsRepository.deleteProgramVenuesByProgramcode(programcode.trim());
           mtProgramDetailsRepository.deleteByProgramcode(programcode.trim());
           
            Long phaseCount = mPhasesService.getPhasesCountByProgramcode(programcode);
            if (phaseCount != null && phaseCount > 0)
                mPhasesService.deleteByProgramcode(programcode);
        
            mProgramsRepository.deleteByProgramcode(programcode);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateProgramDetailsSubmitted(M_Programs mprogram, String[] coordinators,
                                                 String phasedescription, String phaseid) throws Exception {
        try {
            // 1. Save Program
            mProgramsRepository.save(mprogram);
            String pcode = mprogram.getProgramcode();

            // 2. Save Phase
            Integer phaseno = mtProgramDetailsRepository.findPhaseNoByPhaseId(phaseid);
            M_Phases mphases = new M_Phases();
            mphases.setPhaseid(phaseid);
            mphases.setProgramcode(mprogram);
            mphases.setPhaseno(phaseno != null ? phaseno.toString() : "1");
            mphases.setPhasedescription(phasedescription);
            mPhasesRepository.save(mphases);

            // 3. Handle Coordinators (Specific Logic: Index 0 is Head)
            if (coordinators != null && coordinators.length > 0) {
                mtProgramDetailsRepository.deleteProgramMembersByPhaseId(phaseid);
                for (int i = 0; i < coordinators.length; i++) {
                    // Logic from old code: First one is '1', others '0'
                    String isHead = (i == 0) ? "1" : "0";
                    mtProgramDetailsRepository.insertProgramMemberWithRole(pcode, coordinators[i], phaseid, isHead);
                }
            }

            // 4. Update Program Details (EntryDate only, do not nullify existing dates)
            Integer id2 = mtProgramDetailsRepository.findProgramDetailId(pcode, phaseid);

            // Note: Since we don't have start/end dates here, we fetch the existing entity
            // to ensure we don't accidentally overwrite dates with null if we used 'new MT_ProgramDetails()'
            MT_ProgramDetails pd = mtProgramDetailsRepository.findById(id2 != null ? id2.toString() : "0").orElse(new MT_ProgramDetails());

            if(pd.getProgramdetailid() == null && id2 != null) pd.setProgramdetailid(id2.toString());

            pd.setPhaseid(mphases);
            pd.setProgramcode(mprogram);
            // Only update what changed or needs resetting
            pd.setEntrydate(new Date());
            pd.setClosed("N");
            // pd.setFinalized("N"); // Optional: depends if you want to reset this status

            mtProgramDetailsRepository.save(pd);

            return true;
        } catch (Exception e) {
            throw e;
        }
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateProgramDetailsApproved(M_Programs mprogram, String[] venues, String[] coordinators,
                                                String phasedescription, String startdate, String enddate,
                                                String lastdate, String courseclosedate, String phaseid) throws Exception {
        try {
            // 1. Save M_Programs
            mProgramsRepository.save(mprogram);
            String pcode = mprogram.getProgramcode();

            // 2. Update M_Phases
            Integer phaseno = mtProgramDetailsRepository.findPhaseNoByPhaseId(phaseid);
            M_Phases mphases = new M_Phases();
            mphases.setPhaseid(phaseid);
            mphases.setProgramcode(mprogram);
            mphases.setPhaseno(phaseno != null ? phaseno.toString() : "1");
            mphases.setPhasedescription(phasedescription);
            mPhasesRepository.save(mphases);

            // 3. Update Venues (Delete All -> Insert New)
            if (venues != null && venues.length > 0) {
                mtProgramDetailsRepository.deleteProgramVenuesByPhaseId(phaseid);
                for (String venue : venues) {
                    mtProgramDetailsRepository.insertProgramVenue(pcode, venue, phaseid);
                }
            }

            // 4. Update Coordinators (Delete All -> Insert New with Head Coordinator Logic)
            if (coordinators != null && coordinators.length > 0) {
                mtProgramDetailsRepository.deleteProgramMembersByPhaseId(phaseid);
                for (int i = 0; i < coordinators.length; i++) {
                    // EXACT LOGIC FROM SNIPPET: Index 0 is Head ('1'), others are ('0')
                    String isHead = (i == 0) ? "1" : "0";
                    mtProgramDetailsRepository.insertProgramMemberWithRole(pcode, coordinators[i], phaseid, isHead);
                }
            }

            // 5. Update Program Details (Dates Only)
            // Note: Using dd-MM-yyyy to match your Frontend Datepicker
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date startdate2 = dateFormat.parse(startdate);
            Date enddate2 = dateFormat.parse(enddate);
            Date lastdate2 = dateFormat.parse(lastdate);
            Date courseclosedate2 = dateFormat.parse(courseclosedate);

            Integer id2 = mtProgramDetailsRepository.findProgramDetailId(pcode, phaseid);

            if (id2 != null) {
                // Perform the specific Update query (not save()) to preserve other columns
                mtProgramDetailsRepository.updateProgramDates(
                        id2.toString(), startdate2, enddate2, lastdate2, courseclosedate2
                );
            }

            return true;
        } catch (ParseException e) {
            throw new Exception("Error parsing date", e);
        } catch (Exception e) {
            throw e;
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public boolean saveProgramDetails(String pcode, String phaseid, String[] venues, String startdate, String enddate, String lastdate,
                                      String courseclosedate , M_CourseCategories programcategory, String programcattwo) throws ParseException {
        try {
            if (venues != null) {
                mtProgramDetailsRepository.deleteProgramVenuesByPhaseId(phaseid);

                for (String venueCode : venues) {
                    if (venueCode != null && !venueCode.trim().isEmpty()) {
                        mtProgramDetailsRepository.insertProgramVenue(pcode, venueCode, phaseid);
                    }
                }
            }

            MT_ProgramDetails pd = mtProgramDetailsRepository.findByProgramcodeAndPhaseidNative(pcode, phaseid)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ProgramDetails not found for programcode: " + pcode + " and phaseid: " + phaseid));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            pd.setStartdate(dateFormat.parse(startdate));
            pd.setEnddate(dateFormat.parse(enddate));
            pd.setLastdate(dateFormat.parse(lastdate));
            pd.setCourseclosedate(dateFormat.parse(courseclosedate));
            pd.setEntrydate(new Date());

            mtProgramDetailsRepository.save(pd);

            M_Programs p = mProgramsRepository.findByProgramcode(pcode)
                    .orElseThrow(() -> new EntityNotFoundException("Program not found for programcode: " + pcode));

            if (programcategory != null) {
                p.setMcoursecategories(programcategory);
            }
            p.setProgramcattwo(programcattwo);

            mProgramsRepository.save(p);

            return true;

        } catch (EntityNotFoundException e) {
            System.err.println("Entity not found: " + e.getMessage());
            throw e;
        } catch (ParseException e) {
            System.err.println("Date parsing failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error in saveProgramDetails: " + e.getMessage());
            throw new RuntimeException("Failed to save program details", e);
        }
    }

    public List<Object[]> getProgramDetailsBasedOnCodeToPopulateForm(@NotNull @NotBlank String pcode) {
        try {
            return mtProgramDetailsRepository.findProgramDetailsBasedOnCodeToPopulateForm(pcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching program details based on code to populate form: " + pcode, ex);
        }
    }

    public MT_ProgramDetails getProgramDetails(@NotNull @NotBlank String programdetailid) {
        try {
            return mtProgramDetailsRepository.findProgramDetailsById(programdetailid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching program details based on program detail Id: " + programdetailid, ex);
        }
    }

    @Transactional(readOnly = true)
    public Object[] getProgramInfoForNotification(@NotNull @NotBlank String pdid) {
        List<Object[]> results = mtProgramDetailsRepository.findProgramInfoForNotification(pdid.trim());
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }
}

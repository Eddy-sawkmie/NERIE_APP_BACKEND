package com.nic.nerie.t_programmaterials.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_programmaterials.model.T_ProgramMaterials;
import com.nic.nerie.t_programmaterials.service.T_ProgramMaterialsService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/program-materials")
public class T_ProgramMaterialsController {
    private final T_ProgramMaterialsService programMaterialsService;
    private final M_FinancialYearService mFinancialYearService;
    private final MT_UserloginService mtUserloginService;
    private final M_PhasesService mPhasesService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger genericLogger = LoggerFactory.getLogger(T_ProgramMaterialsController.class);
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_ProgramMaterialsController(T_ProgramMaterialsService programMaterialsService, 
                                        M_FinancialYearService mFinancialYearService,
                                        MT_UserloginService mtUserloginService,
                                        M_PhasesService mPhasesService,
                                        M_ProcessesService mProcessesService,
                                        AudittrailService audittrailService) {
        this.programMaterialsService = programMaterialsService;
        this.mFinancialYearService = mFinancialYearService;
        this.mtUserloginService = mtUserloginService;
        this.mPhasesService = mPhasesService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Public endpoint
     */

    @PostMapping("/list")
    public ResponseEntity<List<Object[]>> getProgramMaterials(@RequestParam("phaseid") String phaseId, HttpServletRequest request) {
        // validating phaseId
        if (phaseId == null || phaseId.isBlank() || phaseId.trim().length() > 6 || !mPhasesService.existsById(phaseId))
            return ResponseEntity.badRequest().body(Collections.emptyList());

        return ResponseEntity.ok(programMaterialsService.getMaterialsForPhase(phaseId));
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Co-ordinator Faculty)
     * 'Upload Materials' process (processcode = 11)
     */
    @GetMapping("/manage")
    public String renderProgramMaterialsUploadPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Upload Materials, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 11)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Upload Materials, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("fylist", mFinancialYearService.getfy());

        return "pages/upload-program-material";
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Co-ordinator Faculty)
     * Endpoint tied with 'Upload Materials' process (processcode = 11)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveProgramMaterial(
            @RequestParam("phaseid") String phaseid,
            @RequestParam("reportormaterial") String reportormaterial,
            @RequestParam("materialdesc") String materialdesc,
            @RequestParam("file1") MultipartFile file1,
            HttpServletRequest request
    ) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 11)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required fields
        if (phaseid == null || phaseid.isBlank() || reportormaterial == null || reportormaterial.isBlank() ||
            materialdesc == null || materialdesc.isBlank() || file1 == null)
            return ResponseEntity.badRequest().body("Required fields are missing. Please refresh the page and retry");

        // validating reportormaterial
        // reportormaterial should be either "M" (Material) or "R" (Report)
        if (!reportormaterial.equals("M") && !reportormaterial.equals("R"))
            return ResponseEntity.badRequest().body("Invalid report or material type");

        // validating phaseid
        Optional<M_Phases> existingPhase = mPhasesService.findById(phaseid);
        if (existingPhase.isEmpty())
            return ResponseEntity.badRequest().body("Invalid phase ID");

        T_ProgramMaterials newProgramMaterial = new T_ProgramMaterials();
        newProgramMaterial.setUploaddate(new Date());
        newProgramMaterial.setPhaseid(existingPhase.get());
        newProgramMaterial.setReportormaterial(reportormaterial);
        newProgramMaterial.setMaterialdesc(materialdesc.trim());
        newProgramMaterial.setMtuserlogin(user);

        try {
            if (file1.getSize() > 0) {
                newProgramMaterial.setMaterialfile(file1.getBytes());
                String filename = file1.getOriginalFilename();

                if (filename == null)
                    return ResponseEntity.badRequest().build();

                int idx = filename.lastIndexOf(".");
                if (idx == -1)
                    return ResponseEntity.ok("");
                newProgramMaterial.setMaterialfiletype(filename.substring(idx + 1));
            } else 
                return ResponseEntity.ok("1");
        } catch (Exception ex) {
            genericLogger.error(ex.toString());

            return ResponseEntity.ok("");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if ((newProgramMaterial = programMaterialsService.saveOrUpdateProgramMaterial(newProgramMaterial)) != null) {
                persistenceLogger.info("T_ProgramMaterials with programmaterialid {} saved successfully by userid {}", newProgramMaterial.getProgrammaterialid(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programmaterials with programmaterialid " + newProgramMaterial.getProgrammaterialid() + " saved successfully");
    
                return ResponseEntity.ok("2");
            }        
            
            throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("T_ProgramMaterials save failed.\nMessage: {}\nUserid: {}",ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programmaterials save failed");
            
            return ResponseEntity.ok("");
        }
    }

    @GetMapping("/view-file")
    public void viewProgramMaterialFile(HttpServletResponse response,
                                        @RequestParam("programmaterialid") String programmaterid) throws IOException {
        T_ProgramMaterials programmaterial = programMaterialsService.getByProgrammaterialid(programmaterid);

        if (programmaterial != null && programmaterial.getMaterialfile() != null) {
            byte[] fileContent = programmaterial.getMaterialfile();
            response.reset();
            response.setContentType("application/pdf"); // Or dynamically detect content type if needed
            response.setContentLength(fileContent.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(fileContent);
                out.flush();
            } catch (IOException e) {
                System.err.println("Error writing material file to output stream. " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Program material file not found.");
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Co-ordinator Faculty)
     * Endpoint tied with 'Upload Materials' process (processcode = 11)
     */
    @PostMapping("/delete")
    public ResponseEntity<String> deleteProgramMaterial(
        @RequestParam("programmaterialid") String programmaterialid,
        HttpServletRequest request
    ) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 11)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validation
        if (programmaterialid == null || programmaterialid.isBlank())
            return ResponseEntity.badRequest().body("programmaterialid cannot be null or empty");

        // deleting
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            programMaterialsService.deleteProgramMaterial(programmaterialid);
            persistenceLogger.info("T_ProgramMaterials with programmaterialid {} deleted successfully by userid {}", programmaterialid, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programmaterials with programmaterialid " + programmaterialid + " saved successfully");

            return ResponseEntity.ok("1");  // Deleted successfully
        } catch (Exception ex) {
            persistenceLogger.error("T_ProgramMaterials delete failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programmaterials delete failed");

            return ResponseEntity.ok("");   // Something went wrong...
        }     
    }

    @GetMapping("/download-material")
    public void downloadProgramMaterial(@RequestParam("coursematerialid") String coursematerialid , HttpServletResponse response) {
        try {
            T_ProgramMaterials coursematerial = programMaterialsService.getCourseMaterial(coursematerialid);
            byte[] bytearrayrf = coursematerial.getMaterialfile();
            if (bytearrayrf != null) {
                String rftype = coursematerial.getMaterialfiletype();
                if (rftype.equals("pdf")) {
                    response.setContentType("application/pdf");
                } else if (rftype.equals("jpeg") || rftype.equals("jpg")) {
                    response.setContentType("image/jpeg");
                }
                response.getOutputStream().write(bytearrayrf);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course material not found");
        }
    }
}

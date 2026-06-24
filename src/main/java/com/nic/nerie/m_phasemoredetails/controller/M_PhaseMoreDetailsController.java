package com.nic.nerie.m_phasemoredetails.controller;

import com.nic.nerie.m_phasemoredetails.model.M_PhaseMoreDetails;
import com.nic.nerie.m_phasemoredetails.service.M_PhaseMoreDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/phase-more-details")
public class M_PhaseMoreDetailsController {

    @Autowired
    private M_PhaseMoreDetailsService phaseMoreDetailsService;

    /*
     * Public endpoint
     */
    @GetMapping("/getPhaseMoreDetailsBasedOnPhaseId")
    @ResponseBody
    public ResponseEntity<M_PhaseMoreDetails> getPhaseMoreDetailsBasedOnPhaseId(@RequestParam String phaseid) {
        try {
            M_PhaseMoreDetails pmd = phaseMoreDetailsService.getPhaseMoreDetailsByPhaseId(phaseid);

            if (pmd == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }

            return new ResponseEntity<>(pmd, HttpStatus.OK); // 200 OK

        } catch (Exception e) {
            //System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Error
        }
    }
}

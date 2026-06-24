package com.nic.nerie.t_applications.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nic.nerie.t_applications.service.TApplicationsService;

@Controller
@RequestMapping("/applications")
public class TApplicationsController {
    private final TApplicationsService tApplicationsService;

    public TApplicationsController(TApplicationsService tApplicationsService) {
        this.tApplicationsService = tApplicationsService;
    }

    @PostMapping("/session-participants/list")
    public ResponseEntity<List<Object[]>> findParticipantsInSession(@RequestParam("programtimetablecode") String programtimetablecode, @RequestParam("phaseid") String phaseid) {
        if (programtimetablecode == null || programtimetablecode.isEmpty() || phaseid == null || phaseid.isEmpty()) 
            return ResponseEntity.badRequest().body(Collections.emptyList());
        
        try {
            return ResponseEntity.ok(tApplicationsService.listParticipantsInSession(programtimetablecode, phaseid));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}

package com.nic.nerie.m_processes.controller;

import com.nic.nerie.m_processes.service.M_ProcessesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/processes")
public class M_ProcessesController {
    private final M_ProcessesService mProcessesService;

    @Autowired
    public M_ProcessesController(M_ProcessesService mProcessesService) {
        this.mProcessesService = mProcessesService;
    }

    @PostMapping("/getuprocess")
    public ResponseEntity<List<Object[]>> getUserProcesses(@RequestParam("usercode") String usercode) {
        return ResponseEntity.ok(mProcessesService.getUserProcesses(usercode));
    }
}

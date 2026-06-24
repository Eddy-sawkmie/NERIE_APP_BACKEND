package com.nic.nerie.m_shortterm_phases.controller;

import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.m_shortterm_phases.service.M_ShortTerm_PhasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/short-term-phases")
public class M_ShortTerm_PhasesController {
    private final M_ShortTerm_PhasesService mShortTermPhasesService;

    @Autowired
    public M_ShortTerm_PhasesController(M_ShortTerm_PhasesService mShortTermPhasesService) {
        this.mShortTermPhasesService = mShortTermPhasesService;
    }

    @PostMapping("/getPhasesBasedOnCourse")
    @ResponseBody
    public List<M_ShortTerm_Phases> getphasesbasedoncourse(@RequestParam(value = "fystart", required = false) String fystart, @RequestParam(value = "fyend", required = false) String fyend) {
        List<M_ShortTerm_Phases> ilist = null;
        ilist = mShortTermPhasesService.getSPhaseList();
        return ilist;
    }
    //flutter api
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<M_ShortTerm_Phases>> getPhaseList() {
        return ResponseEntity.ok(mShortTermPhasesService.getSPhaseList());
    }
}

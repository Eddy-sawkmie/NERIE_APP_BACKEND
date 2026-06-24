package com.nic.nerie.m_semesters.controller;

import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_semesters.service.M_SemestersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/semesters")
public class M_SemestersController {
    private final M_SemestersService mSemestersService;

    @Autowired
    public M_SemestersController(M_SemestersService mSemestersService) {
        this.mSemestersService = mSemestersService;
    }

    @PostMapping("/getSemestersBasedOnCourse")
    @ResponseBody
    public List<M_Semesters> getsemestersbasedoncourse(@RequestParam(value = "fystart", required = false) String fystart, @RequestParam(value = "fyend", required = false) String fyend) {
        List<M_Semesters> ilist = null;
        ilist = mSemestersService.getSemesterList();
        return ilist;
    }

    //flutter api
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<M_Semesters>> getSemesterList() {
        return ResponseEntity.ok(mSemestersService.getSemesterList());
    }
}

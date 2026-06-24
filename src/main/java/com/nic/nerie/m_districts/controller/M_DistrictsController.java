package com.nic.nerie.m_districts.controller;

import com.nic.nerie.m_districts.model.M_Districts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.nic.nerie.m_districts.service.M_DistrictsService;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/districts")
public class M_DistrictsController {

    private final M_DistrictsService districtsService;

    @Autowired
    public M_DistrictsController(M_DistrictsService districtsService) {
        this.districtsService = districtsService;
    }

    @PostMapping("/get-districts")
    @ResponseBody
    public ResponseEntity<List<M_Districts>> getDistrictsForState(@RequestParam("statecode") String statecode) {
        try {
            if (statecode == null || statecode.isBlank()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<M_Districts> districts = districtsService.getStateDistrict(statecode);

            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}

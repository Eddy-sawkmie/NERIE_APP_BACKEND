package com.nic.nerie.audittrail.service;

import com.nic.nerie.audittrail.model.Audittrail;
import com.nic.nerie.audittrail.model.Audittrail_Id;
import com.nic.nerie.audittrail.repository.AudittrailRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;

@Service
public class AudittrailService {
    private final AudittrailRepository audittrailRepository;

    @Autowired
    public AudittrailService(AudittrailRepository audittrailRepository) {
        this.audittrailRepository = audittrailRepository;
    }

    public Page<Audittrail> getAuditTrailPage(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return audittrailRepository.searchByKeyword(keyword, pageable);
        } else {
            return audittrailRepository.findAllOrderByEntryDateDesc(pageable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void logAuditTrail(HashMap<String, String> auditMap, String userId, String actionTaken) {
        try {
            if (auditMap != null) {
                auditMap.put("userid", userId);
                auditMap.put("actiontaken", actionTaken);
            
                saveAuditTrail(auditMap);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error saving Audittrail", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveAuditTrail(HashMap<String, String> auditMap) {
        try {
            Audittrail_Id ids = new Audittrail_Id();
            ids.setUserid(auditMap.get("userid"));
            ids.setActiontaken(auditMap.get("actiontaken"));
            ids.setBrowser(auditMap.get("browser"));
            ids.setIpaddress(auditMap.get("ipaddress"));
            ids.setPageurl(auditMap.get("pageurl"));
            ids.setOs(auditMap.get("os"));
            ids.setEntrydate(new Date());

            Audittrail audittrail = new Audittrail();
            audittrail.setId(ids);

            audittrailRepository.save(audittrail);

        } catch (Exception ex) {
            throw new RuntimeException("Error saving audit trail", ex);
        }
    }
}
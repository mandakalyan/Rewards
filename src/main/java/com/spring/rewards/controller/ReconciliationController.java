package com.spring.rewards.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.spring.rewards.entity.Reconciliation;
import com.spring.rewards.services.ReconciliationService;

@RestController
@RequestMapping("/api")
public class ReconciliationController {

    @Autowired
    private ReconciliationService reconciliationService;

    @PostMapping("test/upload")
    public String uploadFile(@RequestParam("otlFile") MultipartFile otlFile,
                             @RequestParam("timexFile") MultipartFile timexFile,@RequestParam String weekNumber, @RequestParam String periodName,@RequestParam String yearNumber ) {
        if (otlFile.isEmpty() || timexFile.isEmpty()) {
            return "Please select both OTL and TIMEX files.";
        }

        try {
            reconciliationService.reconcile(otlFile.getInputStream(), timexFile.getInputStream(),weekNumber,periodName,yearNumber);
            return "Files uploaded and reconciliation process completed.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload files: " + e.getMessage();
        }
    }
    
    
    
    @GetMapping("/test/recordsForParent/{parentEmpId}")
    public ResponseEntity<List<Reconciliation>> getChildrenReconciliationRecords(@PathVariable Long parentEmpId) {
        List<Reconciliation> reconciliationRecords = reconciliationService.getChildrenReconciliationRecords(parentEmpId);
        if (reconciliationRecords.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(reconciliationRecords);
        }
    }
    
    
}

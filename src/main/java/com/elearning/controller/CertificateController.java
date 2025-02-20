package com.elearning.controller;

import org.springframework.web.bind.annotation.*;

import com.elearning.models.Certificate;
import com.elearning.services.CertificateService;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/user/{userId}")
    public List<Certificate> getCertificatesByUser(@PathVariable Long userId) {
        return certificateService.getCertificatesByUser(userId);
    }

    @PostMapping
    public Certificate issueCertificate(@RequestParam Long userId, @RequestParam Long courseId, @RequestParam double score) {
    	return certificateService.issueCertificate(userId, courseId, score);
        
        
    }
}

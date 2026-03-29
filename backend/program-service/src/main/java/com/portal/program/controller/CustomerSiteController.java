package com.portal.program.controller;

import com.portal.program.model.Customer;
import com.portal.program.model.Site;
import com.portal.program.repository.CustomerRepository;
import com.portal.program.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
public class CustomerSiteController {

    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findByIsActiveTrue());
    }

    @GetMapping("/sites")
    public ResponseEntity<List<Site>> getAllSites() {
        return ResponseEntity.ok(siteRepository.findByIsActiveTrue());
    }
}
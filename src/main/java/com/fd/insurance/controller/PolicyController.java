package com.fd.insurance.controller;

import com.fd.insurance.dto.PolicyDetailsResponse;
import com.fd.insurance.dto.PolicyRenewRequest;
import com.fd.insurance.dto.PolicyRequest;
import com.fd.insurance.dto.Response;
import com.fd.insurance.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policies")
public class PolicyController {

    private final PolicyService service;

    public PolicyController(PolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Response> createPolicy(
            @Valid @RequestBody PolicyRequest request) {

        service.createPolicy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Response("Policy created successfully"));
    }

    @PutMapping("/{policyNumber}/renew")
    public ResponseEntity<Response> renewPolicy(
            @PathVariable String policyNumber,
            @Valid @RequestBody PolicyRenewRequest request) {

        service.renewPolicy(policyNumber, request);

        return ResponseEntity.ok(new Response("Policy renewed successfully"));
    }

    @GetMapping("/{policyNumber}")
    public ResponseEntity<PolicyDetailsResponse>
    getPolicy(
            @PathVariable
            String policyNumber) {

        return ResponseEntity.ok(service.getPolicy(policyNumber));
    }
}
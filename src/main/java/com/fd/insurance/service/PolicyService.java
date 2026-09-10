package com.fd.insurance.service;

import com.fd.insurance.dto.PolicyDetailsResponse;
import com.fd.insurance.dto.PolicyRenewRequest;
import com.fd.insurance.dto.PolicyRequest;

public interface PolicyService {
    void createPolicy(PolicyRequest request);

    void renewPolicy(String policyNumber, PolicyRenewRequest request);

    PolicyDetailsResponse getPolicy(String policyNumber);
}

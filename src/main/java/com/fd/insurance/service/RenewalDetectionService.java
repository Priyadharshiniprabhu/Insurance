package com.fd.insurance.service;

import com.fd.insurance.entity.Policy;

import java.util.List;

public interface RenewalDetectionService {
        void detectRenewals();

        void detectRenewals(List<? extends Policy> policies);
}

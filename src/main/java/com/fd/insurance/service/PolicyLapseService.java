package com.fd.insurance.service;

import com.fd.insurance.entity.Policy;

import java.util.List;

public interface PolicyLapseService {
    void markLapsedPolicies();

    void markLapsedPolicies(List<? extends Policy> policies);
}

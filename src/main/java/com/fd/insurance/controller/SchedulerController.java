package com.fd.insurance.controller;

import com.fd.insurance.dto.SchedulerStatusResponse;
import com.fd.insurance.service.SchedulerConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/schedulers")
public class SchedulerController {

    private final SchedulerConfigurationService schedulerConfigurationService;

    public SchedulerController(
            SchedulerConfigurationService schedulerConfigurationService) {
        this.schedulerConfigurationService = schedulerConfigurationService;
    }

    @GetMapping
    public ResponseEntity<List<SchedulerStatusResponse>> getSchedulers() {
        return ResponseEntity.ok(
                schedulerConfigurationService.getSchedulerStatuses());
    }
}

package com.fd.insurance.controller;

import com.fd.insurance.entity.ReminderDLQ;
import com.fd.insurance.dto.Response;
import com.fd.insurance.service.DLQService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dlq")
public class DLQController {

    private final DLQService dlqService;

    public DLQController(DLQService dlqService) {
        this.dlqService = dlqService;
    }

    @GetMapping
    public ResponseEntity<List<ReminderDLQ>>
    getAllDLQEvents() {
        return ResponseEntity.ok(dlqService.getAllDLQEvents());
    }

    @PutMapping("/{id}/requeue")
    public ResponseEntity<Response>
    requeue(
            @PathVariable Long id) {

        dlqService.requeue(id);

        return ResponseEntity.ok(
                new Response("The entry has been requeued successfully"));
    }
}
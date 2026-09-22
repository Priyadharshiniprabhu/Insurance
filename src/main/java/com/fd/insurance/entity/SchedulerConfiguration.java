package com.fd.insurance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "scheduler_configurations",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class SchedulerConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "cron_value", nullable = false)
    private String cronValue;

    protected SchedulerConfiguration() {
    }

    public SchedulerConfiguration(String name, String cronValue) {
        this.name = name;
        this.cronValue = cronValue;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCronValue() {
        return cronValue;
    }
}

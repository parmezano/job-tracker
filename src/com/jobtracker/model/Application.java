package com.jobtracker.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Application {
    private final int id;
    private static int nextId = 1;

    private final String company;
    private final String position;
    private final String jobUrl;
    private final String jobDescription;
    private String notes;
    private final LocalDate appliedDate;
    private Status status;
    private LocalDate lastStatusChange;

    public Application(int id, String company, String position, String jobUrl,
                       String jobDescription, LocalDate appliedDate, Status status,
                       LocalDate lastStatusChange, String notes) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.jobUrl = jobUrl;
        this.jobDescription = jobDescription;
        this.appliedDate = appliedDate;
        this.status = status;
        this.lastStatusChange = lastStatusChange;
        this.notes = notes;
    }

    public int getId() { return id; }

    public String getCompany() { return company; }

    public String getPosition() { return position; }

    public String getJobUrl() { return jobUrl; }

    public String getJobDescription() { return jobDescription; }

    public String getNotes() { return notes; }

    public LocalDate getAppliedDate() { return appliedDate; }

    public Status getStatus() { return status; }

    public LocalDate getLastStatusChange() { return lastStatusChange; }


    public void setStatus(Status newStatus) {
        this.status = newStatus;
        this.lastStatusChange = LocalDate.now();
    }

    public void setNotes(String newNotes) {
        this.notes = newNotes;
    }

    public long daysSinceLastStatusChange(){
        return ChronoUnit.DAYS.between(this.lastStatusChange, LocalDate.now());
    }

    public String toString(){
        return String.format("[%d] %s | %s. Status: %s (last change is %d days ago). Applied: %s",
                id, company, position, status, daysSinceLastStatusChange(), appliedDate);
    }
}

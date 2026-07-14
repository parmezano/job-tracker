package com.jobtracker.service;

import com.jobtracker.model.Application;
import com.jobtracker.model.Status;
import com.jobtracker.repository.ApplicationRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ApplicationService {
    final private int FOLLOW_UP_WAIT_DAYS = 6;

    private final ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public Application addApplication(String company, String position, String jobUrl,
                                      String jobDescription, String notes) {
        int id = repository.nextId();
        LocalDate appliedDay = LocalDate.now();

        return new Application(id, company, position, jobUrl, jobDescription,
                appliedDay, Status.APPLIED, appliedDay, notes);
    }

    public List<Application> listAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Application::getAppliedDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Application> listByStatus(Status status) {
        return listAll().stream()
                .filter(app -> app.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Application findById(int id) {
        return repository.findById(id);
    }

    public boolean updateStatus(int id, Status newStatus) {
        Application application = repository.findById(id);

        if(application == null) { return false;}

        application.setStatus(newStatus);
        repository.save(application);

        return true;
    }

    public boolean updateNotes(int id, String notes) {
        Application application = repository.findById(id);

        if (application == null) { return false;}

        application.setNotes(notes);
        repository.save(application);
        return true;
    }

    public List<Application> getApplicationsNeedFollowUp() {
        return listAll().stream()
                .filter(app -> app.getStatus().isActive())
                .filter(app -> app.daysSinceLastStatusChange() >= FOLLOW_UP_WAIT_DAYS)
                .collect(Collectors.toList());
    }

    public int getFollowUpThresholdDays() {
        return FOLLOW_UP_WAIT_DAYS;
    }
}

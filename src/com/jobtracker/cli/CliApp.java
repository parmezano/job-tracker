package com.jobtracker.cli;

import com.jobtracker.model.Application;
import com.jobtracker.model.Status;
import com.jobtracker.service.ApplicationService;

import java.util.List;
import java.util.Scanner;

public class CliApp {

    private final ApplicationService service;
    private final Scanner scanner;

    public CliApp(ApplicationService applicationService, Scanner scanner) {
        this.service = applicationService;
        this.scanner = scanner;
    }

    public void run() {
        printWelcome();
        showFollowUpReminders();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addApplicationFlow();
                case "2" -> listAllFlows();
                case "3" -> updateStatusFlow();
                case "4" -> showFollowUpReminders();
                case "5" -> {
                    System.out.println("Bye! Good luck in job search.");
                    running = false;
                }
                default -> System.out.println("Wrong choice. Try again.");
            }
        }
    }

    private void printWelcome() {
        System.out.println("=========================================");
        System.out.println("   Job Application Tracker");
        System.out.println("=========================================");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("--- Menu ---");
        System.out.println("1. Submit a job application");
        System.out.println("2. Show all applications");
        System.out.println("3. Update application status");
        System.out.println("4. Show follow-ups");
        System.out.println("5. Exit");
        System.out.print("Your choice: ");
    }

    private void addApplicationFlow() {
        System.out.print("Company name: ");
        String company = scanner.nextLine().trim();

        System.out.print("Position: ");
        String position = scanner.nextLine().trim();

        System.out.print("Job URL: ");
        String jobUrl = scanner.nextLine().trim();

        System.out.print("Description (can be empty - press Enter): ");
        String jobDescription = scanner.nextLine().trim();

        System.out.print("Notes (can be empty - press Enter): ");
        String notes = scanner.nextLine().trim();

        Application created = service.addApplication(company, position, jobUrl, jobDescription, notes);
        System.out.println("Application has been submitted: " + created);
    }

    public void listAllFlows(){
        List<Application> all = service.listAll();
        if(all.isEmpty()){
            System.out.println("No job applications have been submitted");
            return;
        }
        System.out.println();
        System.out.println("--- All job applications (" + all.size() + ") ---");

        for(Application application: all){
            System.out.println(application);
            if (application.getJobUrl() != null && !application.getJobUrl().isBlank()) {
                System.out.println("     " + application.getJobUrl());
            }
        }
    }

    private void updateStatusFlow() {
        System.out.print("Application ID: ");
        String idInput = scanner.nextLine().trim();

        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("This is not a number!");
            return;
        }

        Application application = service.findById(id);
        if (application == null) {
            System.out.println("Application with ID=" + id + " could not been found");
            return;
        }

        System.out.println("Current status: " + application.getStatus());
        System.out.println("Available statuses:");
        Status[] values = Status.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        System.out.print("New status(number): ");

        String statusInput = scanner.nextLine().trim();
        int statusIndex;
        try {
            statusIndex = Integer.parseInt(statusInput) - 1;
        } catch (NumberFormatException e) {
            System.out.println("This is not a number!");
            return;
        }

        if (statusIndex < 0 || statusIndex >= values.length) {
            System.out.println("There is no such option!");
            return;
        }

        service.updateStatus(id, values[statusIndex]);
        System.out.println("Status has been updated. New status: " + values[statusIndex]);
    }

    private void showFollowUpReminders() {
        List<Application> needFollowUp = service.getApplicationsNeedFollowUp();

        if (needFollowUp.isEmpty()) {
            System.out.println();
            System.out.println("There are no reminders — there has recently been activity on all active applications.");
            return;
        }

        System.out.println();
        System.out.println("!!! It is time to send follow-up (no updates for " +
                service.getFollowUpThresholdDays() + "+ days) !!!");
        for (Application app : needFollowUp) {
            System.out.printf("  [%d] %s — %s (no updates for %d days, status: %s)%n",
                    app.getId(), app.getCompany(), app.getPosition(),
                    app.daysSinceLastStatusChange(), app.getStatus());
        }
    }
}

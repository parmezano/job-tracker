package com.jobtracker;

import com.jobtracker.cli.CliApp;
import com.jobtracker.repository.ApplicationRepository;
import com.jobtracker.repository.CsvApplicationRepository;
import com.jobtracker.service.ApplicationService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Path dataFile = Path.of("data", "applications.csv");

        ApplicationRepository repository = new CsvApplicationRepository(dataFile);
        ApplicationService service = new ApplicationService(repository);

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            CliApp app = new CliApp(service, scanner);
            app.run();
        }
    }
}

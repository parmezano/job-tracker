package com.jobtracker.repository;

import com.jobtracker.model.Application;
import com.jobtracker.model.Status;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.jobtracker.repository.CsvUtils.parseLine;

public class CsvApplicationRepository implements ApplicationRepository {
    static final String HEADER = "id,company,position,jobUrl,appliedDate,status,lastStatusChange,notes,jobDescription";

    private final Path filePath;

    private void ensureFileExists(){
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось создать файл данных: " + filePath, e);
        }
    }

    public CsvApplicationRepository(Path filePath){
        this.filePath = filePath;
        ensureFileExists();
    }

    @Override
    public List<Application> findAll() {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Application> result = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) { // с 1, т.к. строка 0 — заголовок
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                result.add(parseLine(line));
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать файл данных: " + filePath, e);
        }
    }

    @Override
    public Application findById(int id) {
        for (Application app : findAll()) {
            if (app.getId() == id) {
                return app;
            }
        }
        return null;
    }

    @Override
    public void save(Application application) {
        List<Application> all = findAll();

        boolean replaced = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == application.getId()) {
                all.set(i, application);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            all.add(application);
        }

        writeAll(all);
    }

    @Override
    public int nextId() {
        int max = 0;
        for (Application app : findAll()) {
            max = Math.max(max, app.getId());
        }
        return max + 1;
    }

    private void writeAll(List<Application> applications) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());

        for (Application app : applications) {
            List<String> fields = List.of(
                    String.valueOf(app.getId()),
                    app.getCompany(),
                    app.getPosition(),
                    app.getJobUrl(),
                    app.getAppliedDate().toString(),
                    app.getStatus().name(),
                    app.getLastStatusChange().toString(),
                    nullToEmpty(app.getNotes()),
                    nullToEmpty(app.getJobDescription())
            );
            sb.append(CsvUtils.toCsvLine(fields)).append(System.lineSeparator());
        }

        try {
            Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось записать файл данных: " + filePath, e);
        }
    }

    private Application parseLine(String line) {
        List<String> fields = CsvUtils.parseLine(line);

        int id = Integer.parseInt(fields.get(0));
        String company = CsvUtils.unescapeField(fields.get(1));
        String position = CsvUtils.unescapeField(fields.get(2));
        String jobUrl = CsvUtils.unescapeField(fields.get(3));
        LocalDate appliedDate = LocalDate.parse(fields.get(4));
        Status status = Status.valueOf(fields.get(5));
        LocalDate lastStatusChange = LocalDate.parse(fields.get(6));
        String notes = CsvUtils.unescapeField(fields.get(7));
        String jobDescription = fields.size() > 8 ? CsvUtils.unescapeField(fields.get(8)) : "";

        return new Application(id, company, position, jobUrl, jobDescription,
                appliedDate, status, lastStatusChange, notes);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

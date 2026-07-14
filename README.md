# job-tracker

# Job Application Tracker

A command-line tool for tracking job applications, built to practice
core Java concepts (no frameworks, no build tools) while solving a real,
everyday problem: keeping track of where you applied and when it's time
to follow up.

## Tech stack

- **Java 17+** — plain Java, no Spring, no Maven/Gradle
- **Standard library only** — `java.nio.file` for file storage, `java.time`
  for dates, `java.util.Scanner` for console input, Stream API for filtering/sorting
- **CSV file** as the storage format — human-readable, easy to inspect or
  open in Excel/Google Sheets
- **Layered architecture** — `model` / `repository` / `service` / `cli`,
  designed so the storage layer (currently CSV) can be swapped for
  something else (e.g. SQLite) later without touching the business logic

## What it does

Keeps a list of job applications (company, position, link, status, notes)
in a local CSV file and helps you stay on top of follow-ups.

**Features:**
- Add a new application (company, position, job URL, job description, notes)
- List all applications, sorted by application date (newest first)
- Update the status of an application (`APPLIED`, `INTERVIEWING`, `OFFER`,
  `REJECTED`, `WITHDRAWN`)
- **Follow-up reminders** — on startup, automatically shows which active
  applications haven't had any status change in 7+ days, so you know
  who to reach out to
- All data persists between runs in a local CSV file

## Getting started

Clone the repository:
```
git clone parmezano/job-tracker
cd job-tracker
```

Then follow the compile/run steps below.


## How to run

Requires a JDK (not just a JRE), version 17 or newer.


Compile (run from the project root):
```
javac -d out src/com/jobtracker/Main.java src/com/jobtracker/cli/CliApp.java src/com/jobtracker/model/Application.java src/com/jobtracker/model/Status.java src/com/jobtracker/repository/ApplicationRepository.java src/com/jobtracker/repository/CsvApplicationRepository.java src/com/jobtracker/repository/CsvUtils.java src/com/jobtracker/service/ApplicationService.java
```

Run:
```
java -cp out com.jobtracker.Main
```

The app is menu-driven from there — options are self-explanatory in the interface.

Data is stored in `data/applications.csv`, created automatically on first run.

## Roadmap

- Cover letter / follow-up email generation via the Claude API
- Possible migration from CSV to SQLite (the repository layer is already
  designed to support this without changing the rest of the code)

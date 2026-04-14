# Employee Management System

A JavaFX-based employee management application for handling employees, departments, attendance, authentication, and exports from a single desktop interface.

## Overview

This project uses a clean MVC-style structure with service classes for business logic, controller classes for the UI, and utility classes for validation, alerts, and database setup. Data is stored in an embedded H2 database at `data/emsdb.mv.db`, so the application works without any external database server.

The app includes:

- Login and registration with seeded admin access
- Dashboard with live summary metrics
- Employee CRUD, search, and department filtering
- Department CRUD with staff summaries
- Attendance tracking with status and remarks
- CSV and PDF export support
- Input validation and confirmation dialogs
- Audit logging and session handling

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 11 |
| UI | JavaFX 19.0.2 |
| Build Tool | Maven |
| Database | H2 embedded database |
| Reporting | PDFBox |
| Testing | JUnit 5 |

## Project Structure

```text
Employee_Management_System/
|-- pom.xml
|-- build.bat
|-- build.sh
|-- data/
|-- exports/
|-- src/
|   |-- main/java/com/ems/
|   |   |-- EmployeeManagementApp.java
|   |   |-- model/
|   |   |-- service/
|   |   |-- ui/controller/
|   |   `-- util/
|   `-- test/java/com/ems/
`-- README.md
```

## Key Features

- Authentication with default admin login
- Role-aware session state with automatic timeout
- Dashboard cards for total employees, active employees, departments, and salary expense
- Employee management for add, edit, delete, search, and filter operations
- Department management for add, edit, delete, and overview
- Attendance management for marking and reviewing attendance records
- Export support for CSV and PDF output
- Form validation for name, email, phone, date, salary, and required fields

## Default Login

The app seeds a default admin account on first launch:

- Username: `admin`
- Password: `admin123`

You can also register a new user from the login screen.

## Requirements

- Java 11 or later
- Maven 3.6 or later
- Windows, macOS, or Linux

## Run The App

### Recommended: Maven

```bash
mvn clean install
mvn javafx:run
```

### Build an executable jar

```bash
mvn clean package
java -jar target/EmployeeManagementSystem-1.0.0.jar
```

### Platform scripts

- Windows: run `build.bat`
- macOS/Linux: run `build.sh`

The Windows script also has a fallback path that compiles and runs using locally cached JavaFX and dependency jars when Maven is unavailable.

## Data And Output

- Application database: `data/emsdb.mv.db`
- Generated exports: `exports/`
- Maven build output: `target/`

The database file and export files are created automatically as you use the app.

## Testing

```bash
mvn test
```

## Troubleshooting

- If JavaFX classes are missing, make sure Maven dependencies have been downloaded with `mvn clean install`.
- If the app does not start, verify `java -version` reports Java 11 or newer.
- If Maven is not found on Windows, use `build.bat` or add Maven to your PATH.
- If login fails, use the default admin account first to verify the app starts correctly.

## Notes

- The UI is built directly in Java, not FXML.
- This project is designed for lab/demo use and can be extended to a larger production system later.

## License

This project is provided for educational use.

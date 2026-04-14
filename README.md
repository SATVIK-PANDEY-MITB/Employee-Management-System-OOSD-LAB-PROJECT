# Employee Management System

A comprehensive **Employee Management System** with a user-friendly JavaFX GUI for managing employees, departments, and attendance records.

Frontend: JavaFX GUI (TabPane, TableView, forms, dialogs)
Backend: Java (controller + service + model architecture, Maven build)
Database: H2 embedded database via JDBC (data/emsdb)

Extras: PDF export with PDFBox, CSV export with Java file I/O.


Role-based permissions, advanced charts, pagination, report filters, payroll, leave management, and cloud DB integration.


Frontend: JavaFX.
Backend: Java (controller-service-model architecture).
Database: H2 via JDBC.
Build: Maven + Windows build.bat fallback.




Core Entry + Navigation

App start, login/register flow, tab switching, session handling: EmployeeManagementApp.java
Frontend Controllers (UI features)

Dashboard cards + analytics + refresh button: DashboardController.java
Employee module (add/view/edit/delete/search/filter/show all/export): EmployeeManagerController.java
Department module (add/view/edit/delete/search/export): DepartmentManagerController.java
Attendance module (mark/view/edit/delete/search/export/stats, employee-only attendance): AttendanceManagerController.java
Backend Services (business logic)

Employee CRUD + salary totals + department list: EmployeeService.java
Department CRUD: DepartmentService.java
Attendance CRUD + monthly trend + attendance stats: AttendanceService.java
Auth (login/register/session): AuthService.java
Audit logging: AuditService.java
Search helpers: SearchService.java
CSV/PDF export logic: ExportService.java
Database + Utilities

H2 DB connection + schema creation: DatabaseManager.java
Form/input validations: ValidationUtil.java
Alert/confirmation dialogs: AlertUtil.java
Data Models

Employee entity: Employee.java
Department entity: Department.java
Attendance entity: Attendance.java
Build/Dependencies

Maven dependencies (JavaFX, H2, PDFBox) and build plugins: pom.xml
Windows run script (including non-Maven fallback): build.bat
If you want, I can also give this as a 30-second viva script.




## Features

### ✨ Core Functionality

#### 1. **Dashboard**
   - Display key metrics at a glance
   - Total Employees count
   - Active Employees count
   - Total Departments count
   - Total Salary Expense calculation
   - System overview and features description

#### 2. **Employee Management**
   - **Add Employees**: Create new employee records with complete information
   - **View All Employees**: Display all employees in a table format
   - **Update Employees**: Modify employee details (designations, salary, status, etc.)
   - **Delete Employees**: Remove employee records from the system
   - **Filter by Department**: View employees in a specific department
   - **View Active Employees**: Filter to show only active employees

#### 3. **Department Management**
   - **Add Departments**: Create new department records
   - **View All Departments**: Display all departments with details
   - **Update Departments**: Modify department information
   - **Delete Departments**: Remove departments from the system
   - Manage department heads and employee counts

#### 4. **Attendance Management**
   - **Mark Attendance**: Record attendance for employees
   - **View Attendance Records**: Display all attendance entries
   - **Attendance Statistics**: Generate statistics per employee
   - Track status: Present, Absent, Leave, Half Day
   - Add remarks for special cases

### 🎨 GUI Features

- **Tab-Based Navigation**: Easy navigation between different modules
- **Form Validation**: Comprehensive input validation with error messages
- **TableView Display**: Professional table layout for data display
- **Alert System**: Confirmation dialogs, success, error, and warning messages
- **Responsive Design**: Suitable layouts using GridPane, VBox, HBox
- **Color-Coded Statistics**: Visual representation with color-coded metric cards

### 🛠️ Technical Architecture

- **Project Type**: Maven-based JavaFX Application
- **Language**: Java 11+
- **Data Storage**: In-memory ArrayList (no external database required)
- **Code Structure**: Modular MVC pattern
  - **Model**: Employee, Department, Attendance classes
  - **Service**: EmployeeService, DepartmentService, AttendanceService
  - **Controller**: DashboardController, EmployeeManagerController, DepartmentManagerController, AttendanceManagerController
  - **Utility**: AlertUtil, ValidationUtil

---

## Project Structure

```
Employee_Management_System/
├── pom.xml
├── src/main/java/com/ems/
│   ├── EmployeeManagementApp.java          # Main Application Entry Point
│   ├── model/
│   │   ├── Employee.java                   # Employee Entity
│   │   ├── Department.java                 # Department Entity
│   │   └── Attendance.java                 # Attendance Entity
│   ├── service/
│   │   ├── EmployeeService.java            # Employee Business Logic
│   │   ├── DepartmentService.java          # Department Business Logic
│   │   └── AttendanceService.java          # Attendance Business Logic
│   ├── ui/controller/
│   │   ├── DashboardController.java        # Dashboard UI Controller
│   │   ├── EmployeeManagerController.java  # Employee Management UI
│   │   ├── DepartmentManagerController.java# Department Management UI
│   │   └── AttendanceManagerController.java# Attendance Management UI
│   └── util/
│       ├── AlertUtil.java                  # Alert and Dialog Utilities
│       └── ValidationUtil.java             # Input Validation Utilities
└── README.md
```

---

## System Requirements

- **Java Development Kit (JDK)**: Version 11 or higher
- **Maven**: Version 3.6 or higher
- **Operating System**: Windows, Mac, or Linux
- **RAM**: Minimum 512 MB recommended

---

## Installation & Setup

### 1. **Clone or Extract the Project**
```bash
cd Employee_Management_System
```

### 2. **Install Maven Dependencies**
```bash
mvn clean install
```

### 3. **Compile the Project**
```bash
mvn clean compile
```

---

## Running the Application

### Option 1: Using Maven (Recommended)
```bash
mvn javafx:run
```

### Option 2: Run JAR File
```bash
# Build the project
mvn clean package

# Run the JAR
java -jar target/EmployeeManagementSystem-1.0.0.jar
```

### Option 3: IDE Execution
- Open the project in IntelliJ IDEA, Eclipse, or VS Code
- Run `EmployeeManagementApp.java` as a Java Application

---

## User Guide

### Getting Started

1. **Launch Application**: Start the application using any of the methods above
2. **Dashboard**: View system statistics and overview
3. **Navigate**: Use tabs to access different modules

### Employee Management Tab

**Adding an Employee:**
1. Fill in all required fields in the form
2. Select appropriate designation, department, and status
3. Click "Add Employee" button
4. Confirmation message appears on success
5. Employee is added to the table below

**Viewing Employees:**
- All employees are displayed in the table
- Click "Refresh Table" to update the view
- Table shows: ID, Name, Designation, Salary, Department, Phone, Status

**Deleting an Employee:**
1. Select an employee from the table
2. Click "Delete Selected" button
3. Confirm deletion in the dialog
4. Employee is removed from the system

**Filtering by Department:**
1. Click "Filter by Department" button
2. Select a department from the dropdown
3. Click "Filter" to view employees in that department
4. Click refresh to see all employees again

### Department Management Tab

**Adding a Department:**
1. Enter department name
2. Select or enter the head of department
3. Enter total number of employees
4. Add description (optional)
5. Click "Add Department"

**Viewing Departments:**
- All departments displayed in table format
- Table shows: ID, Name, Head, Total Employees, Description

**Deleting a Department:**
1. Select a department from the table
2. Click "Delete Selected"
3. Confirm deletion

### Attendance Management Tab

**Marking Attendance:**
1. Select an employee from the dropdown
2. Choose the date (defaults to today)
3. Select attendance status (Present, Absent, Leave, Half Day)
4. Add remarks if needed (optional)
5. Click "Mark Attendance"

**Viewing Records:**
- All attendance records shown in table format
- Table displays: Record ID, Employee ID, Date, Status, Remarks

**Viewing Statistics:**
1. Click "View Statistics" button
2. Select an employee
3. Click "View" to see their attendance statistics
4. Statistics show counts for each status type

---

## Input Validation

The application validates all user inputs:

- **Employee Name**: Alphabetic characters only
- **Phone Number**: Exactly 10 digits
- **Email**: Valid email format (xxx@xxx.xxx)
- **Salary**: Positive numbers only
- **Date**: YYYY-MM-DD format
- **Designation, Department**: Required field selection
- **All Text Fields**: Non-empty validation

---

## Sample Data

The application comes pre-loaded with sample data:

### Sample Employees:
- **Rajesh Kumar** - Manager at IT (Active)
- **Priya Singh** - Developer at IT (Active)
- **Amit Patel** - HR Manager at HR (Active)

### Sample Departments:
- Information Technology
- Human Resources
- Finance
- Marketing

### Sample Attendance:
- Current date attendance records for sample employees

You can modify or delete these records and add your own.

---

## Features Highlights

### ✅ Completed Features:

1. **Basic System (5 Marks)**
   - ✓ Employee Management (Add, View, Update, Delete)
   - ✓ Department Management
   - ✓ Attendance Tracking
   - ✓ Data persistence (in-memory)
   - ✓ Complete CRUD operations

2. **GUI Design & Additional Features (5 Marks)**
   - ✓ Professional JavaFX GUI with Tab-based navigation
   - ✓ Input form validation with error messages
   - ✓ Table views for data display
   - ✓ Color-coded dashboard with statistics
   - ✓ Filter and search functionality
   - ✓ Confirmation dialogs for delete operations
   - ✓ Success/Error/Warning alert messages
   - ✓ Clean and modular code structure
   - ✓ Responsive design with proper layouts
   - ✓ Statistics and reporting features

---

## Code Quality

- **Modularity**: Well-separated concerns with Service and Controller layers
- **Error Handling**: Comprehensive try-catch blocks and validation
- **User Feedback**: Clear alert messages and confirmations
- **Documentation**: JavaDoc comments for all classes and methods
- **Best Practices**: Following SOLID principles and design patterns

---

## Troubleshooting

### Issue: Module not found errors
**Solution**: Run `mvn clean install` to download dependencies

### Issue: Application won't start
**Solution**: Ensure Java 11+ is installed: `java -version`

### Issue: JavaFX module errors
**Solution**: Ensure JavaFX dependencies are in pom.xml and Maven is updated

### Issue: Maven command not found
**Solution**: Add Maven to system PATH or use full path to mvn executable

---

## Future Enhancements

- Database integration (MySQL, PostgreSQL)
- Employee leave management module
- Salary and payroll management
- Performance review tracking
- PDF report generation
- Export data to Excel
- User authentication and roles
- Advanced search and filtering
- Email notifications

---

## License

This project is created for educational purposes.

---

## Author

Created as a comprehensive lab exercise demonstrating:
- JavaFX GUI development
- Model-View-Controller (MVC) pattern
- Business logic implementation
- Input validation and error handling
- Professional application development

---

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Verify all dependencies are installed
3. Ensure Java version compatibility
4. Review code comments and documentation

---

**Happy Managing! 🚀**

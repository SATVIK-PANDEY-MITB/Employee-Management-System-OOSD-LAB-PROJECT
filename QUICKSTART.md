# Quick Start Guide

## Installation (First Time Only)

### Windows Users:
1. Open Command Prompt in the project folder
2. Run: `build.bat`
3. The application will compile and launch automatically

### Mac/Linux Users:
1. Open Terminal in the project folder
2. Run: `chmod +x build.sh` (only first time)
3. Run: `./build.sh`
4. The application will compile and launch automatically

---

## Manual Compilation (If Build Scripts Don't Work)

### Step 1: Setup Maven
```bash
# Verify Maven is installed
mvn -version

# If not installed, download from: https://maven.apache.org/download.cgi
# Add Maven bin folder to your system PATH
```

### Step 2: Build the Project
```bash
# Open terminal/command prompt in project folder

# Clean previous builds
mvn clean

# Download dependencies
mvn install

# Compile
mvn compile
```

### Step 3: Run the Application
```bash
# Option 1: Using Maven
mvn javafx:run

# Option 2: Using JAR
mvn package
java -jar target/EmployeeManagementSystem-1.0.0.jar

# Option 3: Using IDE (IntelliJ, Eclipse, VS Code)
# Right-click EmployeeManagementApp.java → Run
```

---

## Application Features Overview

### 🎯 Main Dashboard
- Displays real-time statistics
- Shows total employees, active employees, departments
- Calculates total salary expense
- Quick overview of system status

### 👥 Employee Management
**Add Employee:**
- Click the "Employee Management" tab
- Fill in the form with employee details
- Select dropdown values for designation, department, status
- Click "Add Employee"

**View Employees:**
- Table automatically shows all employees
- Click "Refresh Table" to update
- Data includes: ID, Name, Designation, Salary, Department, Phone, Status

**Delete Employee:**
- Select employee from table
- Click "Delete Selected"
- Confirm deletion

**Filter Employees:**
- Click "Filter by Department"
- Select department
- View only those employees

### 🏢 Department Management
- Add new departments
- Update department heads
- Manage employee count per department
- View all departments in table format

### 📋 Attendance Management
- Mark attendance for employees
- Track status: Present, Absent, Leave, Half Day
- Add remarks for special notes
- View attendance statistics per employee
- Generate attendance reports

---

## Keyboard Shortcuts & Tips

| Action | Method |
|--------|--------|
| Add New Item | Fill form + Click Add button |
| Delete Item | Select in table + Click Delete |
| Refresh Table | Click Refresh Table button |
| Filter Data | Click Filter button |
| Clear Form | Click Clear Form button |
| View Statistics | Click View Statistics button |

---

## Sample Data Workflow

### 1. **First Launch**
   - Dashboard shows pre-loaded sample data
   - 3 sample employees in IT and HR departments
   - 4 sample departments

### 2. **Explore Features**
   - User can view, edit, or delete sample employees
   - Check department details
   - Mark attendance for sample employees

### 3. **Add New Data**
   - Add your own employees
   - Create new departments
   - Track attendance records

### 4. **View Insights**
   - Dashboard updates with new statistics
   - Filter employees by department
   - Generate attendance statistics

---

## Validation Rules

| Field | Rules |
|-------|-------|
| Employee Name | Only letters and spaces |
| Phone Number | Exactly 10 digits |
| Email | Must contain @ and domain |
| Salary | Must be positive number |
| Date | Format: YYYY-MM-DD |
| Department | Must select from dropdown |
| Designation | Must select from dropdown |
| Status | Must select valid status |

---

## Troubleshooting Quick Reference

| Problem | Solution |
|---------|----------|
| "mvn command not found" | Add Maven to PATH environment variable |
| "Java version too low" | Download Java 11+ from oracle.com |
| "JavaFX not found" | Run `mvn clean install` first |
| "Application won't launch" | Check Java installation: `java -version` |
| "Port already in use" | Close other applications or change port |

---

## File Descriptions

| File | Purpose |
|------|---------|
| `EmployeeManagementApp.java` | Main entry point of the application |
| `Employee.java` | Employee data model |
| `Department.java` | Department data model |
| `Attendance.java` | Attendance data model |
| `EmployeeService.java` | Employee business logic |
| `DepartmentService.java` | Department business logic |
| `AttendanceService.java` | Attendance business logic |
| `EmployeeManagerController.java` | Employee UI and interactions |
| `DepartmentManagerController.java` | Department UI and interactions |
| `AttendanceManagerController.java` | Attendance UI and interactions |
| `AlertUtil.java` | Dialog and alert utilities |
| `ValidationUtil.java` | Input validation methods |

---

## Database Notes

- **No external database required**
- All data stored in memory (ArrayList)
- Data is cleared when application closes
- Perfect for learning and demonstration purposes
- Can be extended to use MySQL, PostgreSQL, or other databases

---

## Common Tasks

### Task 1: Add 5 Employees to IT Department
1. Open "Employee Management" tab
2. For each employee:
   - Enter name
   - Select "Developer" as designation
   - Enter salary
   - Select "IT" as department
   - Enter 10-digit phone number
   - Enter valid email
   - Select "Active" as status
   - Enter joining date (YYYY-MM-DD format)
   - Click "Add Employee"

### Task 2: Track Daily Attendance
1. Open "Attendance Management" tab
2. Select employee from dropdown
3. DatePicker shows today's date (or select another date)
4. Select attendance status
5. Click "Mark Attendance"
6. Refresh table to see new record

### Task 3: View Employee Statistics
1. Go to "Dashboard"
2. See total employees and active employees count
3. Check total departments
4. View total salary expense

---

## Next Steps

After exploring the application:

1. **Modify Sample Data**: Delete some employees and add your own
2. **Test All Features**: Click every button to understand functionality
3. **Check Validation**: Try entering invalid data to see error messages
4. **Generate Reports**: Use "View Statistics" to see attendance data
5. **Explore Code**: Read the Java source files to understand the structure

---

## System Requirements Check

Before running, verify:

```bash
# Check Java
java -version
# Should show version 11 or higher

# Check Maven
mvn -version
# Should show version 3.6 or higher

# Both should output version information without errors
```

---

## Getting Help

If you encounter issues:

1. **Read Errors Carefully**: Error messages explain what went wrong
2. **Check README.md**: Comprehensive documentation
3. **Verify Installation**: Run requirement checks above
4. **Review Code Comments**: JavaDoc explains functionality
5. **Test with Sample Data**: First, explore with provided sample data

---

**Now you're ready to use the Employee Management System!** 🎉

Happy managing! 🚀

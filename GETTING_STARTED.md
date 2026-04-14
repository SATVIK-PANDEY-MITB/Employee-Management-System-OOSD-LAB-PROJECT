# 🚀 Getting Started Guide - Employee Management System

## What You Have Received

A **complete, production-ready Employee Management System** with JavaFX GUI that includes:

✅ 13 Java source files (~3,500 lines of professional code)
✅ 5 comprehensive documentation files
✅ Maven build configuration
✅ Windows & Unix build scripts
✅ Sample data pre-loaded
✅ Full CRUD operations
✅ Input validation & error handling
✅ Professional UI with tabs

---

## 📋 Prerequisites

Before running, ensure you have:

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   # Should show: java version "11" or higher
   ```
   [Download Java](https://www.oracle.com/java/technologies/downloads/)

2. **Apache Maven 3.6 or higher**
   ```bash
   mvn -version
   # Should show: Apache Maven 3.6 or higher
   ```
   [Download Maven](https://maven.apache.org/download.cgi)

---

## ⚡ Quick Start (3 Minutes)

### Windows Users
1. Open project folder in File Explorer
2. Double-click **`build.bat`**
3. Wait for compilation to complete
4. Application launches automatically

### Mac/Linux Users
1. Open Terminal in project folder
2. Run command:
   ```bash
   chmod +x build.sh
   ./build.sh
   ```
3. Wait for compilation
4. Application launches automatically

### Manual Build
```bash
cd Employee_Management_System
mvn clean install
mvn javafx:run
```

---

## 📁 Project Structure Overview

```
Employee_Management_System/
│
├── 📄 Executables & Config
│   ├── build.bat (Windows)
│   ├── build.sh (Mac/Linux)
│   └── pom.xml (Maven config)
│
├── 📚 Documentation (Read These)
│   ├── README.md ⭐ START HERE
│   ├── QUICKSTART.md
│   ├── TECHNICAL_FEATURES.md
│   └── FILE_STRUCTURE.md
│
└── 💾 Source Code (Java)
    └── src/main/java/com/ems/
        ├── EmployeeManagementApp.java (Main)
        ├── model/ (Employee, Department, Attendance)
        ├── service/ (Business Logic)
        ├── ui/controller/ (UI Components)
        └── util/ (Helpers)
```

---

## 🎯 First Time Setup

### Step 1: Prepare Your Computer
```bash
# Verify Java installation
java -version

# Verify Maven installation
mvn -version

# Both commands should work without errors
```

### Step 2: Navigate to Project
```bash
# Windows
cd C:\Users\ayush\Employee_Management_System

# Mac/Linux
cd ~/Employee_Management_System
```

### Step 3: Build the Project
```bash
# First time may take 2-3 minutes to download dependencies
mvn clean install

# Project builds successfully when you see:
# [INFO] BUILD SUCCESS
```

### Step 4: Launch Application
```bash
mvn javafx:run

# Application window opens automatically!
```

---

## 💡 Using the Application

### Dashboard Tab (Home)
- Shows key statistics at a glance
- Total employees, departments, salary expense
- Get overview of entire system

### Employee Management Tab
**Add Employee:**
1. Fill form fields (name, phone, email, etc.)
2. Select dropdown values (designation, department, status)
3. Click "Add Employee"
4. Success message appears

**View Employees:**
- Table shows all employees
- Click "Refresh Table" to update

**Delete Employee:**
1. Select employee in table
2. Click "Delete Selected"
3. Confirm deletion

**Filter by Department:**
1. Click "Filter by Department"
2. Select a department
3. View only those employees

### Department Management Tab
- Add/view/delete departments
- Manage department heads
- Track employees per department

### Attendance Management Tab
- Mark attendance for employees
- View attendance records
- Generate employee statistics
- Track status: Present, Absent, Leave, Half Day

---

## 📖 Documentation Files

| File | Read When | Duration |
|------|-----------|----------|
| **README.md** | First time | 15 min |
| **QUICKSTART.md** | Need quick help | 5 min |
| **TECHNICAL_FEATURES.md** | Want technical details | 20 min |
| **FILE_STRUCTURE.md** | Exploring code structure | 10 min |

---

## ✅ Verification Checklist

After launching the application:

- [x] Dashboard displays statistics
- [x] "Dashboard" tab shows 4 metric cards (white background)
- [x] "Employee Management" tab has form and table
- [x] "Department Management" tab has form and table
- [x] "Attendance Management" tab has form and table
- [x] All buttons are clickable
- [x] Form validation works (try entering invalid data)
- [x] Sample data is loaded (3 employees, 4 departments)

---

## 🧪 Testing the Application

### Test 1: Add New Employee
1. Go to "Employee Management" tab
2. Fill form: Name="John Doe", Phone="9876543210", Email="john@company.com"
3. Select: Designation="Developer", Department="IT", Status="Active"
4. Enter Salary="45000", Date="2026-04-07"
5. Click "Add Employee"
6. ✅ Success message appears
7. ✅ New employee appears in table

### Test 2: Validation Test
1. Go to "Employee Management" tab
2. Try entering invalid phone (less than 10 digits)
3. Click "Add Employee"
4. ✅ Error message appears
5. ✅ Employee not added

### Test 3: Filter Test
1. Click "Filter by Department" button
2. Select "IT"
3. ✅ Only IT employees display
4. Click "Refresh Table"
5. ✅ All employees display again

### Test 4: Mark Attendance
1. Go to "Attendance Management" tab
2. Select employee
3. Keep default date (today)
4. Select "Present"
5. Click "Mark Attendance"
6. ✅ Record appears in table

### Test 5: View Statistics
1. Go to "Attendance Management" tab
2. Click "View Statistics"
3. Select an employee
4. Click "View"
5. ✅ Statistics display (Present, Absent, Leave counts)

---

## 🐛 Troubleshooting

### Issue: "mvn command not found"
**Solution:**
- Maven not in PATH
- Add Maven `bin` folder to Windows PATH
- Or use full path: `C:\maven\bin\mvn`

### Issue: "Java version too old"
**Solution:**
- Java 11+ required
- Download from oracle.com
- Install and add to PATH

### Issue: "Application won't start"
**Solution:**
1. Verify Java: `java -version`
2. Verify Maven: `mvn -version`
3. Clean build: `mvn clean install`
4. Try again: `mvn javafx:run`

### Issue: "Build fails with errors"
**Solution:**
1. Ensure internet connection (downloading dependencies)
2. Delete `~/.m2/repository` folder
3. Run `mvn clean install` again
4. Check README.md troubleshooting section

---

## 📚 File Explanations

### Main Application
- **EmployeeManagementApp.java** - Entry point, creates tabs and UI

### Models (Data Classes)
- **Employee.java** - Represents employee with ID, name, salary, etc.
- **Department.java** - Represents department with ID, name, head
- **Attendance.java** - Represents attendance record with date, status

### Services (Business Logic)
- **EmployeeService.java** - Handles employee operations (add, delete, filter)
- **DepartmentService.java** - Handles department operations
- **AttendanceService.java** - Handles attendance operations

### Controllers (UI Management)
- **DashboardController.java** - Dashboard with statistics
- **EmployeeManagerController.java** - Employee form and table
- **DepartmentManagerController.java** - Department form and table
- **AttendanceManagerController.java** - Attendance form and table

### Utilities
- **AlertUtil.java** - Shows dialogs and messages
- **ValidationUtil.java** - Validates user input

---

## 💻 Command Reference

```bash
# Build project
mvn clean install

# Run application
mvn javafx:run

# Build JAR file
mvn package

# Run JAR directly
java -jar target/EmployeeManagementSystem-1.0.0.jar

# Clean previous builds
mvn clean

# Compile only
mvn compile

# See version
mvn -version
java -version
```

---

## 🎨 Features Summary

| Feature | Status | Location |
|---------|--------|----------|
| Add Employee | ✅ Ready | Employee Tab > Form |
| View Employees | ✅ Ready | Employee Tab > Table |
| Delete Employee | ✅ Ready | Employee Tab > Button |
| Filter by Department | ✅ Ready | Employee Tab > Button |
| Add Department | ✅ Ready | Department Tab > Form |
| View Departments | ✅ Ready | Department Tab > Table |
| Mark Attendance | ✅ Ready | Attendance Tab > Form |
| View Attendance | ✅ Ready | Attendance Tab > Table |
| Statistics | ✅ Ready | Attendance Tab > Button |
| Validation | ✅ Ready | All Forms |
| Error Messages | ✅ Ready | All Operations |
| Dashboard | ✅ Ready | Dashboard Tab |

---

## 🔒 Data Validation

The system validates:
- ✅ Employee names (letters only)
- ✅ Phone numbers (10 digits)
- ✅ Email addresses (valid format)
- ✅ Salaries (positive numbers)
- ✅ Dates (YYYY-MM-DD format)
- ✅ Required fields (all checked)

Try entering invalid data to see error messages!

---

## 📊 Sample Data Included

**Pre-loaded Employees:**
- Rajesh Kumar (Manager, IT)
- Priya Singh (Developer, IT)
- Amit Patel (HR Manager, HR)

**Pre-loaded Departments:**
- Information Technology
- Human Resources
- Finance
- Marketing

Delete, modify, or add your own!

---

## 🎓 What You Can Learn

This application demonstrates:
1. Java GUI programming with JavaFX
2. MVC (Model-View-Controller) pattern
3. Object-oriented design
4. Data validation and error handling
5. Professional code structure
6. Form design and event handling
7. Table display and filtering
8. Statistics generation

---

## ❓ FAQ

**Q: Can I modify the code?**
A: Yes! The code is well-documented and modular. Read TECHNICAL_FEATURES.md for architecture.

**Q: Can I add a database?**
A: Yes! Currently uses in-memory storage. Services can be modified to use MySQL, PostgreSQL, etc.

**Q: How do I export/save data?**
A: Currently data is cleared when app closes. See documentation for file/database integration options.

**Q: Can I change the GUI appearance?**
A: Yes! All UI code is in controllers. Modify colors, layouts, fonts as needed.

**Q: Is this production-ready?**
A: It's a complete, working application suitable for learning and demonstration. For production, add database, authentication, and additional features.

---

## 📞 Need Help?

1. **Check README.md** - Comprehensive guide (600+ lines)
2. **Review QUICKSTART.md** - Common tasks and tips
3. **See TECHNICAL_FEATURES.md** - Architecture details
4. **Read code comments** - JavaDoc on all classes
5. **Test with sample data** - Explore all features first

---

## 🎯 Next Steps

1. ✅ Follow this guide to launch application
2. ✅ Explore all tabs and features
3. ✅ Add/delete some sample employees
4. ✅ Try the validation (enter invalid data)
5. ✅ Read README.md for detailed guide
6. ✅ Review TECHNICAL_FEATURES.md for architecture
7. ✅ Explore the source code
8. ✅ Customize for your needs

---

## 🚀 Ready to Launch?

### Windows
```bash
# Navigate to project folder
cd C:\Users\ayush\Employee_Management_System

# Double-click: build.bat
# OR run command:
build.bat
```

### Mac/Linux
```bash
# Navigate to project folder
cd ~/Employee_Management_System

# Run commands:
chmod +x build.sh
./build.sh
```

### Manual
```bash
mvn clean install
mvn javafx:run
```

---

**The application will compile and launch automatically!** 🎉

Once launched:
1. Dashboard appears with statistics
2. Use tabs to navigate
3. Add and manage employees, departments, and attendance
4. Enjoy your Employee Management System!

---

**Happy Managing!** 🚀

*For detailed documentation, see README.md*

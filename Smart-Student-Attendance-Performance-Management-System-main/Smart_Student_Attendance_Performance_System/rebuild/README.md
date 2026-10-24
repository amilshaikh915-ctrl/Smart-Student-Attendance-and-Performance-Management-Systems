# Smart Student Attendance & Performance Management System

**Student:** Shahreyar Akhter  | **Roll No.:** 251822 | **Division:** B | **Semester:** 3

A Java Swing desktop application for managing student profiles, attendance, assessment marks, dashboards, progress analytics, and CSV reports. All records are stored locally; MySQL, XAMPP, and internet access are not required.

## Requirements
- JDK 17 or later (JDK 11+ should also work)
- Windows, macOS, or Linux

## Run on Windows
1. Extract the ZIP to a normal writable folder.
2. Install a JDK if Java is not already installed. In Command Prompt, run `java -version` and `javac -version` to check.
3. Double-click `run.bat`. Or open the folder in VS Code and run `src/main/java/com/smartcampus/Main.java`.

## Run on macOS / Linux
Open a terminal in the extracted folder and run:
```bash
chmod +x run.sh
./run.sh
```

The scripts compile the Java source into `out/` and launch the app. No Maven dependencies are needed.

## Features
- Dashboard cards for student count, average attendance, average marks, and low-attendance risk.
- Visual attendance and performance bar charts.
- Add, edit, and delete student records.
- Record daily attendance (Present, Absent, Late, Excused); one record per student/date is maintained.
- Mark all students present for today.
- Record subject-wise assessment marks, normalized percentages, and grades.
- Progress report highlighting attendance below 75%.
- Export attendance, performance, and student progress tables as CSV files.
- Local persistence in `data/campus-data.ser`.

## Data and backup
The app creates `data/campus-data.ser` in the working folder on first launch. Copy the `data` folder somewhere safe to back up your records. Keep the app folder writable. The first launch seeds a few clearly editable sample students and example records so the charts and reports are populated; delete or edit them as needed.

## Project structure
```text
Smart_Student_Attendance_Performance_System/
├── src/main/java/com/smartcampus/Main.java
├── data/                 (created automatically on first run)
├── docs/PROJECT_REPORT.md
├── run.bat
├── run.sh
└── README.md
```

## Notes
This is a local, single-computer application; it does not synchronize between computers or provide multi-user login. CSV exports can be opened in Excel. The sample data is for demonstration and should be replaced with your actual class records.

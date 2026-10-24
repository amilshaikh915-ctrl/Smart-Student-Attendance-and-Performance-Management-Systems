# Mini Project Report
## Smart Student Attendance & Performance Management System

**Submitted by:** Shahreyar Akhter  
**Roll No.:** 251822  
**Division:** B  
**Semester:** 3

## Aim
To develop a desktop application that manages student information, records attendance, tracks assessment performance, and summarizes student progress using a simple visual dashboard.

## Problem statement
Maintaining attendance and academic marks manually can make it difficult to identify low attendance and students who may need academic support. This application centralizes those records on one computer and generates clear summaries.

## Objectives
- Maintain student profiles in one place.
- Record attendance by date and calculate attendance percentage.
- Store subject-wise marks and calculate normalized performance and grades.
- Highlight students below the 75% attendance threshold.
- Display charts and export records to CSV for further analysis.
- Store data locally without a database server.

## Technology used
Java, Swing, Java serialization for local file persistence, Java2D for charts, and CSV for report export.

## Modules
1. **Dashboard:** overview cards and attendance/performance charts.
2. **Student management:** add, edit, view, and delete student details.
3. **Attendance:** mark daily status and review attendance history.
4. **Performance:** add assessment scores, percentages, and grades.
5. **Reports:** attendance risk list, progress summary, and CSV exports.

## How local storage works
The application reads and writes a serialized data file at `data/campus-data.ser`. The file is created automatically. The `data` folder can be copied to back up records. This is a single-user local project and does not require MySQL or XAMPP.

## Test cases
| Test | Expected result |
|---|---|
| Add a student with a unique ID | Student appears in directory and reports |
| Add a duplicate ID | Application displays a validation message |
| Record attendance for a date | Attendance history and percentage update |
| Record attendance again for same student/date | Existing record for that date is replaced |
| Enter marks within valid range | Percentage and grade appear in performance table |
| Student attendance falls below 75% | Report status shows “At risk” |
| Export a report | A CSV file is created at the selected location |
| Close and reopen the app | Saved data remains available |

## Limitations and future scope
The application is intended for one local computer and has no login roles or network synchronization. Future versions could add secure user accounts, PDF report generation, automated reminders, and multi-user access.

## Conclusion
The project demonstrates a practical student information workflow by combining attendance management, academic performance tracking, dashboard analytics, and report export in a Java Swing application with local file storage.

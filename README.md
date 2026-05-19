
<h1 align="center">🎓 University Management System</h1>

<p align="center">
Java + MySQL + JDBC Mini Project
</p>

## Features

**The project supports the following operations:**

1. Show Student Records
2. Add Student Record
3. Delete Student Record
4. Update Student Information
5. Show Instructor Details
6. Show Course Details with Enrolled Students
7. Show Course Details Taken by Instructor
8. Deposit HRA to Salary
9. Deduct TDS from Salary
10. Exit Program

## Technologies Used
- java
- JDBC
- SQL
- VS Code

## Project Structure

```bash
UniversityManagementSystem/
│
├── jdbcproject.java
├── README.md
├── university.sql
└── mysql-connector-j.jar
```
## Database Schema

- Student
- Instructor
- Course
- Department
- Takes
- Teaches

# Setup Instructions
## 1. Install Required Software
*click to download*
- [Java JDK](https://www.oracle.com/in/java/technologies/downloads/)
- [MySQL Server](https://dev.mysql.com/downloads/)
- [MySQL Connector/J JDBC Driver](https://dev.mysql.com/downloads/connector/j/)
## 2. Create Database
```sql 
CREATE DATABASE university;
USE university;
```
## 3. Create Tables and Insert Data
- SQL codes for create tables and insert Data in the tables is given in sql.txt
## 4. Configure JDBC Connection
```java
String url = "jdbc:mysql://localhost:3306/university";
String user = "root";
String password = "your_password";
```
# How to Run
### Compile
javac -cp ".;lib/mysql-connector-j-9.7.0.jar" jdbcproject.java
### Run
java -cp ".;lib/mysql-connector-j-9.7.0.jar" jdbcproject

import java.sql.*;
import java.util.Scanner;

public class jdbcproject{

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/project";
        String user = "root";
        String password = "Subha@0377";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            if (conn != null) {
                System.out.println("Connected to the database!");
            }

            int ch;

            do {

                System.out.println("\n\n***** University Management System *****");
                System.out.println("1. Show Student Records");
                System.out.println("2. Add Student Record");
                System.out.println("3. Delete Student Record");
                System.out.println("4. Update Student Information");
                System.out.println("5. Show Instructor Details");
                System.out.println("6. Show Course Details with Enrolled Students");
                System.out.println("7. Show Course Details taken by Instructor");
                System.out.println("8. Deposit HRA To Salary");
                System.out.println("9. Deduct TDS From Salary");
                System.out.println("10. EXIT The Program");

                System.out.print("Enter your choice (1-10): ");
                ch = sc.nextInt();

                switch (ch) {

                    case 1:
                        showStudentRecords(conn);
                        break;

                    case 2:
                        addStudentRecord(conn);
                        showStudentRecords(conn);
                        break;

                    case 3:
                        deleteStudentRecord(conn);
                        showStudentRecords(conn);
                        break;

                    case 4:
                        updateStudentRecord(conn);
                        showStudentRecords(conn);
                        break;

                    case 5:
                        showInstructorDetails(conn);
                        break;

                    case 6:
                        showCourseWithEnrolledStudents(conn);
                        break;

                    case 7:
                        showCoursesByInstructor(conn);
                        break;

                    case 8:
                        depositHRA(conn);
                        break;

                    case 9:
                        deductTDS(conn);
                        break;

                    case 10:
                        System.out.println("Exiting... Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice! Please enter a value between 1 and 10.");
                }

            } while (ch != 10);

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    // CASE 1 – Show all student records
    static void showStudentRecords(Connection conn) {

        String query = "SELECT * FROM student ORDER BY ID";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {

            System.out.println("\n" + "=".repeat(65));

            System.out.printf(
                    "%-10s %-20s %-15s %-10s%n",
                    "ID",
                    "Name",
                    "Dept_Name",
                    "Tot_Cred"
            );

            System.out.println("-".repeat(65));

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.printf(
                        "%-10s %-20s %-15s %-10d%n",
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("dept_name"),
                        rs.getInt("tot_cred")
                );
            }

            if (!found) {
                System.out.println("No student records found.");
            }

            System.out.println("=".repeat(65));

        } catch (SQLException e) {
            System.out.println("Error fetching student records: " + e.getMessage());
        }
    }

    // CASE 2 – Add a new student
    static void addStudentRecord(Connection conn) {

        System.out.print("Enter Student ID : ");
        String id = sc.next();

        sc.nextLine();

        System.out.print("Enter Student Name : ");
        String name = sc.nextLine();

        System.out.print("Enter Department : ");
        String dept = sc.nextLine();

        System.out.print("Enter Total Credits : ");
        int cred = sc.nextInt();

        String sql =
                "INSERT INTO student (ID, name, dept_name, tot_cred) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setInt(4, cred);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student record added successfully!");
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            System.out.println(
                    "Error: Student with ID " + id + " already exists."
            );

        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    // CASE 3 – Delete a student
    static void deleteStudentRecord(Connection conn) {

        System.out.print("Enter Student ID to delete: ");
        String id = sc.next();

        if (studentExists(conn, id)) {
            System.out.println("No student found with ID " + id + ".");
            return;
        }

        try {

            String deleteTakes = "DELETE FROM takes WHERE ID = ?";

            try (PreparedStatement ps =
                         conn.prepareStatement(deleteTakes)) {

                ps.setString(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println(
                            "Removed " + rows +
                            " course enrollment(s) for student " +
                            id + "."
                    );
                }
            }

            String deleteStudent =
                    "DELETE FROM student WHERE ID = ?";

            try (PreparedStatement ps =
                         conn.prepareStatement(deleteStudent)) {

                ps.setString(1, id);

                ps.executeUpdate();

                System.out.println(
                        "Student record with ID " +
                        id +
                        " deleted successfully."
                );
            }

        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }

    // CASE 4 – Update a student's record
    static void updateStudentRecord(Connection conn) {

        System.out.print("Enter Student ID to update: ");
        String id = sc.next();

        if (studentExists(conn, id)) {
            System.out.println("No student found with ID " + id + ".");
            return;
        }

        System.out.println("What do you want to update?");
        System.out.println(" 1: Name");
        System.out.println(" 2: Dept_Name");
        System.out.println(" 3: Tot_Cred");

        System.out.print("Enter choice (1/2/3): ");
        int ch1 = sc.nextInt();

        String sql = "";

        switch (ch1) {

            case 1:

                sc.nextLine();

                System.out.print("Enter new Name: ");
                String newName = sc.nextLine();

                sql = "UPDATE student SET name = ? WHERE ID = ?";

                executeUpdate(conn, sql, newName, id);
                break;

            case 2:

                sc.nextLine();

                System.out.print("Enter new Department: ");
                String newDept = sc.nextLine();

                sql = "UPDATE student SET dept_name = ? WHERE ID = ?";

                executeUpdate(conn, sql, newDept, id);
                break;

            case 3:

                System.out.print("Enter new Total Credits: ");
                int newCred = sc.nextInt();

                sql = "UPDATE student SET tot_cred = ? WHERE ID = ?";

                executeUpdate(
                        conn,
                        sql,
                        String.valueOf(newCred),
                        id
                );

                break;

            default:
                System.out.println(
                        "Invalid choice! Please enter 1, 2, or 3."
                );
        }
    }

    static void executeUpdate(
            Connection conn,
            String sql,
            String value,
            String id
    ) {

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Record updated successfully.");
            } else {
                System.out.println("No record updated.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }
// CASE 5 – Show instructor details
static void showInstructorDetails(Connection conn) {

    System.out.print("Enter Instructor ID: ");
    String id = sc.next();

    String sql = "SELECT * FROM instructor WHERE ID = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        System.out.println("\n" + "=".repeat(65));

        System.out.printf(
                "%-10s %-20s %-15s %-10s%n",
                "ID",
                "Name",
                "Dept_Name",
                "Salary"
        );

        System.out.println("-".repeat(65));

        if (rs.next()) {

            System.out.printf(
                    "%-10s %-20s %-15s %-10.2f%n",
                    rs.getString("ID"),
                    rs.getString("name"),
                    rs.getString("dept_name"),
                    rs.getDouble("salary")
            );

        } else {

            System.out.println(
                    "No instructor found with ID " + id + "."
            );
        }

        System.out.println("=".repeat(65));

    } catch (SQLException e) {

        System.out.println(
                "Error fetching instructor: " + e.getMessage()
        );
    }
}

// CASE 6 – Show course details with enrolled students
static void showCourseWithEnrolledStudents(Connection conn) {

    System.out.print("Enter Course ID: ");
    String courseId = sc.next();

    String courseSql =
            "SELECT * FROM course WHERE course_id = ?";

    try (PreparedStatement ps =
                 conn.prepareStatement(courseSql)) {

        ps.setString(1, courseId);

        ResultSet rs = ps.executeQuery();

        System.out.println("\n" + "=".repeat(70));

        if (rs.next()) {

            System.out.println(
                    "Course ID : " +
                    rs.getString("course_id")
            );

            System.out.println(
                    "Course Name : " +
                    rs.getString("title")
            );

            System.out.println(
                    "Department : " +
                    rs.getString("dept_name")
            );

            System.out.println(
                    "Credits : " +
                    rs.getInt("credits")
            );

        } else {

            System.out.println(
                    "No course found with ID " +
                    courseId + "."
            );

            System.out.println("=".repeat(70));
            return;
        }

    } catch (SQLException e) {

        System.out.println(
                "Error fetching course: " + e.getMessage()
        );

        return;
    }
}

// CASE 7 – Show courses taught by instructor
static void showCoursesByInstructor(Connection conn) {

    System.out.print("Enter Instructor ID: ");
    String id = sc.next();

    String sql =
            "SELECT i.ID AS instr_id, " +
            "i.name AS instr_name, " +
            "c.course_id, c.title, " +
            "t.semester, t.year " +
            "FROM instructor i " +
            "JOIN teaches t ON i.ID = t.ID " +
            "JOIN course c ON t.course_id = c.course_id " +
            "WHERE i.ID = ?";

    try (PreparedStatement ps =
                 conn.prepareStatement(sql)) {

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            System.out.println(
                    rs.getString("instr_name") +
                    " teaches " +
                    rs.getString("title")
            );
        }

    } catch (SQLException e) {

        System.out.println(
                "Error fetching courses: " + e.getMessage()
        );
    }
}

// CASE 8 – Deposit HRA
static void depositHRA(Connection conn) {

    System.out.print("Enter Instructor ID: ");
    String id = sc.next();

    String sql =
            "UPDATE instructor " +
            "SET salary = salary + (salary * 0.15) " +
            "WHERE ID = ?";

    try (PreparedStatement ps =
                 conn.prepareStatement(sql)) {

        ps.setString(1, id);

        ps.executeUpdate();

        System.out.println(
                "HRA added successfully."
        );

    } catch (SQLException e) {

        System.out.println(
                "Error depositing HRA: " +
                e.getMessage()
        );
    }
}

// CASE 9 – Deduct TDS
static void deductTDS(Connection conn) {

    System.out.print("Enter Instructor ID: ");
    String id = sc.next();

    String sql =
            "UPDATE instructor " +
            "SET salary = salary - (salary * 0.20) " +
            "WHERE ID = ?";

    try (PreparedStatement ps =
                 conn.prepareStatement(sql)) {

        ps.setString(1, id);

        ps.executeUpdate();

        System.out.println(
                "TDS deducted successfully."
        );

    } catch (SQLException e) {

        System.out.println(
                "Error deducting TDS: " +
                e.getMessage()
        );
    }
}

static boolean studentExists(Connection conn, String id) {

    String sql =
            "SELECT 1 FROM student WHERE ID = ?";

    try (PreparedStatement ps =
                 conn.prepareStatement(sql)) {

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        return !rs.next();

    } catch (SQLException e) {

        return true;
    }
}

static boolean instructorExists(Connection conn, String id) {

    String sql =
            "SELECT 1 FROM instructor WHERE ID = ?";

    try (PreparedStatement ps =
                 conn.prepareStatement(sql)) {

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        return !rs.next();

    } catch (SQLException e) {

        return true;
    }
}
}
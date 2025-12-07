package dao;

import model.Student;
import exception.*;
import util.StudentValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentDAO {

    // Functional Interface for filtering
    @FunctionalInterface
    public interface StudentFilter {
        boolean test(Student student);
    }

    public void addStudent(Student student) throws InvalidGPAException, InvalidEmailException,
            DuplicateEmailException, SQLException {
        StudentValidator.validateStudent(student);

        String sql = "INSERT INTO students (name, email, major, gpa, enrollment_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getMajor());
            pstmt.setDouble(4, student.getGpa());
            pstmt.setDate(5, java.sql.Date.valueOf(student.getEnrollmentDate()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") || e.getErrorCode() == 1062) {
                throw new DuplicateEmailException(student.getEmail());
            }
            throw e;
        }
    }

    public void updateStudent(Student student) throws StudentNotFoundException, InvalidGPAException,
            InvalidEmailException, SQLException {
        StudentValidator.validateStudent(student);

        if (getStudentById(student.getId()) == null) {
            throw new StudentNotFoundException(student.getId());
        }

        String sql = "UPDATE students SET name=?, email=?, major=?, gpa=?, enrollment_date=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getMajor());
            pstmt.setDouble(4, student.getGpa());
            pstmt.setDate(5, java.sql.Date.valueOf(student.getEnrollmentDate()));
            pstmt.setInt(6, student.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new StudentNotFoundException(student.getId());
            }
        }
    }

    public void deleteStudent(int id) throws StudentNotFoundException, SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new StudentNotFoundException(id);
            }
        }
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            return null;
        }
    }

    public List<Student> getAllStudents() throws DatabaseConnectionException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to retrieve students from database", e);
        }

        return students;
    }

    public Set<String> getUniqueMajors() throws DatabaseConnectionException {
        Set<String> majors = new HashSet<>();
        String sql = "SELECT DISTINCT major FROM students";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                majors.add(rs.getString("major"));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to retrieve majors from database", e);
        }

        return majors;
    }

    public Map<String, List<Student>> getStudentsByMajor() throws DatabaseConnectionException {
        List<Student> allStudents = getAllStudents();
        return allStudents.stream()
                .collect(Collectors.groupingBy(Student::getMajor));
    }

    public List<Student> getStudentsWithGPAAbove(double minGpa) throws InvalidGPAException, DatabaseConnectionException {
        StudentValidator.validateGPA(minGpa);

        return getAllStudents().stream()
                .filter(s -> s.getGpa() >= minGpa)
                .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
                .collect(Collectors.toList());
    }

    public Map<String, Double> getAverageGPAByMajor() throws DatabaseConnectionException {
        return getAllStudents().stream()
                .collect(Collectors.groupingBy(
                        Student::getMajor,
                        Collectors.averagingDouble(Student::getGpa)
                ));
    }

    public List<Student> filterStudents(StudentFilter filter) throws DatabaseConnectionException {
        return getAllStudents().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public Map<String, Long> countStudentsByMajor() throws DatabaseConnectionException {
        return getAllStudents().stream()
                .collect(Collectors.groupingBy(
                        Student::getMajor,
                        Collectors.counting()
                ));
    }

    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        java.sql.Date sqlDate = rs.getDate("enrollment_date");
        java.time.LocalDate enrollmentDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("major"),
                rs.getDouble("gpa"),
                enrollmentDate
        );
    }
}
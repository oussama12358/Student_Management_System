package util;

import model.Student;
import exception.InvalidGPAException;
import exception.InvalidEmailException;

import java.util.regex.Pattern;

public class StudentValidator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static void validateGPA(double gpa) throws InvalidGPAException {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new InvalidGPAException("GPA must be between 0.0 and 4.0. Provided: " + gpa);
        }
    }

    public static void validateEmail(String email) throws InvalidEmailException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be empty.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Student name must be at least 2 characters long.");
        }
    }

    public static void validateMajor(String major) {
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException("Major cannot be empty.");
        }
    }

    public static void validateStudent(Student student) throws InvalidGPAException, InvalidEmailException {
        validateName(student.getName());
        validateEmail(student.getEmail());
        validateMajor(student.getMajor());
        validateGPA(student.getGpa());
    }
}

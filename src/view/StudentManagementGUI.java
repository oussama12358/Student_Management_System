package view;

import dao.DatabaseConnection;
import dao.StudentDAO;
import model.Student;
import exception.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StudentManagementGUI extends JFrame {
    private StudentDAO dao;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField, majorField, gpaField;

    public StudentManagementGUI() {
        dao = new StudentDAO();

        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Student Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadStudents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        nameField = new JTextField();
        emailField = new JTextField();
        majorField = new JTextField();
        gpaField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Major:"));
        panel.add(majorField);
        panel.add(new JLabel("GPA (0.0-4.0):"));
        panel.add(gpaField);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Email", "Major", "GPA", "Enrollment Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudent();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addBtn = new JButton("Add Student");
        JButton updateBtn = new JButton("Update Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton clearBtn = new JButton("Clear Fields");
        JButton statsBtn = new JButton("View Statistics");
        JButton filterBtn = new JButton("Filter High GPA");

        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());
        statsBtn.addActionListener(e -> showStatistics());
        filterBtn.addActionListener(e -> filterHighGPA());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(statsBtn);
        panel.add(filterBtn);

        return panel;
    }

    private void addStudent() {
        try {
            Student student = new Student();
            student.setName(nameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setMajor(majorField.getText().trim());

            String gpaText = gpaField.getText().trim();
            if (gpaText.isEmpty()) {
                throw new IllegalArgumentException("GPA field cannot be empty");
            }

            double gpa = Double.parseDouble(gpaText);
            student.setGpa(gpa);
            student.setEnrollmentDate(LocalDate.now());

            dao.addStudent(student);

            JOptionPane.showMessageDialog(this,
                    "Student added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadStudents();
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid GPA format. Please enter a valid number (e.g., 3.5)",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InvalidGPAException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Invalid GPA",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InvalidEmailException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateEmailException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Duplicate Email",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to update!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);

            Student student = new Student();
            student.setId(id);
            student.setName(nameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setMajor(majorField.getText().trim());

            double gpa = Double.parseDouble(gpaField.getText().trim());
            student.setGpa(gpa);
            student.setEnrollmentDate((LocalDate) tableModel.getValueAt(selectedRow, 5));

            dao.updateStudent(student);

            JOptionPane.showMessageDialog(this,
                    "Student updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadStudents();
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid GPA format. Please enter a valid number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (StudentNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Student Not Found",
                    JOptionPane.ERROR_MESSAGE);
            loadStudents();
        } catch (InvalidGPAException | InvalidEmailException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating student: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                dao.deleteStudent(id);

                JOptionPane.showMessageDialog(this,
                        "Student deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadStudents();
                clearFields();

            } catch (StudentNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Student Not Found",
                        JOptionPane.ERROR_MESSAGE);
                loadStudents();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting student: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void loadStudents() {
        try {
            tableModel.setRowCount(0);
            List<Student> students = dao.getAllStudents();

            students.forEach(s -> tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getEmail(),
                    s.getMajor(), s.getGpa(), s.getEnrollmentDate()
            }));

        } catch (DatabaseConnectionException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database connection error: " + ex.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            try {
                nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
                emailField.setText((String) tableModel.getValueAt(selectedRow, 2));
                majorField.setText((String) tableModel.getValueAt(selectedRow, 3));
                gpaField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading student data: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        majorField.setText("");
        gpaField.setText("");
        table.clearSelection();
    }

    private void showStatistics() {
        try {
            StringBuilder stats = new StringBuilder();
            stats.append("=== STUDENT STATISTICS ===\n\n");

            Map<String, Double> avgGPA = dao.getAverageGPAByMajor();
            stats.append("Average GPA by Major:\n");

            avgGPA.forEach((major, avg) ->
                    stats.append(String.format("  %s: %.2f\n", major, avg))
            );

            stats.append("\n");

            Map<String, Long> counts = dao.countStudentsByMajor();
            stats.append("Student Count by Major:\n");

            counts.forEach((major, count) ->
                    stats.append(String.format("  %s: %d students\n", major, count))
            );

            JOptionPane.showMessageDialog(this,
                    stats.toString(),
                    "Statistics",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (DatabaseConnectionException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating statistics: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterHighGPA() {
        try {
            String input = JOptionPane.showInputDialog(this,
                    "Enter minimum GPA (0.0-4.0):",
                    "Filter by GPA",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.trim().isEmpty()) {
                double minGpa = Double.parseDouble(input.trim());
                List<Student> filtered = dao.getStudentsWithGPAAbove(minGpa);

                tableModel.setRowCount(0);

                filtered.forEach(s -> tableModel.addRow(new Object[]{
                        s.getId(), s.getName(), s.getEmail(),
                        s.getMajor(), s.getGpa(), s.getEnrollmentDate()
                }));

                JOptionPane.showMessageDialog(this,
                        "Found " + filtered.size() + " students with GPA >= " + minGpa,
                        "Filter Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid GPA format. Please enter a valid number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InvalidGPAException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Invalid GPA",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseConnectionException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering students: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
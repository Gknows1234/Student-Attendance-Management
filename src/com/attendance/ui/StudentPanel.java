package com.attendance.ui;

import com.attendance.exception.AttendanceException;
import com.attendance.model.Student;
import com.attendance.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Panel for managing students.
 * Demonstrates: JTable, DefaultTableModel, CompletableFuture (concurrency).
 */
public class StudentPanel extends JPanel {
    private final AttendanceService service;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public StudentPanel(AttendanceService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columns = { "ID", "Name", "Roll No" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameField = new JTextField(15);
        JTextField rollField = new JTextField(10);
        JButton addBtn = new JButton("Add Student");
        JButton removeBtn = new JButton("Remove Selected");

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Roll No:"));
        inputPanel.add(rollField);
        inputPanel.add(addBtn);
        inputPanel.add(removeBtn);
        add(inputPanel, BorderLayout.NORTH);

        // Add student with async save (demonstrates concurrency)
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String rollNo = rollField.getText().trim();
            if (name.isEmpty() || rollNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }

            String id = UUID.randomUUID().toString().substring(0, 8);
            Student student = new Student(id, name, rollNo);

            // Async operation using CompletableFuture
            // Async operation using CompletableFuture
            CompletableFuture.runAsync(() -> {
                try {
                    service.addStudent(student);
                } catch (AttendanceException ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE));
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    tableModel.addRow(new Object[] { student.id(), student.name(), student.rollNo() });
                    nameField.setText("");
                    rollField.setText("");
                });
            });
        });

        // Remove student
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a student to remove");
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            try {
                service.removeStudent(id);
                tableModel.removeRow(row);
            } catch (AttendanceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadStudents();
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        for (Student s : service.getAllStudents()) {
            tableModel.addRow(new Object[] { s.id(), s.name(), s.rollNo() });
        }
    }
}

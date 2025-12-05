package com.attendance.ui;

import com.attendance.exception.AttendanceException;
import com.attendance.model.Student;
import com.attendance.model.Subject;
import com.attendance.model.TimeSlot;
import com.attendance.model.TimetableEntry;
import com.attendance.service.AttendanceService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Panel for marking subject-based attendance.
 * Refactored to use Dropdown status and Day Label.
 */
public class AttendanceMarkingPanel extends JPanel {
    private final AttendanceService service;
    private final DefaultTableModel tableModel;
    private final JComboBox<StudentItem> studentCombo;
    private final JComboBox<SubjectItem> subjectCombo;
    private final JComboBox<SlotItem> slotCombo;
    private final JTextField dateField;
    private final JLabel dayLabel;
    private final JComboBox<String> statusCombo;
    private final JTextField studentSearch;
    private final JTextField subjectSearch;

    public AttendanceMarkingPanel(AttendanceService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Attendance Operations"));

        studentCombo = new JComboBox<>();
        subjectCombo = new JComboBox<>();
        slotCombo = new JComboBox<>();
        dateField = new JTextField(10);
        String todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        dateField.setText(todayStr);
        dayLabel = new JLabel();
        updateDayLabel(); // Set initial day

        statusCombo = new JComboBox<>(new String[] { "Present", "Absent" });

        studentSearch = new JTextField(12);
        subjectSearch = new JTextField(12);
        JButton searchBtn = new JButton("Search");

        // Dynamic Day Label Updater
        dateField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateDayLabel();
            }

            public void removeUpdate(DocumentEvent e) {
                updateDayLabel();
            }

            public void changedUpdate(DocumentEvent e) {
                updateDayLabel();
            }
        });

        // Search Filter Logic
        Runnable doFilter = () -> {
            refreshData();
        };

        DocumentListener filterListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                doFilter.run();
            }

            public void removeUpdate(DocumentEvent e) {
                doFilter.run();
            }

            public void changedUpdate(DocumentEvent e) {
                doFilter.run();
            }
        };
        studentSearch.getDocument().addDocumentListener(filterListener);
        subjectSearch.getDocument().addDocumentListener(filterListener);
        searchBtn.addActionListener(e -> doFilter.run());

        JButton markBtn = new JButton("Mark Record");
        markBtn.setBackground(new Color(135, 206, 235)); // Sky Blue
        JButton todayBtn = new JButton("Load Today's Schedule");

        // Row 1: Filter Controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Student Search:"));
        filterPanel.add(studentSearch);
        filterPanel.add(new JLabel("Subject Search:"));
        filterPanel.add(subjectSearch);
        filterPanel.add(searchBtn);
        filterPanel.add(todayBtn);

        // Row 2: Attendance Marking Inputs
        JPanel markPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        markPanel.add(new JLabel("Student:"));
        markPanel.add(studentCombo);
        markPanel.add(new JLabel("Subject:"));
        markPanel.add(subjectCombo);
        markPanel.add(new JLabel("Slot:"));
        markPanel.add(slotCombo);
        markPanel.add(new JLabel("Date:"));
        markPanel.add(dateField);
        markPanel.add(dayLabel); // Add dynamic day label
        markPanel.add(new JLabel("Status:"));
        markPanel.add(statusCombo);

        // Row 3: Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(markBtn);

        inputPanel.add(filterPanel);
        inputPanel.add(markPanel);
        inputPanel.add(buttonPanel);
        add(inputPanel, BorderLayout.NORTH);

        // Attendance table
        String[] columns = { "Student", "Subject", "Date", "Slot", "Status" };
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        markBtn.addActionListener(e -> markAttendance());
        refreshBtn.addActionListener(e -> refreshData());
        todayBtn.addActionListener(e -> loadTodaySchedule());

        refreshData();
    }

    private void updateDayLabel() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            DayOfWeek day = date.getDayOfWeek();
            dayLabel.setText("(" + day.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ")");
            dayLabel.setForeground(Color.BLUE);
        } catch (DateTimeParseException e) {
            dayLabel.setText("(Invalid Date)");
            dayLabel.setForeground(Color.RED);
        }
    }

    private void markAttendance() {
        StudentItem student = (StudentItem) studentCombo.getSelectedItem();
        SubjectItem subject = (SubjectItem) subjectCombo.getSelectedItem();
        SlotItem slot = (SlotItem) slotCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        if (student == null || subject == null || slot == null || status == null) {
            JOptionPane.showMessageDialog(this, "Select all fields");
            return;
        }

        boolean isPresent = "Present".equals(status);

        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD");
            return;
        }

        try {
            service.markAttendance(student.id, subject.code, date, slot.slotNumber, isPresent);
            refreshData();
        } catch (AttendanceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTodaySchedule() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<TimetableEntry> todayClasses = service.getTimetableForDay(today);

        filterSubjects(""); // Reset filter

        if (todayClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No classes scheduled for " + today);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Loaded " + todayClasses.size() + " classes for today. Please select subject/slot.");
        }
    }

    private void refreshData() {
        String query = studentSearch.getText().toLowerCase();

        filterStudents(query);
        refreshSubjectsAndSlots();

        tableModel.setRowCount(0);
        service.getAllAttendance().stream()
                .sorted((r1, r2) -> r2.date().compareTo(r1.date())) // Sort by date desc
                .forEach(r -> {
                    String studentName = service.getAllStudents().stream()
                            .filter(s -> s.id().equals(r.studentId()))
                            .map(Student::name)
                            .findFirst()
                            .orElse("Unknown");

                    if (!query.isEmpty() && !studentName.toLowerCase().contains(query)) {
                        return;
                    }

                    String subjectName = service.findSubjectByCode(r.subjectCode())
                            .map(Subject::name)
                            .orElse(r.subjectCode());
                    tableModel.addRow(new Object[] {
                            studentName,
                            subjectName,
                            r.date().toString(),
                            r.slotNumber(),
                            r.present() ? "Present" : "Absent"
                    });
                });
    }

    private void filterStudents(String query) {
        studentCombo.removeAllItems();
        String q = query == null ? "" : query.toLowerCase();
        service.getAllStudents().stream()
                .filter(s -> s.name().toLowerCase().contains(q) || s.rollNo().toLowerCase().contains(q))
                .forEach(s -> studentCombo.addItem(new StudentItem(s.id(), s.name())));
    }

    private void filterSubjects(String query) {
        subjectCombo.removeAllItems();
        String q = query == null ? "" : query.toLowerCase();
        service.getAllSubjects().stream()
                .filter(s -> s.name().toLowerCase().contains(q) || s.code().toLowerCase().contains(q))
                .forEach(s -> subjectCombo.addItem(new SubjectItem(s.code(), s.name())));
    }

    private void refreshSubjectsAndSlots() {
        if (subjectCombo.getItemCount() == 0)
            filterSubjects(subjectSearch.getText());

        slotCombo.removeAllItems();
        for (TimeSlot ts : service.getAllTimeSlots()) {
            slotCombo.addItem(new SlotItem(ts.slotNumber(), ts.toString()));
        }
    }

    // Helper Static Nested Classes

    private static class StudentItem {
        final String id;
        final String name;

        StudentItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class SubjectItem {
        final String code;
        final String name;

        SubjectItem(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String toString() {
            return code + " - " + name;
        }
    }

    private static class SlotItem {
        final int slotNumber;
        final String display;

        SlotItem(int slotNumber, String display) {
            this.slotNumber = slotNumber;
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}

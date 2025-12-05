package com.attendance.ui;

import com.attendance.exception.AttendanceException;
import com.attendance.model.Subject;
import com.attendance.model.TimeSlot;
import com.attendance.model.TimetableEntry;
import com.attendance.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;

/**
 * Panel for managing subjects and timetable.
 * Demonstrates: Enums (DayOfWeek), JComboBox with enums.
 */
public class TimetablePanel extends JPanel {
    private final AttendanceService service;
    private final DefaultTableModel subjectTableModel;
    private final DefaultTableModel timetableTableModel;

    public TimetablePanel(AttendanceService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Split panel: subjects on left, timetable on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4);

        // --- Subjects Panel ---
        JPanel subjectsPanel = new JPanel(new BorderLayout(5, 5));
        subjectsPanel.setBorder(BorderFactory.createTitledBorder("Subjects"));

        String[] subjectColumns = { "Code", "Name" };
        subjectTableModel = new DefaultTableModel(subjectColumns, 0);
        JTable subjectTable = new JTable(subjectTableModel);
        subjectsPanel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);

        JPanel subjectInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField codeField = new JTextField(6);
        JTextField nameField = new JTextField(12);
        JButton addSubjectBtn = new JButton("Add");
        JButton removeSubjectBtn = new JButton("Remove");

        subjectInputPanel.add(new JLabel("Code:"));
        subjectInputPanel.add(codeField);
        subjectInputPanel.add(new JLabel("Name:"));
        subjectInputPanel.add(nameField);
        subjectInputPanel.add(addSubjectBtn);
        subjectInputPanel.add(removeSubjectBtn);
        subjectsPanel.add(subjectInputPanel, BorderLayout.NORTH);

        addSubjectBtn.addActionListener(e -> {
            String code = codeField.getText().trim().toUpperCase();
            String name = nameField.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill both fields");
                return;
            }
            try {
                service.addSubject(new Subject(code, name));
                refreshSubjects();
                codeField.setText("");
                nameField.setText("");
            } catch (AttendanceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeSubjectBtn.addActionListener(e -> {
            int row = subjectTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a subject");
                return;
            }
            String code = (String) subjectTableModel.getValueAt(row, 0);
            try {
                service.removeSubject(code);
                refreshSubjects();
                refreshTimetable();
            } catch (AttendanceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        splitPane.setLeftComponent(subjectsPanel);

        // --- Timetable Panel ---
        JPanel timetablePanel = new JPanel(new BorderLayout(5, 5));
        timetablePanel.setBorder(BorderFactory.createTitledBorder("Timetable"));

        String[] ttColumns = { "Day", "Slot", "Subject" };
        timetableTableModel = new DefaultTableModel(ttColumns, 0);
        JTable timetableTable = new JTable(timetableTableModel);
        timetablePanel.add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        JPanel ttInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<DayOfWeek> dayCombo = new JComboBox<>(new DayOfWeek[] {
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        });
        JComboBox<SlotItem> slotCombo = new JComboBox<>();
        JComboBox<SubjectItem> subjectCombo = new JComboBox<>();
        JButton addTTBtn = new JButton("Add");
        JButton removeTTBtn = new JButton("Remove");

        ttInputPanel.add(new JLabel("Day:"));
        ttInputPanel.add(dayCombo);
        ttInputPanel.add(new JLabel("Slot:"));
        ttInputPanel.add(slotCombo);
        ttInputPanel.add(new JLabel("Subject:"));
        ttInputPanel.add(subjectCombo);
        ttInputPanel.add(addTTBtn);
        ttInputPanel.add(removeTTBtn);
        timetablePanel.add(ttInputPanel, BorderLayout.NORTH);

        addTTBtn.addActionListener(e -> {
            DayOfWeek day = (DayOfWeek) dayCombo.getSelectedItem();
            SlotItem slot = (SlotItem) slotCombo.getSelectedItem();
            SubjectItem subj = (SubjectItem) subjectCombo.getSelectedItem();
            if (day == null || slot == null || subj == null) {
                JOptionPane.showMessageDialog(this, "Select all fields");
                return;
            }
            try {
                service.addTimetableEntry(new TimetableEntry(day, slot.slotNumber, subj.code));
                refreshTimetable();
            } catch (AttendanceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeTTBtn.addActionListener(e -> {
            int row = timetableTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an entry");
                return;
            }
            String dayStr = (String) timetableTableModel.getValueAt(row, 0);
            int slotNum = (Integer) timetableTableModel.getValueAt(row, 1);
            try {
                service.removeTimetableEntry(DayOfWeek.valueOf(dayStr), slotNum);
                refreshTimetable();
            } catch (AttendanceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        splitPane.setRightComponent(timetablePanel);
        add(splitPane, BorderLayout.CENTER);

        // Populate combos
        for (TimeSlot ts : service.getAllTimeSlots()) {
            slotCombo.addItem(new SlotItem(ts.slotNumber(), ts.toString()));
        }

        refreshSubjects();
        refreshTimetable();

        // Update subject combo when subjects change
        addSubjectBtn.addActionListener(e -> refreshSubjectCombo(subjectCombo));
        removeSubjectBtn.addActionListener(e -> refreshSubjectCombo(subjectCombo));
        refreshSubjectCombo(subjectCombo);
    }

    private void refreshSubjects() {
        subjectTableModel.setRowCount(0);
        for (Subject s : service.getAllSubjects()) {
            subjectTableModel.addRow(new Object[] { s.code(), s.name() });
        }
    }

    private void refreshTimetable() {
        timetableTableModel.setRowCount(0);
        for (TimetableEntry t : service.getTimetable()) {
            String subjectName = service.findSubjectByCode(t.subjectCode())
                    .map(Subject::name)
                    .orElse(t.subjectCode());
            timetableTableModel.addRow(new Object[] { t.day().name(), t.slotNumber(), subjectName });
        }
    }

    private void refreshSubjectCombo(JComboBox<SubjectItem> combo) {
        combo.removeAllItems();
        for (Subject s : service.getAllSubjects()) {
            combo.addItem(new SubjectItem(s.code(), s.name()));
        }
    }

    private record SlotItem(int slotNumber, String display) {
        @Override
        public String toString() {
            return display;
        }
    }

    private record SubjectItem(String code, String name) {
        @Override
        public String toString() {
            return code + " - " + name;
        }
    }
}

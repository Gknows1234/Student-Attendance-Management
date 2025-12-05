package com.attendance.ui;

import com.attendance.model.Student;
import com.attendance.model.Subject;
import com.attendance.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Panel for viewing attendance reports with subject-wise breakdown.
 * Demonstrates: Streams (via service), Map iteration, nested data.
 */
public class ReportPanel extends JPanel {
    private final AttendanceService service;
    private final DefaultTableModel overallTableModel;
    private final DefaultTableModel subjectTableModel;
    private final JSpinner thresholdSpinner;

    public ReportPanel(AttendanceService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        thresholdSpinner = new JSpinner(new SpinnerNumberModel(75.0, 0.0, 100.0, 5.0));
        JButton lowAttendanceBtn = new JButton("Show Low Attendance");

        controlPanel.add(refreshBtn);
        controlPanel.add(new JLabel("Threshold %:"));
        controlPanel.add(thresholdSpinner);
        controlPanel.add(lowAttendanceBtn);
        add(controlPanel, BorderLayout.NORTH);
  
        // Tabbed pane for overall and subject-wise
        JTabbedPane reportTabs = new JTabbedPane();

        // Overall attendance table
        String[] overallColumns = {"Student ID", "Name", "Roll No", "Overall %"};
        overallTableModel = new DefaultTableModel(overallColumns, 0);
        JTable overallTable = new JTable(overallTableModel);
        reportTabs.addTab("Overall", new JScrollPane(overallTable));

        // Subject-wise attendance table
        String[] subjectColumns = {"Student", "Subject", "Attendance %"};
        subjectTableModel = new DefaultTableModel(subjectColumns, 0);
        JTable subjectTable = new JTable(subjectTableModel);
        reportTabs.addTab("Subject-wise", new JScrollPane(subjectTable));

        add(reportTabs, BorderLayout.CENTER);
 
        refreshBtn.addActionListener(e -> refreshReport());
        lowAttendanceBtn.addActionListener(e -> showLowAttendance());

        refreshReport();
    }

    private void refreshReport() {
        // Overall report
        overallTableModel.setRowCount(0);
        Map<String, Double> percentages = service.getAllAttendancePercentages();

        for (Student s : service.getAllStudents()) {
            double pct = percentages.getOrDefault(s.id(), 0.0);
            overallTableModel.addRow(new Object[]{
                    s.id(), 
                    s.name(),
                    s.rollNo(),
                    String.format("%.1f%%", pct)
            });
        }

        // Subject-wise report
        subjectTableModel.setRowCount(0);
        Map<String, Map<String, Double>> subjectWise = service.getSubjectWiseAttendance();

        for (Student s : service.getAllStudents()) {
            Map<String, Double> studentSubjects = subjectWise.get(s.id());
            if (studentSubjects != null) {
                for (Subject sub : service.getAllSubjects()) {
                    double pct = studentSubjects.getOrDefault(sub.code(), 0.0);
                    subjectTableModel.addRow(new Object[]{
                            s.name(),
                            sub.name(),
                            String.format("%.1f%%", pct)
                    });
                }
            }
        }
    }

    private void showLowAttendance() {
        double threshold = (Double) thresholdSpinner.getValue();
        overallTableModel.setRowCount(0);

        for (Student s : service.getStudentsBelowAttendance(threshold)) {
            double pct = service.getAttendancePercentage(s.id());
            overallTableModel.addRow(new Object[]{
                    s.id(),
                    s.name(),
                    s.rollNo(),
                    String.format("%.1f%%", pct)
            });
        }
    }
}

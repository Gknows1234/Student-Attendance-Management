package com.attendance.service;

import com.attendance.exception.AttendanceException;
import com.attendance.exception.DuplicateRecordException;
import com.attendance.exception.StudentNotFoundException;
import com.attendance.exception.ValidationException;
import com.attendance.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing students, subjects, timetable, and attendance.
 * Demonstrates: Collections (List, Map, Set), Streams API, lambdas,
 * functional interfaces, method references.
 */
public class AttendanceService {
    private final DataManager dataManager;
    private List<Student> students;
    private List<AttendanceRecord> attendanceRecords;
    private List<Subject> subjects;
    private List<TimeSlot> timeSlots;
    private List<TimetableEntry> timetable;

    public AttendanceService(DataManager dataManager) throws AttendanceException {
        this.dataManager = dataManager;
        this.students = new ArrayList<>(dataManager.loadStudents());
        this.attendanceRecords = new ArrayList<>(dataManager.loadAttendance());
        this.subjects = new ArrayList<>(dataManager.loadSubjects());
        this.timeSlots = new ArrayList<>(dataManager.loadTimeSlots());
        this.timetable = new ArrayList<>(dataManager.loadTimetable());

        // Initialize default time slots if empty
        if (timeSlots.isEmpty()) {
            initDefaultTimeSlots();
        }
    }

    private void initDefaultTimeSlots() throws AttendanceException {
        timeSlots.add(new TimeSlot(1, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        timeSlots.add(new TimeSlot(2, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        timeSlots.add(new TimeSlot(3, LocalTime.of(11, 0), LocalTime.of(12, 0)));
        timeSlots.add(new TimeSlot(4, LocalTime.of(13, 0), LocalTime.of(14, 0)));
        timeSlots.add(new TimeSlot(5, LocalTime.of(14, 0), LocalTime.of(15, 0)));
        timeSlots.add(new TimeSlot(6, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        dataManager.saveTimeSlots(timeSlots);
    }

    // --- Student Operations ---

    public void addStudent(Student student) throws AttendanceException {
        validateStudent(student);

        boolean exists = students.stream()
                .anyMatch(s -> s.id().equals(student.id()) || s.rollNo().equals(student.rollNo()));
        if (exists) {
            throw new DuplicateRecordException("Student with same ID or Roll No already exists");
        }
        students.add(student);
        dataManager.saveStudents(students);
    }

    private void validateStudent(Student s) throws ValidationException {
        if (s.id() == null || s.id().trim().isEmpty())
            throw new ValidationException("Student ID cannot be empty");
        if (s.name() == null || s.name().trim().isEmpty())
            throw new ValidationException("Student Name cannot be empty");
        if (s.rollNo() == null || s.rollNo().trim().isEmpty())
            throw new ValidationException("Roll No cannot be empty");

        // Example Core Java Regex: Alphanumeric ID only
        if (!s.id().matches("[a-zA-Z0-9]+")) {
            throw new ValidationException("Student ID must be alphanumeric");
        }
    }

    public void removeStudent(String studentId) throws AttendanceException {
        boolean removed = students.removeIf(s -> s.id().equals(studentId));
        if (!removed) {
            throw new StudentNotFoundException(studentId);
        }
        attendanceRecords.removeIf(r -> r.studentId().equals(studentId));
        dataManager.saveStudents(students);
        dataManager.saveAttendance(attendanceRecords);
    }

    public Student findStudentById(String studentId) throws StudentNotFoundException {
        return students.stream()
                .filter(s -> s.id().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    // --- Subject Operations ---

    public void addSubject(Subject subject) throws AttendanceException {
        if (subject.code() == null || subject.code().trim().isEmpty())
            throw new ValidationException("Subject Code cannot be empty");
        if (subject.name() == null || subject.name().trim().isEmpty())
            throw new ValidationException("Subject Name cannot be empty");

        boolean exists = subjects.stream().anyMatch(s -> s.code().equals(subject.code()));
        if (exists) {
            throw new DuplicateRecordException("Subject with code " + subject.code() + " already exists");
        }
        subjects.add(subject);
        dataManager.saveSubjects(subjects);
    }

    public void removeSubject(String subjectCode) throws AttendanceException {
        subjects.removeIf(s -> s.code().equals(subjectCode));
        timetable.removeIf(t -> t.subjectCode().equals(subjectCode));
        dataManager.saveSubjects(subjects);
        dataManager.saveTimetable(timetable);
    }

    public List<Subject> getAllSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public Optional<Subject> findSubjectByCode(String code) {
        return subjects.stream().filter(s -> s.code().equals(code)).findFirst();
    }

    // --- Time Slot Operations ---

    public List<TimeSlot> getAllTimeSlots() {
        return Collections.unmodifiableList(timeSlots);
    }

    // --- Timetable Operations ---

    public void addTimetableEntry(TimetableEntry entry) throws AttendanceException {
        // Check for conflict (same day and slot)
        boolean conflict = timetable.stream()
                .anyMatch(t -> t.day().equals(entry.day()) && t.slotNumber() == entry.slotNumber());
        if (conflict) {
            throw new DuplicateRecordException(
                    "Timetable slot already occupied for " + entry.day() + " slot " + entry.slotNumber());
        }
        timetable.add(entry);
        dataManager.saveTimetable(timetable);
    }

    public void removeTimetableEntry(DayOfWeek day, int slotNumber) throws AttendanceException {
        timetable.removeIf(t -> t.day().equals(day) && t.slotNumber() == slotNumber);
        dataManager.saveTimetable(timetable);
    }

    public List<TimetableEntry> getTimetable() {
        return Collections.unmodifiableList(timetable);
    }

    public List<TimetableEntry> getTimetableForDay(DayOfWeek day) {
        return timetable.stream()
                .filter(t -> t.day().equals(day))
                .sorted(Comparator.comparingInt(TimetableEntry::slotNumber))
                .collect(Collectors.toList());
    }

    // --- Attendance Operations ---

    public void markAttendance(String studentId, String subjectCode, LocalDate date, int slotNumber, boolean present)
            throws AttendanceException {
        findStudentById(studentId);

        // Validate against Timetable
        DayOfWeek day = date.getDayOfWeek();
        Optional<TimetableEntry> scheduledClass = timetable.stream()
                .filter(t -> t.day().equals(day) && t.slotNumber() == slotNumber)
                .findFirst();

        if (scheduledClass.isPresent()) {
            String scheduledSubject = scheduledClass.get().subjectCode();
            if (!scheduledSubject.equals(subjectCode)) {
                // Determine subject names for better error message
                String scheduledName = findSubjectByCode(scheduledSubject).map(Subject::name).orElse(scheduledSubject);
                String selectedName = findSubjectByCode(subjectCode).map(Subject::name).orElse(subjectCode);
                throw new ValidationException(
                        String.format("Timetable Mismatch: Slot %d on %s is scheduled for '%s', not '%s'.",
                                slotNumber, day, scheduledName, selectedName));
            }
        }

        // Check for duplicate record
        // Remove existing record if present (allow overwrite/update for corrections)
        attendanceRecords.removeIf(r -> r.studentId().equals(studentId)
                && r.date().equals(date)
                && r.slotNumber() == slotNumber);

        attendanceRecords.add(new AttendanceRecord(studentId, subjectCode, date, slotNumber, present));
        dataManager.saveAttendance(attendanceRecords);
    }

    public List<AttendanceRecord> getAttendanceByStudent(String studentId) {
        return attendanceRecords.stream()
                .filter(r -> r.studentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getAttendanceBySubject(String subjectCode) {
        return attendanceRecords.stream()
                .filter(r -> r.subjectCode().equals(subjectCode))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getAttendanceByDate(LocalDate date) {
        return attendanceRecords.stream()
                .filter(r -> r.date().equals(date))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getAllAttendance() {
        return Collections.unmodifiableList(attendanceRecords);
    }

    // --- Reporting with Streams ---

    public double getAttendancePercentage(String studentId) {
        List<AttendanceRecord> records = getAttendanceByStudent(studentId);
        if (records.isEmpty())
            return 0.0;
        long presentCount = records.stream().filter(AttendanceRecord::present).count();
        return (presentCount * 100.0) / records.size();
    }

    public double getAttendancePercentageBySubject(String studentId, String subjectCode) {
        List<AttendanceRecord> records = attendanceRecords.stream()
                .filter(r -> r.studentId().equals(studentId) && r.subjectCode().equals(subjectCode))
                .collect(Collectors.toList());
        if (records.isEmpty())
            return 0.0;
        long presentCount = records.stream().filter(AttendanceRecord::present).count();
        return (presentCount * 100.0) / records.size();
    }

    public Map<String, Double> getAllAttendancePercentages() {
        return students.stream()
                .collect(Collectors.toMap(Student::id, s -> getAttendancePercentage(s.id())));
    }

    public Map<String, Map<String, Double>> getSubjectWiseAttendance() {
        Map<String, Map<String, Double>> result = new HashMap<>();
        for (Student s : students) {
            Map<String, Double> subjectPct = new HashMap<>();
            for (Subject sub : subjects) {
                subjectPct.put(sub.code(), getAttendancePercentageBySubject(s.id(), sub.code()));
            }
            result.put(s.id(), subjectPct);
        }
        return result;
    }

    public List<Student> getStudentsBelowAttendance(double threshold) {
        return students.stream()
                .filter(s -> getAttendancePercentage(s.id()) < threshold)
                .collect(Collectors.toList());
    }
}

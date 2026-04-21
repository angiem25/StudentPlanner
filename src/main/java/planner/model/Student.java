package planner.model;

import java.util.UUID;

/**
 * Represents a student in the student planner.
 * A student has personal information and academic details.
 */
public class Student {
    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private String studentId;
    private int year;
    private String major;
    
    /**
     * Creates a new student with a generated unique ID.
     * @param firstName The student's first name
     * @param lastName The student's last name
     * @param email The student's email address
     * @param studentId The student's institutional ID
     * @param year The academic year (1-4 for undergraduate)
     * @param major The student's major field of study
     */
    public Student(String firstName, String lastName, String email, String studentId, int year, String major) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentId = studentId;
        this.year = year;
        this.major = major;
    }
    
    /**
     * Creates a new student with a generated unique ID and default year 1.
     * @param firstName The student's first name
     * @param lastName The student's last name
     * @param email The student's email address
     * @param studentId The student's institutional ID
     * @param major The student's major field of study
     */
    public Student(String firstName, String lastName, String email, String studentId, String major) {
        this(firstName, lastName, email, studentId, 1, major);
    }
    
    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getStudentId() { return studentId; }
    public int getYear() { return year; }
    public String getMajor() { return major; }
    
    /**
     * Gets the student's full name.
     * @return The full name (first + last)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setYear(int year) { this.year = year; }
    public void setMajor(String major) { this.major = major; }
    
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", studentId='" + studentId + '\'' +
                ", year=" + year +
                ", major='" + major + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

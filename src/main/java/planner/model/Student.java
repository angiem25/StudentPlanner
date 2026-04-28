package planner.model;

import java.util.UUID;

/**
 * Represents a student in the student planner.
 * A student has personal information and academic details.
 */
public class Student {
    
    /**
     * Enumeration for academic years.
     */
    public enum AcademicYear {
        FRESHMAN("Freshman", 1),
        SOPHOMORE("Sophomore", 2),
        JUNIOR("Junior", 3),
        SENIOR("Senior", 4);
        
        private final String displayName;
        private final int numericValue;
        
        AcademicYear(String displayName, int numericValue) {
            this.displayName = displayName;
            this.numericValue = numericValue;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getNumericValue() {
            return numericValue;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        /**
         * Gets the AcademicYear from its numeric value.
         * @param value The numeric value (1-4)
         * @return The corresponding AcademicYear, or FRESHMAN if invalid
         */
        public static AcademicYear fromNumeric(int value) {
            return switch (value) {
                case 1 -> FRESHMAN;
                case 2 -> SOPHOMORE;
                case 3 -> JUNIOR;
                case 4 -> SENIOR;
                default -> FRESHMAN;
            };
        }
        
        /**
         * Gets the AcademicYear from its display name.
         * @param displayName The display name
         * @return The corresponding AcademicYear, or FRESHMAN if invalid
         */
        public static AcademicYear fromDisplayName(String displayName) {
            if (displayName == null) return FRESHMAN;
            return switch (displayName.trim().toLowerCase()) {
                case "freshman" -> FRESHMAN;
                case "sophomore" -> SOPHOMORE;
                case "junior" -> JUNIOR;
                case "senior" -> SENIOR;
                default -> FRESHMAN;
            };
        }
    }
    
    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private String studentId;
    private AcademicYear academicYear;
    private String major;
    
    /**
     * Creates a new student with a generated unique ID.
     * @param firstName The student's first name
     * @param lastName The student's last name
     * @param email The student's email address
     * @param studentId The student's institutional ID
     * @param academicYear The academic year (Freshman, Sophomore, Junior, Senior)
     * @param major The student's major field of study
     */
    public Student(String firstName, String lastName, String email, String studentId, AcademicYear academicYear, String major) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentId = studentId;
        this.academicYear = academicYear;
        this.major = major;
    }
    
    /**
     * Creates a new student with a generated unique ID and default year Freshman.
     * @param firstName The student's first name
     * @param lastName The student's last name
     * @param email The student's email address
     * @param studentId The student's institutional ID
     * @param major The student's major field of study
     */
    public Student(String firstName, String lastName, String email, String studentId, String major) {
        this(firstName, lastName, email, studentId, AcademicYear.FRESHMAN, major);
    }
    
    /**
     * Creates a new student with a generated unique ID using numeric year for backward compatibility.
     * @param firstName The student's first name
     * @param lastName The student's last name
     * @param email The student's email address
     * @param studentId The student's institutional ID
     * @param year The academic year (1-4 for undergraduate)
     * @param major The student's major field of study
     */
    public Student(String firstName, String lastName, String email, String studentId, int year, String major) {
        this(firstName, lastName, email, studentId, AcademicYear.fromNumeric(year), major);
    }
    
    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getStudentId() { return studentId; }
    public AcademicYear getAcademicYear() { return academicYear; }
    public String getMajor() { return major; }
    
    /**
     * Gets the academic year as an integer for backward compatibility.
     * @return The numeric year (1-4)
     */
    public int getYear() { return academicYear.getNumericValue(); }
    
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
    public void setAcademicYear(AcademicYear academicYear) { this.academicYear = academicYear; }
    public void setMajor(String major) { this.major = major; }
    
    /**
     * Sets the academic year using an integer for backward compatibility.
     * @param year The numeric year (1-4)
     */
    public void setYear(int year) { this.academicYear = AcademicYear.fromNumeric(year); }
    
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", studentId='" + studentId + '\'' +
                ", academicYear=" + academicYear +
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

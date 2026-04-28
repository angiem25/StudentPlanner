package planner.model;

import java.util.UUID;

/**
 * Represents a course in the student planner.
 * A course has a name, code, instructor, and credits.
 */
public class Course {
    private final String id;
    private String name;
    private String code;
    private String instructor;
    private int credits;
    
    /**
     * Creates a new course with a generated unique ID.
     * @param name The course name
     * @param code The course code (e.g., "CS101")
     * @param instructor The instructor name
     * @param credits The number of credits for the course
     */
    public Course(String name, String code, String instructor, int credits) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.code = code;
        this.instructor = instructor;
        this.credits = credits;
    }
    
    /**
     * Creates a new course with a specific ID (for repository loading).
     * @param id The course ID
     * @param name The course name
     * @param code The course code
     * @param instructor The instructor name
     * @param credits The number of credits
     */
    public Course(String id, String name, String code, String instructor, int credits) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.instructor = instructor;
        this.credits = credits;
    }
    
    /**
     * Creates a new course with a generated unique ID and default 3 credits.
     * @param name The course name
     * @param code The course code
     * @param instructor The instructor name
     */
    public Course(String name, String code, String instructor) {
        this(name, code, instructor, 3);
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getInstructor() { return instructor; }
    public int getCredits() { return credits; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public void setCredits(int credits) { this.credits = credits; }
    
    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", instructor='" + instructor + '\'' +
                ", credits=" + credits +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id.equals(course.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

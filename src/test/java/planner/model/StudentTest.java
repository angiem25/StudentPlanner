package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Student class.
 * Tests student data management and validation.
 */
class StudentTest {
    
    private Student student;
    
    @BeforeEach
    void setUp() {
        student = new Student("John", "Doe", "john@example.com", "S12345", 2, "Computer Science");
    }
    
    @Test
    @DisplayName("Should create student with valid data")
    void testStudentCreation() {
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals("john@example.com", student.getEmail());
        assertEquals("S12345", student.getStudentId());
        assertEquals(2, student.getYear());
        assertEquals("Computer Science", student.getMajor());
        assertNotNull(student.getId());
    }
    
    @Test
    @DisplayName("Should generate unique IDs for different students")
    void testUniqueIds() {
        Student student2 = new Student("Jane", "Smith", "jane@example.com", "S54321", 3, "Mathematics");
        assertNotEquals(student.getId(), student2.getId());
    }
    
    @Test
    @DisplayName("Should update student information")
    void testSetters() {
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setEmail("jane.smith@example.com");
        student.setStudentId("S54321");
        student.setYear(3);
        student.setMajor("Mathematics");
        
        assertEquals("Jane", student.getFirstName());
        assertEquals("Smith", student.getLastName());
        assertEquals("jane.smith@example.com", student.getEmail());
        assertEquals("S54321", student.getStudentId());
        assertEquals(3, student.getYear());
        assertEquals("Mathematics", student.getMajor());
    }
    
    @Test
    @DisplayName("Should handle toString correctly")
    void testToString() {
        String toString = student.toString();
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("john@example.com"));
    }
    
    @Test
    @DisplayName("Should implement equals based on ID")
    void testEquals() {
        Student sameStudent = new Student("John", "Doe", "john@example.com", "S12345", 2, "Computer Science");
        assertNotEquals(student, sameStudent); // Different IDs
        
        Student sameIdStudent = new Student("Different", "Name", "different@example.com", "S99999", 1, "Different");
        // Use reflection to set the same ID for testing
        try {
            java.lang.reflect.Field idField = Student.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(sameIdStudent, student.getId());
            assertEquals(student, sameIdStudent);
        } catch (Exception e) {
            fail("Could not test equals with same ID: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should implement hashCode based on ID")
    void testHashCode() {
        Student sameStudent = new Student("John", "Doe", "john@example.com", "S12345", 2, "Computer Science");
        assertNotEquals(student.hashCode(), sameStudent.hashCode()); // Different IDs
    }
    
    @Test
    @DisplayName("Should not equal null")
    void testNotEqualsNull() {
        assertNotEquals(student, null);
    }
    
    @Test
    @DisplayName("Should not equal different type")
    void testNotEqualsDifferentType() {
        assertNotEquals(student, "string");
    }
    
    @Test
    @DisplayName("Should equal itself")
    void testEqualsSelf() {
        assertEquals(student, student);
    }
}

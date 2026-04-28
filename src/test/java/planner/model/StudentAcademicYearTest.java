package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the AcademicYear enum functionality.
 */
class StudentAcademicYearTest {
    
    @Test
    void testAcademicYearEnumValues() {
        Student.AcademicYear[] years = Student.AcademicYear.values();
        assertEquals(4, years.length, "Should have exactly 4 academic years");
        
        // Test that all expected values exist
        assertTrue(containsYear(years, Student.AcademicYear.FRESHMAN), "Should contain Freshman");
        assertTrue(containsYear(years, Student.AcademicYear.SOPHOMORE), "Should contain Sophomore");
        assertTrue(containsYear(years, Student.AcademicYear.JUNIOR), "Should contain Junior");
        assertTrue(containsYear(years, Student.AcademicYear.SENIOR), "Should contain Senior");
    }
    
    @Test
    void testAcademicYearDisplayNames() {
        assertEquals("Freshman", Student.AcademicYear.FRESHMAN.getDisplayName());
        assertEquals("Sophomore", Student.AcademicYear.SOPHOMORE.getDisplayName());
        assertEquals("Junior", Student.AcademicYear.JUNIOR.getDisplayName());
        assertEquals("Senior", Student.AcademicYear.SENIOR.getDisplayName());
    }
    
    @Test
    void testAcademicYearNumericValues() {
        assertEquals(1, Student.AcademicYear.FRESHMAN.getNumericValue());
        assertEquals(2, Student.AcademicYear.SOPHOMORE.getNumericValue());
        assertEquals(3, Student.AcademicYear.JUNIOR.getNumericValue());
        assertEquals(4, Student.AcademicYear.SENIOR.getNumericValue());
    }
    
    @Test
    void testFromNumeric() {
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromNumeric(1));
        assertEquals(Student.AcademicYear.SOPHOMORE, Student.AcademicYear.fromNumeric(2));
        assertEquals(Student.AcademicYear.JUNIOR, Student.AcademicYear.fromNumeric(3));
        assertEquals(Student.AcademicYear.SENIOR, Student.AcademicYear.fromNumeric(4));
        
        // Test invalid values (should default to Freshman)
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromNumeric(0));
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromNumeric(5));
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromNumeric(-1));
    }
    
    @Test
    void testFromDisplayName() {
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromDisplayName("Freshman"));
        assertEquals(Student.AcademicYear.SOPHOMORE, Student.AcademicYear.fromDisplayName("Sophomore"));
        assertEquals(Student.AcademicYear.JUNIOR, Student.AcademicYear.fromDisplayName("Junior"));
        assertEquals(Student.AcademicYear.SENIOR, Student.AcademicYear.fromDisplayName("Senior"));
        
        // Test case insensitivity
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromDisplayName("freshman"));
        assertEquals(Student.AcademicYear.SOPHOMORE, Student.AcademicYear.fromDisplayName("SOPHOMORE"));
        
        // Test whitespace handling
        assertEquals(Student.AcademicYear.JUNIOR, Student.AcademicYear.fromDisplayName(" Junior "));
        
        // Test invalid values (should default to Freshman)
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromDisplayName(""));
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromDisplayName("Invalid"));
        assertEquals(Student.AcademicYear.FRESHMAN, Student.AcademicYear.fromDisplayName(null));
    }
    
    @Test
    void testToString() {
        assertEquals("Freshman", Student.AcademicYear.FRESHMAN.toString());
        assertEquals("Sophomore", Student.AcademicYear.SOPHOMORE.toString());
        assertEquals("Junior", Student.AcademicYear.JUNIOR.toString());
        assertEquals("Senior", Student.AcademicYear.SENIOR.toString());
    }
    
    @Test
    void testStudentWithAcademicYear() {
        Student student = new Student("John", "Doe", "john@example.com", "S12345", 
                                    Student.AcademicYear.JUNIOR, "Computer Science");
        
        assertEquals(Student.AcademicYear.JUNIOR, student.getAcademicYear());
        assertEquals(3, student.getYear()); // Backward compatibility
        assertEquals("Computer Science", student.getMajor());
    }
    
    @Test
    void testStudentSetAcademicYear() {
        Student student = new Student("Jane", "Smith", "jane@example.com", "S54321", "Mathematics");
        
        student.setAcademicYear(Student.AcademicYear.SENIOR);
        assertEquals(Student.AcademicYear.SENIOR, student.getAcademicYear());
        assertEquals(4, student.getYear()); // Backward compatibility
        
        // Test setting year as integer (backward compatibility)
        student.setYear(2);
        assertEquals(Student.AcademicYear.SOPHOMORE, student.getAcademicYear());
        assertEquals(2, student.getYear());
    }
    
    private boolean containsYear(Student.AcademicYear[] years, Student.AcademicYear year) {
        for (Student.AcademicYear y : years) {
            if (y == year) return true;
        }
        return false;
    }
}

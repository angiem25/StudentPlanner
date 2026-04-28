package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Course class.
 * Tests course data management and validation.
 */
class CourseTest {
    
    private Course course;
    
    @BeforeEach
    void setUp() {
        course = new Course("Introduction to Programming", "CS101", "Dr. Smith", 3);
    }
    
    @Test
    @DisplayName("Should create course with valid data")
    void testCourseCreation() {
        assertEquals("Introduction to Programming", course.getName());
        assertEquals("CS101", course.getCode());
        assertEquals("Dr. Smith", course.getInstructor());
        assertEquals(3, course.getCredits());
        assertNotNull(course.getId());
    }
    
    @Test
    @DisplayName("Should create course with default credits")
    void testCourseCreationDefaultCredits() {
        Course defaultCourse = new Course("Math", "MATH101", "Dr. Jones");
        assertEquals(3, defaultCourse.getCredits());
    }
    
    @Test
    @DisplayName("Should generate unique IDs for different courses")
    void testUniqueIds() {
        Course course2 = new Course("Advanced Programming", "CS102", "Dr. Johnson", 4);
        assertNotEquals(course.getId(), course2.getId());
    }
    
    @Test
    @DisplayName("Should update course information")
    void testSetters() {
        course.setName("Advanced Programming");
        course.setCode("CS102");
        course.setInstructor("Dr. Johnson");
        course.setCredits(4);
        
        assertEquals("Advanced Programming", course.getName());
        assertEquals("CS102", course.getCode());
        assertEquals("Dr. Johnson", course.getInstructor());
        assertEquals(4, course.getCredits());
    }
    
    @Test
    @DisplayName("Should handle toString correctly")
    void testToString() {
        String toString = course.toString();
        assertTrue(toString.contains("Introduction to Programming"));
        assertTrue(toString.contains("CS101"));
        assertTrue(toString.contains("Dr. Smith"));
        assertTrue(toString.contains("3"));
    }
    
    @Test
    @DisplayName("Should implement equals based on ID")
    void testEquals() {
        Course sameCourse = new Course("Introduction to Programming", "CS101", "Dr. Smith", 3);
        assertNotEquals(course, sameCourse); // Different IDs
        
        // Create a course with the same ID using the constructor that accepts ID
        Course sameIdCourse = new Course(course.getId(), "Different", "DIFF101", "Dr. Different", 4);
        assertEquals(course, sameIdCourse);
    }
    
    @Test
    @DisplayName("Should implement hashCode based on ID")
    void testHashCode() {
        Course sameCourse = new Course("Introduction to Programming", "CS101", "Dr. Smith", 3);
        assertNotEquals(course.hashCode(), sameCourse.hashCode()); // Different IDs
        
        Course sameIdCourse = new Course(course.getId(), "Different", "DIFF101", "Dr. Different", 4);
        assertEquals(course.hashCode(), sameIdCourse.hashCode());
    }
    
    @Test
    @DisplayName("Should not equal null")
    void testNotEqualsNull() {
        assertNotEquals(course, null);
    }
    
    @Test
    @DisplayName("Should not equal different type")
    void testNotEqualsDifferentType() {
        assertNotEquals(course, "string");
    }
    
    @Test
    @DisplayName("Should equal itself")
    void testEqualsSelf() {
        assertEquals(course, course);
    }
    
    @Test
    @DisplayName("Should handle course with ID constructor")
    void testCourseWithIdConstructor() {
        String testId = "test-id-123";
        Course courseWithId = new Course(testId, "Test Course", "TEST101", "Dr. Test", 2);
        
        assertEquals(testId, courseWithId.getId());
        assertEquals("Test Course", courseWithId.getName());
        assertEquals("TEST101", courseWithId.getCode());
        assertEquals("Dr. Test", courseWithId.getInstructor());
        assertEquals(2, courseWithId.getCredits());
    }
}

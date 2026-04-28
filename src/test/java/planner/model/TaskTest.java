package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Task class.
 * Tests task data management, priority, and completion status.
 */
class TaskTest {
    
    private Task task;
    private LocalDateTime testDueDate;
    
    @BeforeEach
    void setUp() {
        testDueDate = LocalDateTime.now().plusDays(7);
        task = new Task("Complete assignment", "Finish chapter 5", testDueDate, Task.Priority.HIGH);
    }
    
    @Test
    @DisplayName("Should create task with valid data")
    void testTaskCreation() {
        assertEquals("Complete assignment", task.getTitle());
        assertEquals("Finish chapter 5", task.getDescription());
        assertEquals(testDueDate, task.getDueDate());
        assertEquals(Task.Priority.HIGH, task.getPriority());
        assertFalse(task.isCompleted());
        assertNull(task.getCourseId());
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, task.getAccentColorHex());
        assertNotNull(task.getId());
    }
    
    @Test
    @DisplayName("Should create task with default priority")
    void testTaskCreationDefaultPriority() {
        Task defaultTask = new Task("Default task", "Description", testDueDate);
        assertEquals(Task.Priority.MEDIUM, defaultTask.getPriority());
    }
    
    @DisplayName("Should generate unique IDs for different tasks")
    void testUniqueIds() {
        Task task2 = new Task("Another task", "Description", testDueDate, Task.Priority.LOW);
        assertNotEquals(task.getId(), task2.getId());
    }
    
    @Test
    @DisplayName("Should update task information")
    void testSetters() {
        LocalDateTime newDueDate = testDueDate.plusDays(1);
        task.setTitle("Updated task");
        task.setDescription("Updated description");
        task.setDueDate(newDueDate);
        task.setPriority(Task.Priority.LOW);
        task.setCourseId("course-123");
        task.setAccentColorHex("#FF0000");
        
        assertEquals("Updated task", task.getTitle());
        assertEquals("Updated description", task.getDescription());
        assertEquals(newDueDate, task.getDueDate());
        assertEquals(Task.Priority.LOW, task.getPriority());
        assertEquals("course-123", task.getCourseId());
        assertEquals("#FF0000", task.getAccentColorHex());
    }
    
    @Test
    @DisplayName("Should handle task completion")
    void testTaskCompletion() {
        assertFalse(task.isCompleted());
        
        task.complete();
        assertTrue(task.isCompleted());
        
        task.incomplete();
        assertFalse(task.isCompleted());
    }
    
    @Test
    @DisplayName("Should handle accent color hex validation")
    void testAccentColorHexValidation() {
        // Valid hex colors
        task.setAccentColorHex("#FF0000");
        assertEquals("#FF0000", task.getAccentColorHex());
        
        task.setAccentColorHex("#00FF00");
        assertEquals("#00FF00", task.getAccentColorHex());
        
        // Invalid hex colors (should be trimmed and validated)
        task.setAccentColorHex("  #0000FF  ");
        assertEquals("#0000FF", task.getAccentColorHex());
        
        task.setAccentColorHex(null);
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, task.getAccentColorHex());
        
        task.setAccentColorHex("");
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, task.getAccentColorHex());
        
        task.setAccentColorHex("   ");
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, task.getAccentColorHex());
    }
    
    @Test
    @DisplayName("Should handle toString correctly")
    void testToString() {
        String toString = task.toString();
        assertTrue(toString.contains("Complete assignment"));
        assertTrue(toString.contains("Finish chapter 5"));
        assertTrue(toString.contains("HIGH"));
    }
    
    @Test
    @DisplayName("Should implement equals based on ID")
    void testEquals() {
        Task sameTask = new Task("Complete assignment", "Finish chapter 5", testDueDate, Task.Priority.HIGH);
        assertNotEquals(task, sameTask); // Different IDs
        
        // Create a task with the same ID using the constructor that accepts ID
        Task sameIdTask = new Task(task.getId(), "Different", "Description", testDueDate, Task.Priority.LOW);
        assertEquals(task, sameIdTask);
    }
    
    @Test
    @DisplayName("Should implement hashCode based on ID")
    void testHashCode() {
        Task sameTask = new Task("Complete assignment", "Finish chapter 5", testDueDate, Task.Priority.HIGH);
        assertNotEquals(task.hashCode(), sameTask.hashCode()); // Different IDs
        
        Task sameIdTask = new Task(task.getId(), "Different", "Description", testDueDate, Task.Priority.LOW);
        assertEquals(task.hashCode(), sameIdTask.hashCode());
    }
    
    @Test
    @DisplayName("Should not equal null")
    void testNotEqualsNull() {
        assertNotEquals(task, null);
    }
    
    @Test
    @DisplayName("Should not equal different type")
    void testNotEqualsDifferentType() {
        assertNotEquals(task, "string");
    }
    
    @Test
    @DisplayName("Should equal itself")
    void testEqualsSelf() {
        assertEquals(task, task);
    }
    
    @Test
    @DisplayName("Should handle task with ID constructor")
    void testTaskWithIdConstructor() {
        String testId = "test-task-id-123";
        Task taskWithId = new Task(testId, "Test Task", "Test Description", testDueDate, Task.Priority.MEDIUM);
        
        assertEquals(testId, taskWithId.getId());
        assertEquals("Test Task", taskWithId.getTitle());
        assertEquals("Test Description", taskWithId.getDescription());
        assertEquals(testDueDate, taskWithId.getDueDate());
        assertEquals(Task.Priority.MEDIUM, taskWithId.getPriority());
        assertFalse(taskWithId.isCompleted());
    }
    
    @Test
    @DisplayName("Should test priority enum values")
    void testPriorityEnum() {
        assertEquals(3, Task.Priority.values().length);
        assertTrue(java.util.Arrays.asList(Task.Priority.values()).contains(Task.Priority.LOW));
        assertTrue(java.util.Arrays.asList(Task.Priority.values()).contains(Task.Priority.MEDIUM));
        assertTrue(java.util.Arrays.asList(Task.Priority.values()).contains(Task.Priority.HIGH));
    }
}

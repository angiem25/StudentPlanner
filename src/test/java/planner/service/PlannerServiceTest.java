package planner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import planner.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for PlannerService class.
 * Tests business logic, validation, and service layer operations.
 */
class PlannerServiceTest {
    
    private PlannerModel model;
    private PlannerService service;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
        service = new PlannerService(model);
    }
    
    // Student Profile Tests
    @Test
    @DisplayName("Should create student profile with valid data")
    void testCreateStudentProfile() {
        service.createStudentProfile("John", "Doe", "john@example.com", 
                                     "S12345", 2, "Computer Science");
        
        Student student = model.getCurrentStudent();
        assertNotNull(student);
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals("john@example.com", student.getEmail());
        assertEquals("S12345", student.getStudentId());
        assertEquals(2, student.getYear());
        assertEquals("Computer Science", student.getMajor());
    }
    
    @Test
    @DisplayName("Should reject empty first name")
    void testCreateStudentProfileEmptyFirstName() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createStudentProfile("", "Doe", "john@example.com", 
                                         "S12345", 2, "Computer Science");
        });
    }
    
    @Test
    @DisplayName("Should reject invalid email")
    void testCreateStudentProfileInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createStudentProfile("John", "Doe", "invalid-email", 
                                         "S12345", 2, "Computer Science");
        });
    }
    
    @Test
    @DisplayName("Should reject year outside valid range")
    void testCreateStudentProfileInvalidYear() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createStudentProfile("John", "Doe", "john@example.com", 
                                         "S12345", 7, "Computer Science");
        });
    }
    
    // Course Operations Tests
    @Test
    @DisplayName("Should add course with valid data")
    void testAddCourse() {
        Course course = service.addCourse("Intro to Programming", "CS101", 
                                         "Dr. Smith", 3);
        
        assertNotNull(course);
        assertEquals("Intro to Programming", course.getName());
        assertEquals("CS101", course.getCode());
        assertEquals(1, model.getCourses().size());
    }
    
    @Test
    @DisplayName("Should reject duplicate course code")
    void testAddCourseDuplicateCode() {
        service.addCourse("Intro to Programming", "CS101", "Dr. Smith", 3);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.addCourse("Advanced Programming", "CS101", "Dr. Jones", 4);
        });
    }
    
    @Test
    @DisplayName("Should reject empty course name")
    void testAddCourseEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addCourse("", "CS101", "Dr. Smith", 3);
        });
    }
    
    @Test
    @DisplayName("Should reject credits outside valid range")
    void testAddCourseInvalidCredits() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addCourse("Course", "CS101", "Dr. Smith", 7);
        });
    }
    
    @Test
    @DisplayName("Should update existing course")
    void testUpdateCourse() {
        Course course = service.addCourse("Intro", "CS101", "Dr. Smith", 3);
        service.updateCourse(course.getId(), "Advanced", "CS102", "Dr. Jones", 4);
        
        var updated = model.getCourseById(course.getId());
        assertTrue(updated.isPresent());
        assertEquals("Advanced", updated.get().getName());
        assertEquals("CS102", updated.get().getCode());
    }
    
    @Test
    @DisplayName("Should reject updating with duplicate code")
    void testUpdateCourseDuplicateCode() {
        service.addCourse("Course 1", "CS101", "Dr. Smith", 3);
        Course course2 = service.addCourse("Course 2", "CS102", "Dr. Jones", 3);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.updateCourse(course2.getId(), "Course 2", "CS101", "Dr. Jones", 3);
        });
    }
    
    @Test
    @DisplayName("Should remove course by ID")
    void testRemoveCourse() {
        Course course = service.addCourse("Course", "CS101", "Dr. Smith", 3);
        service.removeCourse(course.getId());
        
        assertEquals(0, model.getCourses().size());
    }
    
    @Test
    @DisplayName("Should reject removing non-existent course")
    void testRemoveNonExistentCourse() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.removeCourse("non-existent-id");
        });
    }
    
    // Task Operations Tests
    @Test
    @DisplayName("Should add task with valid data")
    void testAddTask() {
        Course course = service.addCourse("Course", "CS101", "Dr. Smith", 3);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        
        Task task = service.addTask("Complete assignment", "Finish chapter 5", 
                                    dueDate, Task.Priority.HIGH, course.getId(), "#FF0000");
        
        assertNotNull(task);
        assertEquals("Complete assignment", task.getTitle());
        assertEquals(course.getId(), task.getCourseId());
        assertEquals(1, model.getTasks().size());
    }
    
    @Test
    @DisplayName("Should add task without course")
    void testAddTaskWithoutCourse() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        
        Task task = service.addTaskWithoutCourse("Personal task", "Description", 
                                                 dueDate, Task.Priority.MEDIUM);
        
        assertNotNull(task);
        assertNull(task.getCourseId());
        assertEquals(1, model.getTasks().size());
    }
    
    @Test
    @DisplayName("Should reject task with non-existent course")
    void testAddTaskInvalidCourse() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.addTask("Task", "Description", dueDate, 
                           Task.Priority.HIGH, "invalid-course-id", "#FF0000");
        });
    }
    
    @Test
    @DisplayName("Should reject empty task title")
    void testAddTaskEmptyTitle() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.addTask("", "Description", dueDate, Task.Priority.HIGH, null, "#FF0000");
        });
    }
    
    @Test
    @DisplayName("Should reject past due date")
    void testAddTaskPastDueDate() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.addTask("Task", "Description", pastDate, Task.Priority.HIGH, null, "#FF0000");
        });
    }
    
    @Test
    @DisplayName("Should update existing task")
    void testUpdateTask() {
        Course course = service.addCourse("Course", "CS101", "Dr. Smith", 3);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        Task task = service.addTask("Task", "Description", dueDate, 
                                    Task.Priority.HIGH, course.getId(), "#FF0000");
        
        LocalDateTime newDueDate = LocalDateTime.now().plusDays(10);
        service.updateTask(task.getId(), "Updated", "New description", 
                          newDueDate, Task.Priority.LOW, course.getId(), "#00FF00");
        
        var updated = model.getTaskById(task.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated", updated.get().getTitle());
        assertEquals(Task.Priority.LOW, updated.get().getPriority());
    }
    
    @Test
    @DisplayName("Should toggle task completion")
    void testToggleTaskCompletion() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        Task task = service.addTask("Task", "Description", dueDate, 
                                    Task.Priority.HIGH, null, "#FF0000");
        
        service.toggleTaskCompletion(task.getId());
        assertTrue(model.getTaskById(task.getId()).get().isCompleted());
        
        service.toggleTaskCompletion(task.getId());
        assertFalse(model.getTaskById(task.getId()).get().isCompleted());
    }
    
    @Test
    @DisplayName("Should remove task by ID")
    void testRemoveTask() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        Task task = service.addTask("Task", "Description", dueDate, 
                                    Task.Priority.HIGH, null, "#FF0000");
        
        service.removeTask(task.getId());
        assertEquals(0, model.getTasks().size());
    }
    
    // Query Operations Tests
    @Test
    @DisplayName("Should get upcoming tasks within date range")
    void testGetUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        service.addTask("Task 1", "Desc", now.plusDays(1), Task.Priority.HIGH, null, "#FF0000");
        service.addTask("Task 2", "Desc", now.plusDays(3), Task.Priority.MEDIUM, null, "#FF0000");
        service.addTask("Task 3", "Desc", now.plusDays(10), Task.Priority.LOW, null, "#FF0000");
        
        List<Task> upcoming = service.getUpcomingTasks(7);
        assertEquals(2, upcoming.size());
    }
    
    @Test
    @DisplayName("Should get overdue tasks")
    void testGetOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        // Create a task with a future date, then manually set it to be overdue
        Task overdueTask = new Task("Overdue", "Desc", now.plusDays(1), Task.Priority.HIGH);
        overdueTask.setDueDate(now.minusDays(1)); // Manually set to past date
        model.addTask(overdueTask);
        
        service.addTask("Future", "Desc", now.plusDays(2), Task.Priority.MEDIUM, null, "#FF0000");
        
        List<Task> overdue = service.getOverdueTasks();
        assertEquals(1, overdue.size());
        assertEquals("Overdue", overdue.get(0).getTitle());
    }
    
    @Test
    @DisplayName("Should get tasks by priority")
    void testGetTasksByPriority() {
        service.addTask("High", "Desc", LocalDateTime.now().plusDays(1), 
                       Task.Priority.HIGH, null, "#FF0000");
        service.addTask("High 2", "Desc", LocalDateTime.now().plusDays(2), 
                       Task.Priority.HIGH, null, "#FF0000");
        service.addTask("Low", "Desc", LocalDateTime.now().plusDays(1), 
                       Task.Priority.LOW, null, "#FF0000");
        
        List<Task> highPriority = service.getTasksByPriority(Task.Priority.HIGH);
        assertEquals(2, highPriority.size());
    }
    
    // Validation Edge Cases
    @Test
    @DisplayName("Should reject null priority")
    void testAddTaskNullPriority() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addTask("Task", "Description", LocalDateTime.now().plusDays(7), 
                           null, null, "#FF0000");
        });
    }
    
    @Test
    @DisplayName("Should accept whitespace-only strings as empty")
    void testCreateStudentProfileWhitespace() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createStudentProfile("   ", "Doe", "john@example.com", 
                                         "S12345", 2, "Computer Science");
        });
    }
    
    @Test
    @DisplayName("Should reject null task description")
    void testAddTaskNullDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addTask("Task", null, LocalDateTime.now().plusDays(7), 
                           Task.Priority.HIGH, null, "#FF0000");
        });
    }
}

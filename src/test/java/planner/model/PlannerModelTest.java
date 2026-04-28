package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for PlannerModel class.
 * Tests CRUD operations for students, courses, tasks, and events.
 */
class PlannerModelTest {
    
    private PlannerModel model;
    private Student testStudent;
    private Course testCourse;
    private Task testTask;
    private Event testEvent;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
        testStudent = new Student("John", "Doe", "john@example.com", "S12345", 2, "Computer Science");
        testCourse = new Course("Introduction to Programming", "CS101", "Dr. Smith", 3);
        testTask = new Task("Complete assignment", "Finish chapter 5", 
                           LocalDateTime.now().plusDays(7), Task.Priority.HIGH);
        testEvent = new Event("Study Group", "Group meeting for CS101",
                             LocalDateTime.now().plusDays(1).withHour(14),
                             LocalDateTime.now().plusDays(1).withHour(16));
    }
    
    // Student Management Tests
    @Test
    @DisplayName("Should set current student and fire notification")
    void testSetCurrentStudent() {
        model.setCurrentStudent(testStudent);
        assertEquals(testStudent, model.getCurrentStudent());
    }
    
    @Test
    @DisplayName("Should allow setting student to null")
    void testSetNullStudent() {
        model.setCurrentStudent(testStudent);
        model.setCurrentStudent(null);
        assertNull(model.getCurrentStudent());
    }
    
    // Course Management Tests
    @Test
    @DisplayName("Should add course successfully")
    void testAddCourse() {
        model.addCourse(testCourse);
        assertEquals(1, model.getCourses().size());
        assertTrue(model.getCourses().contains(testCourse));
    }
    
    @Test
    @DisplayName("Should not add duplicate course")
    void testAddDuplicateCourse() {
        model.addCourse(testCourse);
        model.addCourse(testCourse);
        assertEquals(1, model.getCourses().size());
    }
    
    @Test
    @DisplayName("Should not add null course")
    void testAddNullCourse() {
        model.addCourse(null);
        assertEquals(0, model.getCourses().size());
    }
    
    @Test
    @DisplayName("Should find course by ID")
    void testGetCourseById() {
        model.addCourse(testCourse);
        var found = model.getCourseById(testCourse.getId());
        assertTrue(found.isPresent());
        assertEquals(testCourse, found.get());
    }
    
    @Test
    @DisplayName("Should find course by code (case insensitive)")
    void testGetCourseByCode() {
        model.addCourse(testCourse);
        var found = model.getCourseByCode("cs101");
        assertTrue(found.isPresent());
        assertEquals(testCourse, found.get());
    }
    
    @Test
    @DisplayName("Should remove course and associated tasks")
    void testRemoveCourse() {
        model.addCourse(testCourse);
        testTask.setCourseId(testCourse.getId());
        model.addTask(testTask);
        
        model.removeCourse(testCourse);
        
        assertEquals(0, model.getCourses().size());
        assertEquals(0, model.getTasks().size());
    }
    
    @Test
    @DisplayName("Should update existing course")
    void testUpdateCourse() {
        model.addCourse(testCourse);
        testCourse.setName("Advanced Programming");
        model.updateCourse(testCourse);
        
        var updated = model.getCourseById(testCourse.getId());
        assertTrue(updated.isPresent());
        assertEquals("Advanced Programming", updated.get().getName());
    }
    
    // Task Management Tests
    @Test
    @DisplayName("Should add task successfully")
    void testAddTask() {
        model.addTask(testTask);
        assertEquals(1, model.getTasks().size());
        assertTrue(model.getTasks().contains(testTask));
    }
    
    @Test
    @DisplayName("Should not add duplicate task")
    void testAddDuplicateTask() {
        model.addTask(testTask);
        model.addTask(testTask);
        assertEquals(1, model.getTasks().size());
    }
    
    @Test
    @DisplayName("Should get tasks for specific course")
    void testGetTasksForCourse() {
        model.addCourse(testCourse);
        testTask.setCourseId(testCourse.getId());
        model.addTask(testTask);
        
        Task unrelatedTask = new Task("Other task", "Description", 
                                      LocalDateTime.now().plusDays(5), Task.Priority.LOW);
        model.addTask(unrelatedTask);
        
        List<Task> courseTasks = model.getTasksForCourse(testCourse.getId());
        assertEquals(1, courseTasks.size());
        assertEquals(testTask, courseTasks.get(0));
    }
    
    @Test
    @DisplayName("Should get completed tasks")
    void testGetCompletedTasks() {
        model.addTask(testTask);
        testTask.complete();
        
        Task incompleteTask = new Task("Incomplete", "Desc", 
                                       LocalDateTime.now().plusDays(3), Task.Priority.MEDIUM);
        model.addTask(incompleteTask);
        
        List<Task> completed = model.getCompletedTasks();
        assertEquals(1, completed.size());
        assertTrue(completed.get(0).isCompleted());
    }
    
    @Test
    @DisplayName("Should get incomplete tasks")
    void testGetIncompleteTasks() {
        model.addTask(testTask);
        Task completedTask = new Task("Complete", "Desc", 
                                       LocalDateTime.now().plusDays(3), Task.Priority.MEDIUM);
        completedTask.complete();
        model.addTask(completedTask);
        
        List<Task> incomplete = model.getIncompleteTasks();
        assertEquals(1, incomplete.size());
        assertFalse(incomplete.get(0).isCompleted());
    }
    
    @Test
    @DisplayName("Should mark task as completed")
    void testCompleteTask() {
        model.addTask(testTask);
        model.completeTask(testTask);
        assertTrue(testTask.isCompleted());
    }
    
    @Test
    @DisplayName("Should mark task as incomplete")
    void testIncompleteTask() {
        model.addTask(testTask);
        testTask.complete();
        model.incompleteTask(testTask);
        assertFalse(testTask.isCompleted());
    }
    
    @Test
    @DisplayName("Should remove task")
    void testRemoveTask() {
        model.addTask(testTask);
        model.removeTask(testTask);
        assertEquals(0, model.getTasks().size());
    }
    
    @Test
    @DisplayName("Should update task")
    void testUpdateTask() {
        model.addTask(testTask);
        testTask.setTitle("Updated title");
        model.updateTask(testTask);
        
        var updated = model.getTaskById(testTask.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated title", updated.get().getTitle());
    }
    
    // Event Management Tests
    @Test
    @DisplayName("Should add event successfully")
    void testAddEvent() {
        model.addEvent(testEvent);
        assertEquals(1, model.getEvents().size());
        assertTrue(model.getEvents().contains(testEvent));
    }
    
    @Test
    @DisplayName("Should not add duplicate event")
    void testAddDuplicateEvent() {
        model.addEvent(testEvent);
        model.addEvent(testEvent);
        assertEquals(1, model.getEvents().size());
    }
    
    @Test
    @DisplayName("Should get events for specific date")
    void testGetEventsForDate() {
        LocalDate eventDate = testEvent.getStartDateTime().toLocalDate();
        model.addEvent(testEvent);
        
        Event otherEvent = new Event("Other", "Desc", 
                                     eventDate.plusDays(1).atTime(10, 0),
                                     eventDate.plusDays(1).atTime(12, 0));
        model.addEvent(otherEvent);
        
        List<Event> dateEvents = model.getEventsForDate(eventDate);
        assertEquals(1, dateEvents.size());
        assertEquals(testEvent, dateEvents.get(0));
    }
    
    @Test
    @DisplayName("Should get events in date range")
    void testGetEventsInRange() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        
        model.addEvent(testEvent);
        Event outsideEvent = new Event("Outside", "Desc", 
                                     end.plusDays(1).withHour(10),
                                     end.plusDays(1).withHour(12));
        model.addEvent(outsideEvent);
        
        List<Event> rangeEvents = model.getEventsInRange(start, end);
        assertEquals(1, rangeEvents.size());
        assertEquals(testEvent, rangeEvents.get(0));
    }
    
    @Test
    @DisplayName("Should remove event")
    void testRemoveEvent() {
        model.addEvent(testEvent);
        model.removeEvent(testEvent);
        assertEquals(0, model.getEvents().size());
    }
    
    @Test
    @DisplayName("Should update event")
    void testUpdateEvent() {
        model.addEvent(testEvent);
        testEvent.setTitle("Updated event");
        model.updateEvent(testEvent);
        
        var updated = model.getEventById(testEvent.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated event", updated.get().getTitle());
    }
    
    // Clear All Tests
    @Test
    @DisplayName("Should clear all data")
    void testClearAll() {
        model.setCurrentStudent(testStudent);
        model.addCourse(testCourse);
        model.addTask(testTask);
        model.addEvent(testEvent);
        
        model.clearAll();
        
        assertNull(model.getCurrentStudent());
        assertEquals(0, model.getCourses().size());
        assertEquals(0, model.getTasks().size());
        assertEquals(0, model.getEvents().size());
    }
    
    // Defensive Copy Tests
    @Test
    @DisplayName("Should return defensive copy of courses list")
    void testCoursesDefensiveCopy() {
        model.addCourse(testCourse);
        List<Course> courses = model.getCourses();
        courses.clear();
        
        assertEquals(1, model.getCourses().size());
    }
    
    @Test
    @DisplayName("Should return defensive copy of tasks list")
    void testTasksDefensiveCopy() {
        model.addTask(testTask);
        List<Task> tasks = model.getTasks();
        tasks.clear();
        
        assertEquals(1, model.getTasks().size());
    }
    
    @Test
    @DisplayName("Should return defensive copy of events list")
    void testEventsDefensiveCopy() {
        model.addEvent(testEvent);
        List<Event> events = model.getEvents();
        events.clear();
        
        assertEquals(1, model.getEvents().size());
    }
}

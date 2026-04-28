package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Today tab functionality in PlannerModel.
 */
class PlannerModelTodayTest {
    
    private PlannerModel model;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
    }
    
    @Test
    void testGetTasksForToday_EmptyList() {
        List<Task> todayTasks = model.getTasksForToday();
        assertTrue(todayTasks.isEmpty(), "Should return empty list when no tasks exist");
    }
    
    @Test
    void testGetTasksForToday_NoTasksForToday() {
        // Add tasks for different days
        Task yesterdayTask = new Task("Yesterday Task", "Due yesterday", 
            LocalDateTime.now().minusDays(1), Task.Priority.LOW);
        Task tomorrowTask = new Task("Tomorrow Task", "Due tomorrow", 
            LocalDateTime.now().plusDays(1), Task.Priority.MEDIUM);
        
        model.addTask(yesterdayTask);
        model.addTask(tomorrowTask);
        
        List<Task> todayTasks = model.getTasksForToday();
        assertTrue(todayTasks.isEmpty(), "Should return empty list when no tasks are due today");
    }
    
    @Test
    void testGetTasksForToday_WithTasksForToday() {
        LocalDate today = LocalDate.now();
        
        // Add tasks for today
        Task todayTask1 = new Task("Today Task 1", "Due today morning", 
            today.atTime(9, 0), Task.Priority.HIGH);
        Task todayTask2 = new Task("Today Task 2", "Due today afternoon", 
            today.atTime(14, 30), Task.Priority.MEDIUM);
        
        // Add tasks for other days
        Task yesterdayTask = new Task("Yesterday Task", "Due yesterday", 
            today.minusDays(1).atTime(10, 0), Task.Priority.LOW);
        Task tomorrowTask = new Task("Tomorrow Task", "Due tomorrow", 
            today.plusDays(1).atTime(11, 0), Task.Priority.LOW);
        
        model.addTask(todayTask1);
        model.addTask(todayTask2);
        model.addTask(yesterdayTask);
        model.addTask(tomorrowTask);
        
        List<Task> todayTasks = model.getTasksForToday();
        
        assertEquals(2, todayTasks.size(), "Should return exactly 2 tasks for today");
        assertTrue(todayTasks.contains(todayTask1), "Should contain first today task");
        assertTrue(todayTasks.contains(todayTask2), "Should contain second today task");
        assertFalse(todayTasks.contains(yesterdayTask), "Should not contain yesterday task");
        assertFalse(todayTasks.contains(tomorrowTask), "Should not contain tomorrow task");
    }
    
    @Test
    void testGetTasksForToday_SortedByTime() {
        LocalDate today = LocalDate.now();
        
        // Add tasks for today in random order
        Task afternoonTask = new Task("Afternoon Task", "Due afternoon", 
            today.atTime(15, 0), Task.Priority.MEDIUM);
        Task morningTask = new Task("Morning Task", "Due morning", 
            today.atTime(9, 0), Task.Priority.HIGH);
        Task eveningTask = new Task("Evening Task", "Due evening", 
            today.atTime(20, 0), Task.Priority.LOW);
        
        model.addTask(afternoonTask);
        model.addTask(eveningTask);
        model.addTask(morningTask);
        
        List<Task> todayTasks = model.getTasksForToday();
        
        assertEquals(3, todayTasks.size(), "Should return exactly 3 tasks");
        assertEquals(morningTask, todayTasks.get(0), "First task should be morning task");
        assertEquals(afternoonTask, todayTasks.get(1), "Second task should be afternoon task");
        assertEquals(eveningTask, todayTasks.get(2), "Third task should be evening task");
    }
    
    @Test
    void testGetTasksForToday_IncludesCompletedTasks() {
        LocalDate today = LocalDate.now();
        
        Task incompleteTask = new Task("Incomplete Task", "Not done yet", 
            today.atTime(10, 0), Task.Priority.HIGH);
        Task completedTask = new Task("Completed Task", "Already done", 
            today.atTime(14, 0), Task.Priority.MEDIUM);
        
        completedTask.complete();
        
        model.addTask(incompleteTask);
        model.addTask(completedTask);
        
        List<Task> todayTasks = model.getTasksForToday();
        
        assertEquals(2, todayTasks.size(), "Should include both completed and incomplete tasks");
        assertTrue(todayTasks.contains(incompleteTask), "Should contain incomplete task");
        assertTrue(todayTasks.contains(completedTask), "Should contain completed task");
    }
    
    @Test
    void testGetTasksForToday_WithNullDueDate() {
        // Add a task with null due date
        Task nullDueDateTask = new Task("No Due Date Task", "No due date set", 
            null, Task.Priority.MEDIUM);
        
        model.addTask(nullDueDateTask);
        
        List<Task> todayTasks = model.getTasksForToday();
        
        assertTrue(todayTasks.isEmpty(), "Should not include tasks with null due date");
    }
    
    @Test
    void testGetTasksForToday_MidnightTasks() {
        LocalDate today = LocalDate.now();
        
        // Task due exactly at midnight (should be included)
        Task midnightTask = new Task("Midnight Task", "Due at midnight", 
            today.atStartOfDay(), Task.Priority.HIGH);
        
        model.addTask(midnightTask);
        
        List<Task> todayTasks = model.getTasksForToday();
        
        assertEquals(1, todayTasks.size(), "Should include task due at midnight");
        assertTrue(todayTasks.contains(midnightTask), "Should contain midnight task");
    }
}

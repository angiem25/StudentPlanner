package planner.service;

import planner.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer that provides business logic for the student planner.
 * Encapsulates complex operations and validation rules.
 */
public class PlannerService {
    private final PlannerModel model;
    
    /**
     * Creates a new PlannerService with the specified model.
     * @param model The planner model to operate on
     */
    public PlannerService(PlannerModel model) {
        this.model = model;
    }
    
    // Student operations
    public void createStudentProfile(String firstName, String lastName, String email, 
                                   String studentId, int year, String major) {
        validateStudentData(firstName, lastName, email, studentId, year, major);
        Student student = new Student(firstName, lastName, email, studentId, year, major);
        model.setCurrentStudent(student);
    }
    
    public void updateStudentProfile(Student student) {
        validateStudentData(student.getFirstName(), student.getLastName(), 
                          student.getEmail(), student.getStudentId(), 
                          student.getYear(), student.getMajor());
        model.updateCourse(null); // Trigger update notification
    }
    
    // Course operations
    public Course addCourse(String name, String code, String instructor, int credits) {
        validateCourseData(name, code, instructor, credits);
        
        // Check for duplicate course code
        if (model.getCourseByCode(code).isPresent()) {
            throw new IllegalArgumentException("Course with code " + code + " already exists");
        }
        
        Course course = new Course(name, code, instructor, credits);
        model.addCourse(course);
        return course;
    }
    
    public void updateCourse(String courseId, String name, String code, String instructor, int credits) {
        validateCourseData(name, code, instructor, credits);
        
        Optional<Course> courseOpt = model.getCourseById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            
            // Check if new code conflicts with existing course (excluding this one)
            if (!course.getCode().equals(code)) {
                if (model.getCourseByCode(code).isPresent()) {
                    throw new IllegalArgumentException("Course with code " + code + " already exists");
                }
            }
            
            course.setName(name);
            course.setCode(code);
            course.setInstructor(instructor);
            course.setCredits(credits);
            model.updateCourse(course);
        } else {
            throw new IllegalArgumentException("Course not found: " + courseId);
        }
    }
    
    public void removeCourse(String courseId) {
        Optional<Course> courseOpt = model.getCourseById(courseId);
        if (courseOpt.isPresent()) {
            model.removeCourse(courseOpt.get());
        } else {
            throw new IllegalArgumentException("Course not found: " + courseId);
        }
    }
    
    // Task operations
    public Task addTask(String title, String description, LocalDateTime dueDate, 
                       Task.Priority priority, String courseId) {
        validateTaskData(title, description, dueDate, priority, courseId);
        
        // Verify course exists if courseId is provided
        if (courseId != null && !courseId.trim().isEmpty()) {
            if (model.getCourseById(courseId).isEmpty()) {
                throw new IllegalArgumentException("Course not found: " + courseId);
            }
        }
        
        Task task = new Task(title, description, dueDate, priority);
        task.setCourseId(courseId);
        model.addTask(task);
        return task;
    }
    
    public Task addTaskWithoutCourse(String title, String description, 
                                    LocalDateTime dueDate, Task.Priority priority) {
        return addTask(title, description, dueDate, priority, null);
    }
    
    public void updateTask(String taskId, String title, String description, 
                          LocalDateTime dueDate, Task.Priority priority, String courseId) {
        validateTaskData(title, description, dueDate, priority, courseId);
        
        Optional<Task> taskOpt = model.getTaskById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            
            // Verify course exists if courseId is provided
            if (courseId != null && !courseId.trim().isEmpty()) {
                if (model.getCourseById(courseId).isEmpty()) {
                    throw new IllegalArgumentException("Course not found: " + courseId);
                }
            }
            
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            task.setCourseId(courseId);
            model.updateTask(task);
        } else {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
    }
    
    public void removeTask(String taskId) {
        Optional<Task> taskOpt = model.getTaskById(taskId);
        if (taskOpt.isPresent()) {
            model.removeTask(taskOpt.get());
        } else {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
    }
    
    public void toggleTaskCompletion(String taskId) {
        Optional<Task> taskOpt = model.getTaskById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (task.isCompleted()) {
                model.incompleteTask(task);
            } else {
                model.completeTask(task);
            }
        } else {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
    }
    
    // Query operations
    public List<Task> getUpcomingTasks(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        
        return model.getIncompleteTasks().stream()
                .filter(task -> task.getDueDate().isAfter(now) && task.getDueDate().isBefore(future))
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .toList();
    }
    
    public List<Task> getOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        return model.getIncompleteTasks().stream()
                .filter(task -> task.getDueDate().isBefore(now))
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .toList();
    }
    
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return model.getTasks().stream()
                .filter(task -> task.getPriority() == priority)
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .toList();
    }
    
    // Validation methods
    private void validateStudentData(String firstName, String lastName, String email, 
                                   String studentId, int year, String major) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        if (year < 1 || year > 6) {
            throw new IllegalArgumentException("Year must be between 1 and 6");
        }
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException("Major cannot be empty");
        }
    }
    
    private void validateCourseData(String name, String code, String instructor, int credits) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (instructor == null || instructor.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor name cannot be empty");
        }
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("Credits must be between 1 and 6");
        }
    }
    
    private void validateTaskData(String title, String description, LocalDateTime dueDate, 
                                Task.Priority priority, String courseId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        if (dueDate == null || dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date must be in the future");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Task priority cannot be null");
        }
    }
}

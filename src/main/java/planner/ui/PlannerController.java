package planner.ui;

import mvc.AbstractController;
import planner.model.*;
import planner.service.PlannerService;
import java.time.LocalDateTime;

/**
 * Controller for the student planner application.
 * Handles user interactions and coordinates between the view and service layer.
 */
public class PlannerController extends AbstractController {
    private final PlannerService service;
    
    /**
     * Creates a new PlannerController with the specified model, view, and service.
     * @param model The planner model
     * @param view The planner view
     * @param service The planner service
     */
    public PlannerController(PlannerModel model, PlannerView view, PlannerService service) {
        super(model, view);
        this.service = service;
    }
    
    /**
     * Saves the student profile.
     * @param firstName The first name
     * @param lastName The last name
     * @param email The email address
     * @param studentId The student ID
     * @param yearText The year as text
     * @param major The major field of study
     */
    public void saveStudentProfile(String firstName, String lastName, String email, 
                                  String studentId, String yearText, String major) {
        try {
            int year = Integer.parseInt(yearText);
            service.createStudentProfile(firstName, lastName, email, studentId, year, major);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Student profile saved successfully!");
        } catch (NumberFormatException e) {
            PlannerView view = (PlannerView) getView();
            view.showError("Year must be a valid number");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Adds a new course.
     * @param name The course name
     * @param code The course code
     * @param instructor The instructor name
     * @param creditsText The credits as text
     */
    public void addCourse(String name, String code, String instructor, String creditsText) {
        try {
            int credits = Integer.parseInt(creditsText);
            service.addCourse(name, code, instructor, credits);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Course added successfully!");
        } catch (NumberFormatException e) {
            PlannerView view = (PlannerView) getView();
            view.showError("Credits must be a valid number");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Updates an existing course.
     * @param courseId The course ID to update
     * @param name The new course name
     * @param code The new course code
     * @param instructor The new instructor name
     * @param creditsText The new credits as text
     */
    public void updateCourse(String courseId, String name, String code, 
                           String instructor, String creditsText) {
        try {
            int credits = Integer.parseInt(creditsText);
            service.updateCourse(courseId, name, code, instructor, credits);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Course updated successfully!");
        } catch (NumberFormatException e) {
            PlannerView view = (PlannerView) getView();
            view.showError("Credits must be a valid number");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Removes a course.
     * @param courseId The course ID to remove
     */
    public void removeCourse(String courseId) {
        try {
            service.removeCourse(courseId);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Course removed successfully!");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Adds a new task.
     * @param title The task title
     * @param description The task description
     * @param dueDate The due date and time
     * @param priority The task priority
     * @param courseId The course ID (can be null)
     */
    public void addTask(String title, String description, LocalDateTime dueDate,
                       Task.Priority priority, String courseId) {
        try {
            service.addTask(title, description, dueDate, priority, courseId);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Task added successfully!");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Updates an existing task.
     * @param taskId The task ID to update
     * @param title The new task title
     * @param description The new task description
     * @param dueDate The new due date and time
     * @param priority The new task priority
     * @param courseId The new course ID (can be null)
     */
    public void updateTask(String taskId, String title, String description,
                         LocalDateTime dueDate, Task.Priority priority, String courseId) {
        try {
            service.updateTask(taskId, title, description, dueDate, priority, courseId);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Task updated successfully!");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Removes a task.
     * @param taskId The task ID to remove
     */
    public void removeTask(String taskId) {
        try {
            service.removeTask(taskId);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Task removed successfully!");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Toggles the completion status of a task.
     * @param taskId The task ID to toggle
     */
    public void toggleTaskCompletion(String taskId) {
        try {
            service.toggleTaskCompletion(taskId);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Task status updated successfully!");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
}

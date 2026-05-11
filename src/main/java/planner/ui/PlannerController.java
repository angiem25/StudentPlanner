package planner.ui;

import mvc.AbstractController;
import planner.model.*;
import planner.service.PlannerService;
import java.io.IOException;
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
     * @param academicYear The academic year
     * @param major The major field of study
     */
    public void saveStudentProfile(String firstName, String lastName, String email, 
                                  String studentId, Student.AcademicYear academicYear, String major) {
        try {
            service.createStudentProfile(firstName, lastName, email, studentId, academicYear, major);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Student profile saved successfully!");
        } catch (IllegalArgumentException e) {
            PlannerView view = (PlannerView) getView();
            view.showError(e.getMessage());
        }
    }
    
    /**
     * Saves the student profile with year as string for backward compatibility.
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
            Student.AcademicYear academicYear = Student.AcademicYear.fromDisplayName(yearText);
            service.createStudentProfile(firstName, lastName, email, studentId, academicYear, major);
            PlannerView view = (PlannerView) getView();
            view.showInfo("Student profile saved successfully!");
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
                       Task.Priority priority, String courseId, String accentColorHex) {
        try {
            service.addTask(title, description, dueDate, priority, courseId, accentColorHex);
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
                         LocalDateTime dueDate, Task.Priority priority, String courseId,
                         String accentColorHex) {
        try {
            service.updateTask(taskId, title, description, dueDate, priority, courseId, accentColorHex);
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

    /**
     * Toggles task completion without success popup, for checklist-style interactions.
     */
    public void toggleTaskCompletionSilently(String taskId) {
        service.toggleTaskCompletion(taskId);
    }
    
    /**
     * Exports the current planner data to files.
     * @throws IOException If an I/O error occurs during export
     */
    public void exportData() throws IOException {
        PlannerModel model = (PlannerModel) getModel();
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        repository.savePlannerData(model);
    }
    
    /**
     * Imports planner data from files.
     * @throws IOException If an I/O error occurs during import
     */
    public void importData() throws IOException {
        PlannerModel model = (PlannerModel) getModel();
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        repository.loadPlannerData(model);
    }
    
    /**
     * Saves the current student as a profile.
     * @throws IOException If an I/O error occurs during save
     */
    public void saveProfile() throws IOException {
        PlannerModel model = (PlannerModel) getModel();
        Student currentStudent = model.getCurrentStudent();
        if (currentStudent != null) {
            planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
            repository.saveProfile(currentStudent);
        }
    }
    
    /**
     * Loads all available profile names.
     * @return List of profile display names
     * @throws IOException If an I/O error occurs during load
     */
    public java.util.List<String> loadProfileNames() throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        return repository.loadProfileNames();
    }
    
    /**
     * Loads a specific profile by display name.
     * @param displayName The display name of the profile to load
     * @throws IOException If an I/O error occurs during load
     */
    public void loadProfile(String displayName) throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        
        // Save current profile data before switching
        PlannerModel currentModel = (PlannerModel) getModel();
        if (currentModel.getCurrentStudent() != null) {
            repository.savePlannerData(currentModel);
        }
        
        Student student = repository.loadProfile(displayName);
        if (student != null) {
            PlannerModel model = (PlannerModel) getModel();
            
            // Clear existing data and load profile-specific data
            model.clearAll();
            model.setCurrentStudent(student);
            
            // Load the profile's data
            repository.loadPlannerData(model);
            
            // Save the current profile state
            repository.saveAccountState(true, student);
            
            // Refresh all UI tabs to show the new profile data
            if (getView() != null) {
                PlannerView view = (PlannerView) getView();
                view.refreshAll();
            }
        }
    }
    
    /**
     * Removes a profile by display name.
     * @param displayName The display name of profile to remove
     * @throws IOException If an I/O error occurs during removal
     */
    public void removeProfile(String displayName) throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        repository.removeProfile(displayName);
    }

    /**
     * Clears all profile-specific data for the current student.
     * @throws IOException If an I/O error occurs during clearing
     */
    public void clearProfileData() throws IOException {
        PlannerModel model = (PlannerModel) getModel();
        Student currentStudent = model.getCurrentStudent();
        if (currentStudent != null) {
            planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
            repository.clearProfileData(currentStudent);
        }
    }
    
    /**
     * Saves user preferences including default tab setting.
     * @param defaultTab The default tab name
     * @throws IOException If an I/O error occurs during save
     */
    public void savePreferences(String defaultTab) throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        repository.savePreferences(defaultTab);
    }
    
    /**
     * Loads user preferences including default tab setting.
     * @return The default tab name
     * @throws IOException If an I/O error occurs during load
     */
    public String loadPreferences() throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        return repository.loadPreferences();
    }
    
    /**
     * Saves user preferences including account state.
     * @param isLoggedIn Whether user is logged into an account
     * @throws IOException If an I/O error occurs during save
     */
    public void saveAccountState(boolean isLoggedIn) throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        repository.saveAccountState(isLoggedIn);
    }
    
    /**
     * Loads user preferences including account state.
     * @return True if user is logged in, false otherwise
     * @throws IOException If an I/O error occurs during load
     */
    public boolean loadAccountState() throws IOException {
        planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
        return repository.loadAccountState();
    }
    
    /**
     * Logs out the current user and saves all data to the current profile.
     * @throws IOException If an I/O error occurs during logout
     */
    public void logout() throws IOException {
        PlannerModel model = (PlannerModel) getModel();
        Student currentStudent = model.getCurrentStudent();
        if (currentStudent != null) {
            planner.persistence.PlannerRepository repository = new planner.persistence.PlannerRepository();
            System.out.println("DEBUG: Logout - model has " + model.getCourses().size() + " courses before save");
            // Save all data to the current profile before logging out
            repository.savePlannerData(model);
            System.out.println("DEBUG: Logout - saved planner data");
            
            // Small delay to ensure data is fully written before clearing model
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Clear model data
            model.clearAll();
            // Set current student to null
            model.setCurrentStudent(null);
            // Save account state as logged out but keep current profile for easy login
            repository.saveAccountState(false, currentStudent);
            System.out.println("DEBUG: Logout - completed");
        }
    }
    
    }

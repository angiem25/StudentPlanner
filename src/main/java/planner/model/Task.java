package planner.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a task in the student planner.
 * A task has a title, description, due date, priority, and completion status.
 */
public class Task {
    private final String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private boolean completed;
    private String courseId;
    
    /**
     * Enumeration for task priority levels.
     */
    public enum Priority {
        LOW, MEDIUM, HIGH
    }
    
    /**
     * Creates a new task with a generated unique ID.
     * @param title The task title
     * @param description The task description
     * @param dueDate The due date for the task
     * @param priority The priority level of the task
     */
    public Task(String title, String description, LocalDateTime dueDate, Priority priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }
    
    /**
     * Creates a new task with a generated unique ID and default MEDIUM priority.
     * @param title The task title
     * @param description The task description
     * @param dueDate The due date for the task
     */
    public Task(String title, String description, LocalDateTime dueDate) {
        this(title, description, dueDate, Priority.MEDIUM);
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public String getCourseId() { return courseId; }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    /**
     * Marks this task as completed.
     */
    public void complete() {
        this.completed = true;
    }
    
    /**
     * Marks this task as incomplete.
     */
    public void incomplete() {
        this.completed = false;
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", completed=" + completed +
                ", courseId='" + courseId + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

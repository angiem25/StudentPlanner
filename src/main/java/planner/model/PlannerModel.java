package planner.model;

import mvc.AbstractModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The main model for the student planner application.
 * Manages students, courses, and tasks using the MVC framework.
 */
public class PlannerModel extends AbstractModel {
    private Student currentStudent;
    private final List<Course> courses;
    private final List<Task> tasks;
    private final List<Event> events;
    
    /**
     * Creates a new PlannerModel with empty collections.
     */
    public PlannerModel() {
        this.courses = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.events = new ArrayList<>();
    }
    
    // Student management
    public Student getCurrentStudent() { return currentStudent; }
    
    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        fireModelChanged("STUDENT_CHANGED", student);
    }
    
    // Course management
    public List<Course> getCourses() { return new ArrayList<>(courses); }
    
    public Optional<Course> getCourseById(String id) {
        return courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst();
    }
    
    public Optional<Course> getCourseByCode(String code) {
        return courses.stream()
                .filter(course -> course.getCode().equalsIgnoreCase(code))
                .findFirst();
    }
    
    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
            fireModelChanged("COURSE_ADDED", course);
        }
    }
    
    public void removeCourse(Course course) {
        if (courses.remove(course)) {
            // Remove tasks associated with this course
            tasks.removeIf(task -> course.getId().equals(task.getCourseId()));
            fireModelChanged("COURSE_REMOVED", course);
        }
    }
    
    public void updateCourse(Course course) {
        int index = courses.indexOf(course);
        if (index >= 0) {
            courses.set(index, course);
            fireModelChanged("COURSE_UPDATED", course);
        }
    }
    
    // Task management
    public List<Task> getTasks() { return new ArrayList<>(tasks); }
    
    public List<Task> getTasksForCourse(String courseId) {
        return tasks.stream()
                .filter(task -> courseId.equals(task.getCourseId()))
                .toList();
    }
    
    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(Task::isCompleted)
                .toList();
    }
    
    public List<Task> getIncompleteTasks() {
        return tasks.stream()
                .filter(task -> !task.isCompleted())
                .toList();
    }
    
    public Optional<Task> getTaskById(String id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }
    
    public void addTask(Task task) {
        if (task != null && !tasks.contains(task)) {
            tasks.add(task);
            fireModelChanged("TASK_ADDED", task);
        }
    }
    
    public void removeTask(Task task) {
        if (tasks.remove(task)) {
            fireModelChanged("TASK_REMOVED", task);
        }
    }
    
    public void updateTask(Task task) {
        int index = tasks.indexOf(task);
        if (index >= 0) {
            tasks.set(index, task);
            fireModelChanged("TASK_UPDATED", task);
        }
    }
    
    public void completeTask(Task task) {
        if (tasks.contains(task)) {
            task.complete();
            fireModelChanged("TASK_COMPLETED", task);
        }
    }
    
    public void incompleteTask(Task task) {
        if (tasks.contains(task)) {
            task.incomplete();
            fireModelChanged("TASK_INCOMPLETED", task);
        }
    }
    
    // Event management
    public List<Event> getEvents() { return new ArrayList<>(events); }
    
    public List<Event> getEventsForDate(java.time.LocalDate date) {
        return events.stream()
                .filter(event -> event.occursOn(date))
                .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                .toList();
    }
    
    public List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) {
        return events.stream()
                .filter(event -> event.overlapsWith(start, end))
                .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                .toList();
    }
    
    public Optional<Event> getEventById(String id) {
        return events.stream()
                .filter(event -> event.getId().equals(id))
                .findFirst();
    }
    
    public void addEvent(Event event) {
        if (event != null && !events.contains(event)) {
            events.add(event);
            fireModelChanged("EVENT_ADDED", event);
        }
    }
    
    public void removeEvent(Event event) {
        if (events.remove(event)) {
            fireModelChanged("EVENT_REMOVED", event);
        }
    }
    
    public void updateEvent(Event event) {
        int index = events.indexOf(event);
        if (index >= 0) {
            events.set(index, event);
            fireModelChanged("EVENT_UPDATED", event);
        }
    }
    
    /**
     * Clears all data from the planner.
     */
    public void clearAll() {
        courses.clear();
        tasks.clear();
        events.clear();
        currentStudent = null;
        fireModelChanged("PLANNER_CLEARED", null);
    }
}

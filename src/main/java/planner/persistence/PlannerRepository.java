package planner.persistence;

import planner.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository class for persisting and loading planner data.
 * Uses file-based storage with a simple CSV-like format.
 */
public class PlannerRepository {
    private static final String DATA_DIR = "planner_data";
    private static final String STUDENT_FILE = "student.csv";
    private static final String COURSES_FILE = "courses.csv";
    private static final String TASKS_FILE = "tasks.csv";
    private static final String EVENTS_FILE = "events.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Saves the complete planner data to files.
     * @param model The planner model to save
     * @throws IOException If an I/O error occurs
     */
    public void savePlannerData(PlannerModel model) throws IOException {
        createDataDirectory();
        
        saveStudent(model.getCurrentStudent());
        saveCourses(model.getCourses());
        saveTasks(model.getTasks());
        saveEvents(model.getEvents());
    }
    
    /**
     * Loads planner data from files into the model.
     * @param model The planner model to load data into
     * @throws IOException If an I/O error occurs
     */
    public void loadPlannerData(PlannerModel model) throws IOException {
        Path dataDir = Paths.get(DATA_DIR);
        if (!Files.exists(dataDir)) {
            return; // No data to load
        }
        
        Student student = loadStudent();
        if (student != null) {
            model.setCurrentStudent(student);
        }
        
        List<Course> courses = loadCourses();
        courses.forEach(model::addCourse);
        
        List<Task> tasks = loadTasks();
        tasks.forEach(model::addTask);
        
        List<Event> events = loadEvents();
        events.forEach(model::addEvent);
    }
    
    /**
     * Creates the data directory if it doesn't exist.
     * @throws IOException If directory creation fails
     */
    private void createDataDirectory() throws IOException {
        Path dataDir = Paths.get(DATA_DIR);
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
    }
    
    /**
     * Saves student data to file.
     * @param student The student to save
     * @throws IOException If an I/O error occurs
     */
    private void saveStudent(Student student) throws IOException {
        if (student == null) {
            return;
        }
        
        Path studentFile = Paths.get(DATA_DIR, STUDENT_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(studentFile.toFile()))) {
            writer.println(student.getId());
            writer.println(student.getFirstName());
            writer.println(student.getLastName());
            writer.println(student.getEmail());
            writer.println(student.getStudentId());
            writer.println(student.getYear());
            writer.println(student.getMajor());
        }
    }
    
    /**
     * Loads student data from file.
     * @return The loaded student, or null if no student file exists
     * @throws IOException If an I/O error occurs
     */
    private Student loadStudent() throws IOException {
        Path studentFile = Paths.get(DATA_DIR, STUDENT_FILE);
        if (!Files.exists(studentFile)) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(studentFile.toFile()))) {
            String id = reader.readLine();
            String firstName = reader.readLine();
            String lastName = reader.readLine();
            String email = reader.readLine();
            String studentId = reader.readLine();
            String yearStr = reader.readLine();
            String major = reader.readLine();
            
            int year = Integer.parseInt(yearStr);
            Student student = new Student(firstName, lastName, email, studentId, year, major);
            // Use reflection or create a constructor that accepts ID, or create a setter
            // For now, we'll create a new student and ignore the saved ID
            
            return student;
        }
    }
    
    /**
     * Saves courses to file.
     * @param courses The list of courses to save
     * @throws IOException If an I/O error occurs
     */
    private void saveCourses(List<Course> courses) throws IOException {
        Path coursesFile = Paths.get(DATA_DIR, COURSES_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(coursesFile.toFile()))) {
            for (Course course : courses) {
                writer.printf("%s,%s,%s,%s,%d%n",
                    course.getId(),
                    escapeCsv(course.getName()),
                    escapeCsv(course.getCode()),
                    escapeCsv(course.getInstructor()),
                    course.getCredits()
                );
            }
        }
    }
    
    /**
     * Loads courses from file.
     * @return The list of loaded courses
     * @throws IOException If an I/O error occurs
     */
    private List<Course> loadCourses() throws IOException {
        List<Course> courses = new ArrayList<>();
        Path coursesFile = Paths.get(DATA_DIR, COURSES_FILE);
        if (!Files.exists(coursesFile)) {
            return courses;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = unescapeCsv(parts[1]);
                    String code = unescapeCsv(parts[2]);
                    String instructor = unescapeCsv(parts[3]);
                    int credits = Integer.parseInt(parts[4]);
                    
                    Course course = new Course(name, code, instructor, credits);
                    courses.add(course);
                }
            }
        }
        
        return courses;
    }
    
    /**
     * Saves tasks to file.
     * @param tasks The list of tasks to save
     * @throws IOException If an I/O error occurs
     */
    private void saveTasks(List<Task> tasks) throws IOException {
        Path tasksFile = Paths.get(DATA_DIR, TASKS_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(tasksFile.toFile()))) {
            for (Task task : tasks) {
                writer.printf("%s,%s,%s,%s,%s,%b,%s,%s%n",
                    task.getId(),
                    escapeCsv(task.getTitle()),
                    escapeCsv(task.getDescription()),
                    task.getDueDate().format(DATE_FORMATTER),
                    task.getPriority().toString(),
                    task.isCompleted(),
                    task.getCourseId() != null ? task.getCourseId() : "",
                    escapeCsv(task.getAccentColorHex() != null ? task.getAccentColorHex() : "")
                );
            }
        }
    }
    
    /**
     * Loads tasks from file.
     * @return The list of loaded tasks
     * @throws IOException If an I/O error occurs
     */
    private List<Task> loadTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        Path tasksFile = Paths.get(DATA_DIR, TASKS_FILE);
        if (!Files.exists(tasksFile)) {
            return tasks;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(tasksFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 7) {
                    String id = parts[0];
                    String title = unescapeCsv(parts[1]);
                    String description = unescapeCsv(parts[2]);
                    LocalDateTime dueDate = LocalDateTime.parse(parts[3], DATE_FORMATTER);
                    Task.Priority priority = Task.Priority.valueOf(parts[4]);
                    boolean completed = Boolean.parseBoolean(parts[5]);
                    String courseId = parts[6].isEmpty() ? null : parts[6];
                    String accentHex = parts.length >= 8 ? unescapeCsv(parts[7]) : "";
                    
                    Task task = new Task(title, description, dueDate, priority);
                    task.setCompleted(completed);
                    if (courseId != null && !courseId.isEmpty()) {
                        task.setCourseId(courseId);
                    }
                    if (accentHex != null && !accentHex.isEmpty()) {
                        task.setAccentColorHex(accentHex);
                    }
                    tasks.add(task);
                }
            }
        }
        
        return tasks;
    }
    
    /**
     * Saves events to file.
     * @param events The list of events to save
     * @throws IOException If an I/O error occurs
     */
    private void saveEvents(List<Event> events) throws IOException {
        Path eventsFile = Paths.get(DATA_DIR, EVENTS_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(eventsFile.toFile()))) {
            for (Event event : events) {
                writer.printf("%s,%s,%s,%s,%s%n",
                    event.getId(),
                    escapeCsv(event.getTitle()),
                    escapeCsv(event.getDescription()),
                    event.getStartDateTime().format(DATE_FORMATTER),
                    event.getEndDateTime().format(DATE_FORMATTER)
                );
            }
        }
    }
    
    /**
     * Loads events from file.
     * @return The list of loaded events
     * @throws IOException If an I/O error occurs
     */
    private List<Event> loadEvents() throws IOException {
        List<Event> events = new ArrayList<>();
        Path eventsFile = Paths.get(DATA_DIR, EVENTS_FILE);
        if (!Files.exists(eventsFile)) {
            return events;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(eventsFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String id = parts[0];
                    String title = unescapeCsv(parts[1]);
                    String description = unescapeCsv(parts[2]);
                    LocalDateTime startDateTime = LocalDateTime.parse(parts[3], DATE_FORMATTER);
                    LocalDateTime endDateTime = LocalDateTime.parse(parts[4], DATE_FORMATTER);
                    
                    Event event = new Event(title, description, startDateTime, endDateTime);
                    events.add(event);
                }
            }
        }
        
        return events;
    }
    
    /**
     * Escapes a string for CSV format.
     * @param value The string to escape
     * @return The escaped string
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Unescapes a string from CSV format.
     * @param value The string to unescape
     * @return The unescaped string
     */
    private String unescapeCsv(String value) {
        if (value == null) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
    
    /**
     * Deletes all planner data files.
     * @throws IOException If an I/O error occurs
     */
    public void deleteAllData() throws IOException {
        Path dataDir = Paths.get(DATA_DIR);
        if (Files.exists(dataDir)) {
            Files.walk(dataDir)
                .sorted((a, b) -> -a.compareTo(b)) // Reverse order to delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore errors during deletion
                    }
                });
        }
    }
}

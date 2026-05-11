package planner.persistence;

import planner.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
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
    private static final String PROFILES_FILE = "profiles.csv";
    private static final String PROFILES_DIR = "profiles";
    private static final String PREFERENCES_FILE = "preferences.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Saves the complete planner data to files.
     * @param model The planner model to save
     * @throws IOException If an I/O error occurs
     */
    public void savePlannerData(PlannerModel model) throws IOException {
        createDataDirectory();
        
        Student currentStudent = model.getCurrentStudent();
        saveStudent(currentStudent);
        
        // Save data to profile-specific files if logged in, otherwise to global files
        if (currentStudent != null) {
            String profilePrefix = getProfilePrefix(currentStudent);
            saveCourses(model.getCourses(), profilePrefix);
            saveTasks(model.getTasks(), profilePrefix);
            saveEvents(model.getEvents(), profilePrefix);
        } else {
            // Save to global files when no student is logged in
            saveCourses(model.getCourses(), null);
            saveTasks(model.getTasks(), null);
            saveEvents(model.getEvents(), null);
        }
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
        
        // Try to load current profile from preferences
        String currentProfileDisplayName = loadCurrentProfile();
        System.out.println("DEBUG: Current profile from preferences: " + currentProfileDisplayName);
        Student student = null;
        
        if (currentProfileDisplayName != null) {
            // Load the specific profile
            System.out.println("DEBUG: Loading profile: " + currentProfileDisplayName);
            student = loadProfile(currentProfileDisplayName);
        }
        
        if (student != null) {
            model.setCurrentStudent(student);
            System.out.println("DEBUG: Student set in model: " + student.getFirstName() + " " + student.getLastName());
            
            // Load profile-specific data for logged in student
            String profilePrefix = getProfilePrefix(student);
            System.out.println("DEBUG: Loading courses from prefix: " + profilePrefix);
            List<Course> courses = loadCourses(profilePrefix);
            System.out.println("DEBUG: Loaded " + courses.size() + " courses");
            // Clear existing courses before adding new ones to prevent duplicates
            model.getCourses().clear();
            courses.forEach(model::addCourse);
            System.out.println("DEBUG: Added " + courses.size() + " courses to model");
            
            System.out.println("DEBUG: Loading tasks from prefix: " + profilePrefix);
            List<Task> tasks = loadTasks(profilePrefix);
            System.out.println("DEBUG: Loaded " + tasks.size() + " tasks");
            tasks.forEach(model::addTask);
            System.out.println("DEBUG: Added " + tasks.size() + " tasks to model");
            
            System.out.println("DEBUG: Loading events from prefix: " + profilePrefix);
            List<Event> events = loadEvents(profilePrefix);
            System.out.println("DEBUG: Loaded " + events.size() + " events");
            events.forEach(model::addEvent);
            System.out.println("DEBUG: Added " + events.size() + " events to model");
        } else {
            // When no student is logged in, keep all lists empty
            // No global data should be loaded
            System.out.println("DEBUG: No student logged in - keeping lists empty");
        }
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
        
        try (BufferedReader reader = new BufferedReader(new FileReader(studentFile.toString()))) {
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
     * Gets the profile prefix for a student.
     * @param student The student to get prefix for
     * @return The profile prefix string
     */
    public String getProfilePrefix(Student student) {
        if (student == null) return null;
        return student.getStudentId() + "_" + student.getFirstName() + "_" + student.getLastName();
    }

    /**
     * Saves courses to file.
     * @param courses The list of courses to save
     * @param profilePrefix The profile prefix for profile-specific files, null for global
     * @throws IOException If an I/O error occurs
     */
    private void saveCourses(List<Course> courses, String profilePrefix) throws IOException {
        System.out.println("DEBUG: saveCourses called with " + courses.size() + " courses, profilePrefix: " + profilePrefix);
        Path coursesFile;
        if (profilePrefix != null) {
            // Save to profile-specific file
            createProfilesDirectory();
            coursesFile = Paths.get(DATA_DIR, PROFILES_DIR, profilePrefix + "_" + COURSES_FILE);
        } else {
            // Save to global file
            coursesFile = Paths.get(DATA_DIR, COURSES_FILE);
        }
        
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
     * @param profilePrefix The profile prefix for profile-specific files, null for global
     * @return The list of loaded courses
     * @throws IOException If an I/O error occurs
     */
    public List<Course> loadCourses(String profilePrefix) throws IOException {
        System.out.println("DEBUG: loadCourses called with profilePrefix: " + profilePrefix);
        List<Course> courses = new ArrayList<>();
        Path coursesFile;
        if (profilePrefix != null) {
            // Load from profile-specific file
            createProfilesDirectory();
            coursesFile = Paths.get(DATA_DIR, PROFILES_DIR, profilePrefix + "_" + COURSES_FILE);
        } else {
            // Load from global file
            coursesFile = Paths.get(DATA_DIR, COURSES_FILE);
        }
        
        System.out.println("DEBUG: Loading courses from file: " + coursesFile);
        System.out.println("DEBUG: File exists: " + Files.exists(coursesFile));
        if (!Files.exists(coursesFile)) {
            System.out.println("DEBUG: File does not exist, returning empty list");
            return courses;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFile.toFile()))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println("DEBUG: Reading line " + lineNumber + ": '" + line + "'");
                if (line.trim().isEmpty()) {
                    System.out.println("DEBUG: Skipping empty line " + lineNumber);
                    continue;
                }
                
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                System.out.println("DEBUG: Line " + lineNumber + " has " + parts.length + " parts");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = unescapeCsv(parts[1]);
                    String code = unescapeCsv(parts[2]);
                    String instructor = unescapeCsv(parts[3]);
                    int credits = Integer.parseInt(parts[4]);
                    
                    Course course = new Course(name, code, instructor, credits);
                    courses.add(course);
                    System.out.println("DEBUG: Successfully added course: " + course.getName());
                } else {
                    System.out.println("DEBUG: Line " + lineNumber + " has insufficient parts (" + parts.length + "), skipping");
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Exception during course loading: " + e.getMessage());
            e.printStackTrace();
        }
        
        return courses;
    }
    
    /**
     * Saves tasks to file.
     * @param tasks The list of tasks to save
     * @param profilePrefix The profile prefix for profile-specific files, null for global
     * @throws IOException If an I/O error occurs
     */
    private void saveTasks(List<Task> tasks, String profilePrefix) throws IOException {
        Path tasksFile;
        if (profilePrefix != null) {
            // Save to profile-specific file
            createProfilesDirectory();
            tasksFile = Paths.get(DATA_DIR, PROFILES_DIR, profilePrefix + "_" + TASKS_FILE);
        } else {
            // Save to global file
            tasksFile = Paths.get(DATA_DIR, TASKS_FILE);
        }
        
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
     * @param profilePrefix The profile prefix for profile-specific files, null for global
     * @return The list of loaded tasks
     * @throws IOException If an I/O error occurs
     */
    public List<Task> loadTasks(String profilePrefix) throws IOException {
        List<Task> tasks = new ArrayList<>();
        Path tasksFile;
        if (profilePrefix != null) {
            // Load from profile-specific file
            createProfilesDirectory();
            tasksFile = Paths.get(DATA_DIR, PROFILES_DIR, profilePrefix + "_" + TASKS_FILE);
        } else {
            // Load from global file
            tasksFile = Paths.get(DATA_DIR, TASKS_FILE);
        }
        
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
     * @param profilePrefix The profile prefix for profile-specific files, null for global
     * @throws IOException If an I/O error occurs
     */
    private void saveEvents(List<Event> events, String profilePrefix) throws IOException {
        Path eventsFile;
        if (profilePrefix != null) {
            // Save to profile-specific file
            createProfilesDirectory();
            eventsFile = Paths.get(DATA_DIR, PROFILES_DIR, profilePrefix + "_" + EVENTS_FILE);
        } else {
            // Save to global file
            eventsFile = Paths.get(DATA_DIR, EVENTS_FILE);
        }
        
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
     * @param profilePrefix The profile prefix for profile-specific files, null for global
     * @return The list of loaded events
     * @throws IOException If an I/O error occurs
     */
    public List<Event> loadEvents(String profilePrefix) throws IOException {
        List<Event> events = new ArrayList<>();
        Path eventsFile;
        if (profilePrefix != null) {
            // Load from profile-specific file
            createProfilesDirectory();
            eventsFile = Paths.get(DATA_DIR, PROFILES_DIR, profilePrefix + "_" + EVENTS_FILE);
        } else {
            // Load from global file
            eventsFile = Paths.get(DATA_DIR, EVENTS_FILE);
        }
        
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
     * Clears profile-specific data for a student.
     * @param student The student whose data should be cleared
     * @throws IOException If an I/O error occurs
     */
    public void clearProfileData(Student student) throws IOException {
        if (student == null) return;
        
        String profilePrefix = getProfilePrefix(student);
        clearProfileFiles(profilePrefix);
    }

    /**
     * Clears profile-specific files for a profile prefix.
     * @param profilePrefix The profile prefix to clear files for
     * @throws IOException If an I/O error occurs
     */
    private void clearProfileFiles(String profilePrefix) throws IOException {
        if (profilePrefix == null) return;
        
        Path profilesDir = Paths.get(DATA_DIR, PROFILES_DIR);
        if (!Files.exists(profilesDir)) return;
        
        // Delete profile-specific data files
        String[] dataFiles = {profilePrefix + "_" + COURSES_FILE, 
                               profilePrefix + "_" + TASKS_FILE, 
                               profilePrefix + "_" + EVENTS_FILE};
        
        for (String fileName : dataFiles) {
            Path filePath = profilesDir.resolve(fileName);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    // Ignore errors during deletion
                }
            }
        }
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
    
    /**
     * Saves a student profile to the profiles directory.
     * @param student The student to save as a profile
     * @throws IOException If an I/O error occurs
     */
    public void saveProfile(Student student) throws IOException {
        createProfilesDirectory();
        String profileFileName = student.getStudentId() + "_" + student.getFirstName() + "_" + student.getLastName() + ".csv";
        Path profilePath = Paths.get(DATA_DIR, PROFILES_DIR, profileFileName);
        
        try (BufferedWriter writer = Files.newBufferedWriter(profilePath)) {
            // Save student data
            writer.write("STUDENT");
            writer.newLine();
            writer.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," +
                        student.getEmail() + "," + student.getStudentId() + "," + student.getAcademicYear().getDisplayName() + "," +
                        student.getMajor());
            writer.newLine();
        }
    }
    
    /**
     * Loads all available student profiles.
     * @return List of profile names (display strings)
     * @throws IOException If an I/O error occurs
     */
    public List<String> loadProfileNames() throws IOException {
        List<String> profileNames = new ArrayList<>();
        Path profilesDir = Paths.get(DATA_DIR, PROFILES_DIR);
        
        if (!Files.exists(profilesDir)) {
            return profileNames;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(profilesDir, "*.csv")) {
            for (Path profileFile : stream) {
                try (BufferedReader reader = Files.newBufferedReader(profileFile)) {
                    String line = reader.readLine(); // Skip STUDENT header
                    if (line != null && line.equals("STUDENT")) {
                        String dataLine = reader.readLine();
                        if (dataLine != null) {
                            String[] parts = dataLine.split(",");
                            if (parts.length >= 7) {
                                String displayName = parts[1] + " " + parts[2] + " - " + parts[6] + " (" + parts[5] + ")";
                                profileNames.add(displayName);
                            }
                        }
                    }
                } catch (IOException e) {
                    // Skip problematic files
                }
            }
        }
        
        return profileNames;
    }
    
    /**
     * Loads a specific student profile by display name.
     * @param displayName The display name of the profile to load
     * @return The loaded student, or null if not found
     * @throws IOException If an I/O error occurs
     */
    public Student loadProfile(String displayName) throws IOException {
        Path profilesDir = Paths.get(DATA_DIR, PROFILES_DIR);
        
        if (!Files.exists(profilesDir)) {
            return null;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(profilesDir, "*.csv")) {
            for (Path profileFile : stream) {
                try (BufferedReader reader = Files.newBufferedReader(profileFile)) {
                    String line = reader.readLine(); // Skip STUDENT header
                    if (line != null && line.equals("STUDENT")) {
                        String dataLine = reader.readLine();
                        if (dataLine != null) {
                            String[] parts = dataLine.split(",");
                            if (parts.length >= 7) {
                                String currentDisplayName = parts[1] + " " + parts[2] + " - " + parts[6] + " (" + parts[5] + ")";
                                if (currentDisplayName.equals(displayName)) {
                                    // Parse and create student
                                    String id = parts[0];
                                    String firstName = parts[1];
                                    String lastName = parts[2];
                                    String email = parts[3];
                                    String studentId = parts[4];
                                    String yearDisplayName = parts[5];
                                    String major = parts[6];
                                    
                                    Student.AcademicYear academicYear = Student.AcademicYear.fromDisplayName(yearDisplayName);
                                    return new Student(firstName, lastName, email, studentId, academicYear, major);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    // Skip problematic files
                }
            }
        }
        
        return null;
    }
    
    /**
     * Removes a student profile by display name.
     * @param displayName The display name of the profile to remove
     * @throws IOException If an I/O error occurs
     */
    public void removeProfile(String displayName) throws IOException {
        Path profilesDir = Paths.get(DATA_DIR, PROFILES_DIR);
        
        if (!Files.exists(profilesDir)) {
            return;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(profilesDir, "*.csv")) {
            for (Path profileFile : stream) {
                try (BufferedReader reader = Files.newBufferedReader(profileFile)) {
                    String line = reader.readLine(); // Skip STUDENT header
                    if (line != null && line.equals("STUDENT")) {
                        String dataLine = reader.readLine();
                        if (dataLine != null) {
                            String[] parts = dataLine.split(",");
                            if (parts.length >= 7) {
                                String currentDisplayName = parts[1] + " " + parts[2] + " - " + parts[6] + " (" + parts[5] + ")";
                                if (currentDisplayName.equals(displayName)) {
                                    Files.delete(profileFile);
                                    return;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    // Skip problematic files
                }
            }
        }
    }
    
    /**
     * Creates profiles directory if it doesn't exist.
     * @throws IOException If an I/O error occurs
     */
    private void createProfilesDirectory() throws IOException {
        Path profilesDir = Paths.get(DATA_DIR, PROFILES_DIR);
        if (!Files.exists(profilesDir)) {
            Files.createDirectories(profilesDir);
        }
    }
    
    /**
     * Saves user preferences including default tab setting.
     * @param defaultTab The default tab name
     * @throws IOException If an I/O error occurs
     */
    public void savePreferences(String defaultTab) throws IOException {
        createDataDirectory();
        Path preferencesPath = Paths.get(DATA_DIR, PREFERENCES_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(preferencesPath)) {
            writer.write("DEFAULT_TAB," + defaultTab);
            writer.newLine();
        }
    }
    
    /**
     * Saves user preferences including account state and current profile.
     * @param isLoggedIn Whether user is logged into an account
     * @throws IOException If an I/O error occurs
     */
    public void saveAccountState(boolean isLoggedIn) throws IOException {
        saveAccountState(isLoggedIn, null);
    }
    
    /**
     * Saves user preferences including account state and current profile.
     * @param isLoggedIn Whether user is logged into an account
     * @param currentStudent The currently logged in student (if any)
     * @throws IOException If an I/O error occurs
     */
    public void saveAccountState(boolean isLoggedIn, Student currentStudent) throws IOException {
        Path preferencesPath = Paths.get(DATA_DIR, PREFERENCES_FILE);
        createDataDirectory();
        
                
        List<String> lines = new ArrayList<>();
        boolean accountLineFound = false;
        boolean profileLineFound = false;
        
        // Read existing preferences
        if (Files.exists(preferencesPath)) {
            try (BufferedReader reader = Files.newBufferedReader(preferencesPath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(preferencesPath)) {
            // Update existing lines
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith("ACCOUNT_STATE,")) {
                    writer.write("ACCOUNT_STATE," + (isLoggedIn ? "LOGGED_IN" : "LOGGED_OUT"));
                    accountLineFound = true;
                } else if (line.startsWith("CURRENT_PROFILE,")) {
                    if (isLoggedIn && currentStudent != null) {
                        String displayName = currentStudent.getFirstName() + " " + currentStudent.getLastName() + " - " + currentStudent.getMajor() + " (" + currentStudent.getAcademicYear().getDisplayName() + ")";
                                                writer.write("CURRENT_PROFILE," + displayName);
                    } else {
                        // Only clear profile if explicitly logged out, not if currentStudent is null during startup
                        if (!isLoggedIn) {
                            writer.write("CURRENT_PROFILE,");
                        } else {
                            // Keep existing profile when currentStudent is null during startup
                            writer.write(line);
                        }
                    }
                    profileLineFound = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
            
            // Add account state line if not found
            if (!accountLineFound) {
                writer.write("ACCOUNT_STATE," + (isLoggedIn ? "LOGGED_IN" : "LOGGED_OUT"));
                writer.newLine();
            }
            
            // Add current profile line if not found
            if (!profileLineFound && isLoggedIn && currentStudent != null) {
                String displayName = currentStudent.getFirstName() + " " + currentStudent.getLastName() + " - " + currentStudent.getMajor() + " (" + currentStudent.getAcademicYear().getDisplayName() + ")";
                                writer.write("CURRENT_PROFILE," + displayName);
                writer.newLine();
            }
        }
        
            }
    
    /**
     * Loads the current profile from preferences.
     * @return The display name of the current profile, or null if not found
     * @throws IOException If an I/O error occurs
     */
    public String loadCurrentProfile() throws IOException {
        Path preferencesPath = Paths.get(DATA_DIR, PREFERENCES_FILE);
        if (!Files.exists(preferencesPath)) {
            return null;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(preferencesPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CURRENT_PROFILE,")) {
                    String[] parts = line.split(",", 2);
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        return parts[1];
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Loads user preferences including default tab setting.
     * @return The default tab name, or "Tasks" if not set
     * @throws IOException If an I/O error occurs
     */
    public String loadPreferences() throws IOException {
        Path preferencesPath = Paths.get(DATA_DIR, PREFERENCES_FILE);
        if (!Files.exists(preferencesPath)) {
            return "Tasks"; // Default to Tasks tab
        }
        
        try (BufferedReader reader = Files.newBufferedReader(preferencesPath)) {
            String line = reader.readLine();
            if (line != null && line.startsWith("DEFAULT_TAB,")) {
                String defaultTab = line.substring("DEFAULT_TAB,".length());
                // Validate that it's one of the allowed tabs
                if (defaultTab.equals("Tasks") || defaultTab.equals("Today") || 
                    defaultTab.equals("Weekly Priorities") || defaultTab.equals("Calendar")) {
                    return defaultTab;
                }
            }
        }
        
        return "Tasks"; // Default to Tasks tab if invalid or not found
    }
    
    /**
     * Loads user preferences including account state.
     * @return True if user is logged in, false otherwise
     * @throws IOException If an I/O error occurs
     */
    public boolean loadAccountState() throws IOException {
        Path preferencesPath = Paths.get(DATA_DIR, PREFERENCES_FILE);
        if (!Files.exists(preferencesPath)) {
            return false; // Default to not logged in
        }
        
        try (BufferedReader reader = Files.newBufferedReader(preferencesPath)) {
            String line = reader.readLine();
            while (line != null) {
                if (line.startsWith("ACCOUNT_STATE,")) {
                    String state = line.substring("ACCOUNT_STATE,".length());
                    return "LOGGED_IN".equals(state);
                }
                line = reader.readLine();
            }
        }
        
        return false; // Default to not logged in if not found
    }
}

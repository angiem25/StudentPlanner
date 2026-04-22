# PROMPT G: PERSISTENCE LAYER (DATA STORAGE)

## **MANDATORY**: Implement file-based data persistence for automatic save/load.

## Module of Thought

**Phase**: Data Storage Implementation  
**Purpose**: Enable data persistence across application restarts  
**Dependencies**: PROMPT-F (User Interface)

### Persistence Strategy

**Format Choice: CSV**:
- Human-readable (easy to debug)
- Text-based (version control friendly)
- Portable (works across operating systems)
- Simple (no database setup required)

**Storage Location**:
- Local directory: `planner_data/`
- Separate files per entity type:
  - `student.csv` - Single student record
  - `courses.csv` - Multiple course records
  - `tasks.csv` - Multiple task records

### File Locations

```
src/main/java/planner/persistence/
└── PlannerRepository.java
```

### Repository Pattern

```java
public class PlannerRepository {
    private static final String DATA_DIR = "planner_data";
    
    // Save all data
    public void savePlannerData(PlannerModel model) throws IOException
    
    // Load all data
    public void loadPlannerData(PlannerModel model) throws IOException
    
    // Delete all data
    public void deleteAllData() throws IOException
}
```

### File Format Design

**Student File** (single record, one per line):
```
<uuid>
<firstName>
<lastName>
<email>
<studentId>
<year>
<major>
```

**Courses File** (multiple records, CSV format):
```csv
id,name,code,instructor,credits
abc-123,Java Programming,CS101,Dr. Smith,3
xyz-789,Database Systems,CS205,Dr. Jones,3
```

**Tasks File** (multiple records, CSV format):
```csv
id,title,description,dueDate,priority,completed,courseId
task-1,Assignment 1,First assignment,2026-04-30 23:59,HIGH,false,abc-123
```

### CSV Handling

**Escaping**:
```java
private String escapeCsv(String value) {
    if (value == null) return "";
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
}
```

### Loading Strategy

```java
public void loadPlannerData(PlannerModel model) throws IOException {
    // Load student first
    Student student = loadStudent();
    if (student != null) {
        model.setCurrentStudent(student);
    }
    
    // Load courses (before tasks, since tasks reference courses)
    List<Course> courses = loadCourses();
    courses.forEach(model::addCourse);
    
    // Load tasks (after courses, for references)
    List<Task> tasks = loadTasks();
    tasks.forEach(model::addTask);
}
```

**Key Insight**: Load order matters! Load courses before tasks.

### Shutdown Hook Pattern

```java
// In Main.java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    try {
        repository.savePlannerData(model);
    } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Could not save planner data", e);
    }
}));
```

**Why shutdown hook?**
- Ensures data is saved when application exits
- Catches normal termination and SIGTERM
- Does NOT catch SIGKILL or power loss

### Expected Outcome

After this prompt, you should have:
- PlannerRepository with save/load/delete operations
- CSV format for human-readable storage
- Proper escaping for special characters
- Loading order that respects entity dependencies
- Shutdown hook for automatic saving

### Verification Halt

**Check before proceeding**:

- [ ] Directory created automatically if doesn't exist
- [ ] CSV escaping handles commas and quotes
- [ ] Date format consistent (ISO-8601 style)
- [ ] Loading order: Student → Courses → Tasks
- [ ] Graceful handling of missing files (first run)
- [ ] Shutdown hook implemented in Main
- [ ] Can explain why CSV vs. JSON vs. Database

**Test Scenario**:
```java
PlannerRepository repo = new PlannerRepository();
PlannerModel model = new PlannerModel();

// Add some data
model.setCurrentStudent(new Student(...));
model.addCourse(new Course(...));

// Save
repo.savePlannerData(model);

// Verify files exist
assert new File("planner_data/student.csv").exists();
assert new File("planner_data/courses.csv").exists();

// Clear and reload
model.clearAll();
repo.loadPlannerData(model);
assert model.getCurrentStudent() != null;
assert !model.getCourses().isEmpty();
```

---

**Next**: PROMPT-H: Main Application Entry Point  
**Prev**: PROMPT-F: User Interface

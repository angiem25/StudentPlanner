# PROMPT E: SERVICE LAYER (BUSINESS LOGIC)

## **MANDATORY**: Create the service layer that encapsulates business logic and validation rules.

## Module of Thought

**Phase**: Business Logic Implementation  
**Purpose**: Separate business rules from model and UI concerns  
**Dependencies**: PROMPT-D (Domain Models)

### Why a Service Layer?

**Separation of Concerns**:
- Model: Data storage and basic CRUD
- Service: Business rules and validation
- UI: Presentation and user interaction

**Complex Operations**: When an operation involves:
- Multiple entities
- Validation rules
- Business constraints
- Complex queries

Put it in the Service layer, not the Model or Controller.

### Service Layer Responsibilities

1. **Validation**: Business rule validation (duplicates, ranges, constraints)
2. **Coordination**: Operations that touch multiple models
3. **Queries**: Complex queries (upcoming tasks, overdue tasks, etc.)
4. **Transaction-like operations**: All-or-nothing updates

### File Locations

```
src/main/java/planner/service/
└── PlannerService.java
```

### Implementation Structure

```java
public class PlannerService {
    private final PlannerModel model;
    
    public PlannerService(PlannerModel model) {
        this.model = model;
    }
    
    // Student operations
    public void createStudentProfile(String firstName, String lastName, ...)
    
    // Course operations with validation
    public Course addCourse(String name, String code, ...)
    public void updateCourse(String courseId, ...)
    public void removeCourse(String courseId)
    
    // Task operations with validation
    public Task addTask(String title, String description, ...)
    public void updateTask(String taskId, ...)
    public void toggleTaskCompletion(String taskId)
    
    // Query operations
    public List<Task> getUpcomingTasks(int days)
    public List<Task> getOverdueTasks()
    public List<Task> getTasksByPriority(Priority priority)
    
    // Private validation methods
    private void validateStudentData(...)
    private void validateCourseData(...)
    private void validateTaskData(...)
}
```

### Validation Strategy

**Multi-layer validation**:
1. **UI Layer**: Format validation (dates, numbers)
2. **Service Layer**: Business rules (duplicates, ranges, cross-entity validation)
3. **Model Layer**: Data integrity (null checks, basic constraints)

**Example Validations**:
```java
// Course validation
if (model.getCourseByCode(code).isPresent()) {
    throw new IllegalArgumentException("Course with code " + code + " already exists");
}

// Task validation
if (courseId != null && model.getCourseById(courseId).isEmpty()) {
    throw new IllegalArgumentException("Course not found: " + courseId);
}
```

### Design Decisions

**Why IllegalArgumentException?**:
- Standard Java exception for bad arguments
- Checked vs unchecked: Use unchecked (RuntimeException) for programming errors
- Caller can catch and display to user

**Why Optional for lookups?**:
- Clear semantic: entity may or may not exist
- Forces caller to handle missing entity case
- Better than returning null

**Why validation in private methods?**:
- DRY principle: validation logic reused across methods
- Centralized: easy to find and modify rules
- Testable: can unit test validation separately

### Expected Outcome

After this prompt, you should have:
- PlannerService with comprehensive business logic
- Validation methods for all entity types
- Query methods for common use cases
- Proper exception handling
- Clear separation between validation and persistence

### Verification Halt

**Check before proceeding**:

- [ ] All public methods validate inputs
- [ ] Duplicate checking for course codes
- [ ] Cross-entity validation (task references valid course)
- [ ] Query methods return sorted/filtered results
- [ ] All validation failures throw IllegalArgumentException with message
- [ ] Service is stateless (only holds model reference)

**Test Scenario**:
```java
PlannerService service = new PlannerService(model);

// Should succeed
Course c1 = service.addCourse("Java Programming", "CS101", "Dr. Smith", 3);

// Should fail - duplicate code
try {
    Course c2 = service.addCourse("Python", "CS101", "Dr. Jones", 3);
    assert false : "Should have thrown exception";
} catch (IllegalArgumentException e) {
    assert e.getMessage().contains("already exists");
}
```

---

**Next**: PROMPT-F: User Interface (Swing Components)  
**Prev**: PROMPT-D: Domain Models

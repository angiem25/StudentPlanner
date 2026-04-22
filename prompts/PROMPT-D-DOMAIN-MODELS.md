# PROMPT D: DOMAIN MODELS (STUDENT, COURSE, TASK)

## **MANDATORY**: Create the business entities that represent the student planner domain.

## Module of Thought

**Phase**: Domain Model Implementation  
**Purpose**: Define business entities with proper encapsulation and relationships  
**Dependencies**: PROMPT-C (Base MVC Classes)

### Entity Design Principles

1. **Identity**: Each entity has a unique, immutable ID (UUID)
2. **Mutability**: Business data can change, identity cannot
3. **Encapsulation**: Private fields with public getters/setters
4. **Validation**: Basic validation in setters (null checks, etc.)
5. **Equality**: Based on ID, not on field values

### Entity Breakdown

**Student**:
- Fields: id, firstName, lastName, email, studentId, year, major
- Behavior: getFullName() convenience method
- Validation: Names and email should not be empty

**Course**:
- Fields: id, name, code, instructor, credits
- Behavior: Simple data holder
- Validation: Credits between 1-6 typically

**Task**:
- Fields: id, title, description, dueDate, priority (enum), completed (boolean), courseId
- Behavior: complete(), incomplete(), toggle completion
- Priority enum: LOW, MEDIUM, HIGH
- Validation: Due date should be in the future

### File Locations

```
src/main/java/planner/model/
├── Student.java
├── Course.java
├── Task.java
└── PlannerModel.java (main model)
```

### Implementation Details

**UUID for Identity**:
```java
private final String id;

public Student(...) {
    this.id = UUID.randomUUID().toString();
    // ... other fields
}
```

**Equals and HashCode**:
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Student student = (Student) o;
    return id.equals(student.id);  // Identity-based equality
}

@Override
public int hashCode() {
    return id.hashCode();
}
```

**Priority Enum (inside Task)**:
```java
public enum Priority {
    LOW, MEDIUM, HIGH
}
```

### Design Considerations

**Why UUID as String?**:
- Easier to debug (readable)
- Serialization friendly
- Can convert to/from actual UUID if needed

**Why courseId in Task instead of Course reference?**:
- Looser coupling
- Easier serialization
- Prevents circular references
- Course can be looked up when needed

**Why final id but mutable other fields?**:
- Identity never changes (entity lifecycle)
- Business data can evolve over time
- This is standard domain-driven design

### Expected Outcome

After this prompt, you should have:
- Three entity classes with proper encapsulation
- UUID-based identity system
- Proper equals/hashCode implementations
- Task.Priority enum
- Basic validation
- PlannerModel extending AbstractModel with collections

### PlannerModel Design

```java
public class PlannerModel extends AbstractModel {
    private Student currentStudent;
    private final List<Course> courses;
    private final List<Task> tasks;
    
    // CRUD operations that fire events
    public void addCourse(Course course) { ... fireModelChanged("COURSE_ADDED", course); }
    public void removeCourse(Course course) { ... fireModelChanged("COURSE_REMOVED", course); }
    // ... etc
}
```

### Verification Halt

**Check before proceeding**:

- [ ] Each entity has UUID-based immutable id
- [ ] Equals/hashCode based on id only
- [ ] Proper encapsulation (private fields)
- [ ] Task.Priority enum defined
- [ ] PlannerModel extends AbstractModel
- [ ] All CRUD operations fire appropriate events
- [ ] No UI dependencies in domain models

**Test Scenario**:
```java
// Two students with same data but different IDs should not be equal
Student s1 = new Student("John", "Doe", ...);
Student s2 = new Student("John", "Doe", ...);
assert !s1.equals(s2) : "Different UUIDs mean different entities";

// Same student reference should be equal to itself
assert s1.equals(s1) : "Same reference should be equal";
```

---

**Next**: PROMPT-E: Service Layer (Business Logic)  
**Prev**: PROMPT-C: Base MVC Framework Classes

# PROMPT-L: Testing Strategy and Implementation

## Overview
Implement comprehensive unit tests for the Student Planner application using JUnit 5. Tests should cover the Model, Service, and Persistence layers, ensuring business logic validation, data integrity, and correct CRUD operations.

## Testing Philosophy
- **Unit Tests**: Test individual classes in isolation
- **Integration Tests**: Test interactions between components
- **Test-Driven Development**: Write tests before or alongside implementation
- **High Coverage**: Aim for >80% code coverage
- **Fast Execution**: Tests should run quickly (no UI, no external dependencies)

## Module of Thought for Test Implementation

### Step 1: Analyze Test Targets
Identify which classes need testing:
- **Model Layer**: `PlannerModel`, domain entities (`Student`, `Course`, `Task`, `Event`)
- **Service Layer**: `PlannerService` (business logic, validation)
- **Persistence Layer**: `PlannerRepository` (file I/O, CSV parsing)
- **UI Layer**: Controller/View tests (optional, use mocking frameworks)

### Step 2: Set Up Test Infrastructure
- Add JUnit 5 dependencies to `build.gradle` (already present)
- Create test directory structure: `src/test/java/planner/`
- Use `@TempDir` for file system isolation in repository tests
- Use `@BeforeEach` and `@AfterEach` for test setup/teardown

### Step 3: Write Model Layer Tests
Test `PlannerModel`:
- **CRUD Operations**: Add, remove, update for students, courses, tasks, events
- **Query Operations**: Get by ID, get by code, filter by criteria
- **State Changes**: Complete/incomplete tasks, student changes
- **Defensive Copies**: Ensure returned lists are copies, not references
- **Event Notifications**: Verify `fireModelChanged` is called (use listeners)

### Step 4: Write Service Layer Tests
Test `PlannerService`:
- **Validation Logic**: Empty strings, invalid emails, out-of-range values
- **Business Rules**: Duplicate course codes, past due dates, non-existent references
- **Query Operations**: Upcoming tasks, overdue tasks, priority filtering
- **Edge Cases**: Null inputs, whitespace-only strings, boundary values
- **Exception Handling**: Verify correct exceptions thrown for invalid inputs

### Step 5: Write Persistence Layer Tests
Test `PlannerRepository`:
- **Save/Load Operations**: Verify data persistence across save/load cycles
- **CSV Escaping**: Handle commas, quotes, newlines in data
- **Null Handling**: Handle null course IDs, null students
- **Multiple Items**: Save/load multiple courses, tasks, events
- **State Preservation**: Task completion status, task colors
- **Error Handling**: Malformed CSV, missing files, empty directories
- **Cleanup**: Delete all data functionality

### Step 6: Test Naming and Organization
- Use descriptive test names: `testAddCourseWithValidData`
- Use `@DisplayName` for human-readable test descriptions
- Group related tests in the same test class
- Follow package structure: `test/java/planner/model/PlannerModelTest.java`

### Step 7: Run and Verify Tests
- Run all tests: `./gradlew test`
- Run specific test class: `./gradlew test --tests PlannerModelTest`
- View test report: `build/reports/tests/test/index.html`
- Ensure all tests pass before committing

## Implementation Details

### Test Class Structure
```java
package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PlannerModelTest {
    private PlannerModel model;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
    }
    
    @Test
    @DisplayName("Should add course successfully")
    void testAddCourse() {
        Course course = new Course("Name", "CODE", "Instructor", 3);
        model.addCourse(course);
        assertEquals(1, model.getCourses().size());
    }
}
```

### Repository Test Pattern
```java
class PlannerRepositoryTest {
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() throws IOException {
        System.setProperty("user.dir", tempDir.toString());
        repository = new PlannerRepository();
    }
    
    @AfterEach
    void tearDown() throws IOException {
        repository.deleteAllData();
    }
}
```

### Service Validation Test Pattern
```java
@Test
@DisplayName("Should reject empty course name")
void testAddCourseEmptyName() {
    assertThrows(IllegalArgumentException.class, () -> {
        service.addCourse("", "CS101", "Dr. Smith", 3);
    });
}
```

## Test Coverage Goals

### Model Layer (PlannerModel)
- [x] Student management (set, get, null handling)
- [x] Course CRUD (add, remove, update, find by ID/code)
- [x] Task CRUD (add, remove, update, complete/incomplete)
- [x] Task queries (by course, completed, incomplete)
- [x] Event CRUD (add, remove, update)
- [x] Event queries (by date, by range)
- [x] Clear all functionality
- [x] Defensive copy behavior

### Service Layer (PlannerService)
- [x] Student profile creation and validation
- [x] Course operations with business rules
- [x] Task operations with validation
- [x] Query operations (upcoming, overdue, by priority)
- [x] Edge case handling (nulls, whitespace, boundaries)

### Persistence Layer (PlannerRepository)
- [x] Save/load student data
- [x] Save/load course data
- [x] Save/load task data
- [x] Save/load event data
- [x] Complete model save/load
- [x] CSV escaping (commas, quotes)
- [x] Null handling (course IDs, students)
- [x] Multiple items handling
- [x] State preservation (completion, colors)
- [x] Delete all data
- [x] Error handling (malformed CSV, empty lines)

## Running Tests

### Command Line
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PlannerModelTest

# Run specific test method
./gradlew test --tests PlannerModelTest.testAddCourse

# Run with verbose output
./gradlew test --info

# Generate coverage report (requires JaCoCo plugin)
./gradlew test jacocoTestReport
```

### IDE Integration
- IntelliJ IDEA: Right-click test class → Run 'PlannerModelTest'
- VS Code: Click "Run Test" button above each test method
- Eclipse: Right-click → Run As → JUnit Test

## Test Best Practices

### DO
- Use descriptive test names that explain what is being tested
- Test one thing per test method
- Use `@DisplayName` for human-readable descriptions
- Set up fresh state in `@BeforeEach`
- Clean up in `@AfterEach`
- Use `@TempDir` for file system operations
- Test both positive and negative cases
- Test edge cases and boundary conditions
- Use assertions with clear error messages

### DON'T
- Don't test multiple scenarios in one test
- Don't rely on test execution order
- Don't use hardcoded file paths (use temp directories)
- Don't leave test data files after tests complete
- Don't ignore test failures
- Don't write tests that depend on external services
- Don't test UI components in unit tests (use integration tests)

## Verification Protocol

### After Test Implementation
1. Run `./gradlew test` - all tests should pass
2. Check test report: `build/reports/tests/test/index.html`
3. Verify test coverage is >80%
4. Ensure tests run in under 10 seconds
5. Confirm no test data files remain in project directory

### Continuous Integration
- Tests should run automatically on each commit
- Failed tests should block merge
- Coverage reports should be generated
- Test results should be visible in CI dashboard

## Example Test Files Created

### PlannerModelTest.java
- 30+ test methods covering all CRUD operations
- Tests for defensive copy behavior
- Tests for query operations
- Tests for state changes (completion, updates)

### PlannerServiceTest.java
- 25+ test methods covering business logic
- Validation tests for all input fields
- Business rule tests (duplicates, references)
- Query operation tests (upcoming, overdue, priority)

### PlannerRepositoryTest.java
- 20+ test methods covering persistence
- CSV escaping tests
- Null handling tests
- Multiple items tests
- Error handling tests

## Future Test Enhancements

### Integration Tests
- Test end-to-end workflows (add course → add task → save → load)
- Test with actual file system (not just temp dir)
- Test with realistic data volumes

### UI Tests
- Use Swing testing frameworks (AssertJ Swing, Fest)
- Test controller/view interactions
- Test user input validation in UI

### Performance Tests
- Test with large datasets (1000+ tasks)
- Measure save/load performance
- Test memory usage

## Dependencies

### build.gradle
```gradle
dependencies {
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.2'
    
    // Optional: Mocking framework
    testImplementation 'org.mockito:mockito-core:5.3.1'
    
    // Optional: AssertJ for fluent assertions
    testImplementation 'org.assertj:assertj-core:3.24.2'
    
    // Optional: JaCoCo for coverage
    testImplementation 'org.jacoco:org.jacoco.core:0.8.10'
}

test {
    useJUnitPlatform()
}
```

## Troubleshooting

### Tests Fail with File Not Found
- Ensure `@TempDir` is used for file operations
- Check that working directory is set correctly
- Verify file paths are relative, not absolute

### Tests Pass but Coverage is Low
- Add tests for uncovered branches
- Test exception paths
- Test boundary conditions
- Test null/empty input cases

### Tests Are Slow
- Remove external dependencies
- Use in-memory alternatives where possible
- Parallelize test execution
- Mock file system operations

### Tests Are Flaky
- Ensure proper cleanup in `@AfterEach`
- Avoid time-dependent assertions
- Use deterministic test data
- Don't rely on file system state

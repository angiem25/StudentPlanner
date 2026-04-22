# PROMPT F: USER INTERFACE (SWING COMPONENTS)

## **MANDATORY**: Implement the Swing-based user interface with automatic model update handling.

## Module of Thought

**Phase**: Presentation Layer Implementation  
**Purpose**: Create user interface that responds to model changes  
**Dependencies**: PROMPT-E (Service Layer)

### UI Architecture Principles

**Separation of Concerns**:
- View displays data, doesn't modify it directly
- Controller handles user actions, delegates to service
- Model changes trigger automatic UI updates via observer pattern

**Tabbed Interface Design**:
- Tab 1: Student Profile (forms, buttons)
- Tab 2: Course Management (list, forms, buttons)
- Tab 3: Task Management (list, forms, buttons)

### File Locations

```
src/main/java/planner/ui/
├── PlannerView.java
└── PlannerController.java
```

### PlannerView Structure

```java
public class PlannerView extends AbstractView {
    // UI Components
    private JFrame frame;
    private JTabbedPane tabbedPane;
    
    // Student tab components
    private JTextField firstNameField, lastNameField, emailField, ...;
    private JButton saveStudentButton;
    
    // Course tab components
    private JList<Course> courseList;
    private DefaultListModel<Course> courseListModel;
    private JTextField courseNameField, courseCodeField, ...;
    
    // Task tab components
    private JList<Task> taskList;
    private DefaultListModel<Task> taskListModel;
    private JTextField taskTitleField, taskDueDateField, ...;
    private JComboBox<Task.Priority> taskPriorityCombo;
    
    // Constructor and initialization
    public PlannerView(PlannerModel model, PlannerController controller) {
        super(model, controller);
        initializeUI();
    }
    
    // ModelListener implementation - CRITICAL METHOD
    @Override
    public void modelChanged(ModelEvent event) {
        // This is called automatically when model changes
        // Must update UI on Event Dispatch Thread (SwingUtilities.invokeLater)
    }
}
```

### Critical Implementation: modelChanged()

**This method is the heart of the MVC pattern**:

```java
@Override
public void modelChanged(ModelEvent event) {
    SwingUtilities.invokeLater(() -> {
        if (event == null) {
            refreshAll();
            return;
        }
        
        switch (event.getEventName()) {
            case "STUDENT_CHANGED":
                refreshStudentInfo();
                break;
            case "COURSE_ADDED":
            case "COURSE_REMOVED":
            case "COURSE_UPDATED":
                refreshCourseList();
                refreshTaskCourseCombo();
                break;
            case "TASK_ADDED":
            case "TASK_REMOVED":
            case "TASK_UPDATED":
            case "TASK_COMPLETED":
                refreshTaskList();
                break;
            // ... etc
        }
    });
}
```

**Why SwingUtilities.invokeLater()?**
- Model events may fire from any thread
- Swing components must be updated on Event Dispatch Thread (EDT)
- This ensures thread safety for UI updates

### Refresh Methods

```java
private void refreshCourseList() {
    PlannerModel model = (PlannerModel) getModel();
    courseListModel.clear();
    for (Course course : model.getCourses()) {
        courseListModel.addElement(course);
    }
}
```

### Event Handling Pattern

```java
// In button action listener:
private void onAddCourse() {
    if (getController() instanceof PlannerController) {
        PlannerController controller = (PlannerController) getController();
        controller.addCourse(
            courseNameField.getText().trim(),
            courseCodeField.getText().trim(),
            instructorField.getText().trim(),
            creditsField.getText().trim()
        );
    }
}
```

**Pattern**: View → Controller → Service → Model → Observer → View Update

### Design Considerations

**List Models**:
- Use DefaultListModel<T> for JList
- Clear and repopulate on refresh (simplest approach)
- Alternative: use custom model that listens directly

**Form Validation**:
- Basic validation in UI (empty fields)
- Format validation (dates, numbers)
- Service layer handles business rules

**Error Display**:
```java
public void showError(String message) {
    JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
}
```

### Expected Outcome

After this prompt, you should have:
- PlannerView extending AbstractView with tabbed interface
- PlannerController handling all user actions
- Automatic UI updates via modelChanged()
- Form components for all entity types
- List displays with selection handling
- Error dialog methods

### Verification Halt

**Check before proceeding**:

- [ ] PlannerView extends AbstractView
- [ ] modelChanged() implemented with event switching
- [ ] SwingUtilities.invokeLater() used for all UI updates
- [ ] All form fields have proper names and types
- [ ] Button actions delegate to Controller
- [ ] Lists use DefaultListModel
- [ ] Error dialogs implemented
- [ ] Can explain the View → Controller → Service → Model → View flow

**Test Scenario**:
```java
// User clicks "Add Course" button
// 1. Button action listener calls controller.addCourse(...)
// 2. Controller validates and calls service.addCourse(...)
// 3. Service validates and calls model.addCourse(course)
// 4. Model fires "COURSE_ADDED" event
// 5. View.modelChanged() receives event
// 6. View.refreshCourseList() updates JList
// 7. User sees new course in list
```

---

**Next**: PROMPT-G: Persistence Layer (Data Storage)  
**Prev**: PROMPT-E: Service Layer

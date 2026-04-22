# PROMPT A: CORE MVC CONTRACTS

## **MANDATORY**: Generate **Model.java**, **View.java**, and **Controller.java** simultaneously. They are co-dependent and must be considered as a unit.

## Module of Thought

**Phase**: Framework Foundation  
**Purpose**: Define the minimal viable interfaces for the MVC framework  
**Dependencies**: PROMPT-00 (Architecture Overview)

### Critical Design Rules

**Model Interface**:
- Declares `void notifyChanged(ModelEvent event)`
- Must be capable of notifying observers of state changes
- Knows nothing about View or Controller

**View Interface**:
- Declares `Model getModel()` and `Controller getController()`
- Must be able to access its associated Model and Controller
- Responsible for displaying data and capturing input

**Controller Interface**:
- Declares `Model getModel()` and `View getView()`
- Must be able to access its associated Model and View
- Responsible for interpreting user actions

### File Locations

```
src/main/java/mvc/
├── Model.java
├── View.java
└── Controller.java
```

### Example Signatures

```java
// Model.java
package mvc;

public interface Model {
    void notifyChanged(ModelEvent event);
}

// View.java
package mvc;

public interface View {
    Model getModel();
    Controller getController();
}

// Controller.java
package mvc;

public interface Controller {
    Model getModel();
    View getView();
}
```

### Design Considerations

**Why these interfaces matter**:
- They define the **contract** that all MVC components must follow
- They enable **polymorphism** (any Model works with any View, etc.)
- They enforce **separation of concerns** at the interface level
- They make the framework **reusable** for any application

**Key Insights**:
- The Model doesn't know about View or Controller (loose coupling)
- The View knows about both Model and Controller
- The Controller knows about both Model and View
- All relationships are through interfaces, not concrete classes

### Expected Outcome

After this prompt, you should have:
- Three minimal, clean interfaces
- No implementation details in interfaces
- Clear documentation of what each component does
- Framework that knows nothing about planners (reusable)

### Verification Halt

**Check before proceeding**:

- [ ] Each interface has minimal, necessary methods only
- [ ] No implementation details in interfaces (no `ArrayList`, no `Swing`, etc.)
- [ ] Interfaces are in `mvc` package
- [ ] Documentation explains purpose of each method
- [ ] Can explain why View needs Controller access
- [ ] Can explain why Model doesn't need View/Controller access

**Code Review Questions**:
1. Does Model interface contain any UI-related methods? (Should be: NO)
2. Do interfaces reference concrete classes? (Should be: NO)
3. Are the method names clear and descriptive? (Should be: YES)

### Error Recovery Protocol

If verification fails:
1. Check for framework leakage (planner-specific code in mvc package)
2. Verify minimal interface principle (only essential methods)
3. Ensure proper package structure

---

**Next**: PROMPT-B: ModelEvent and ModelListener Infrastructure  
**Prev**: PROMPT-00: Architecture Overview

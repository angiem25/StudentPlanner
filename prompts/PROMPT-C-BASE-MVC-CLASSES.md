# PROMPT C: BASE MVC FRAMEWORK CLASSES

## **MANDATORY**: Implement abstract base classes that provide common functionality for all MVC components.

## Module of Thought

**Phase**: Framework Implementation  
**Purpose**: Create reusable base classes with observer pattern support  
**Dependencies**: PROMPT-A (Contracts), PROMPT-B (Event Infrastructure)

### Why Abstract Classes?

**The DRY Principle**: Don't Repeat Yourself
- Every Model needs listener management
- Every View needs model/controller references
- Every Controller needs model/view references
- Put common code in base classes, let specifics be in subclasses

### Design Requirements

**AbstractModel**:
- Implements `Model` interface
- Manages list of `ModelListener` objects
- Provides `addModelListener()` and `removeModelListener()`
- Implements `notifyChanged()` to fire events to all listeners
- Convenience methods: `fireModelChanged(String)` and `fireModelChanged(String, Object)`

**AbstractView**:
- Implements `View` interface
- Implements `ModelListener` interface (receives updates)
- Stores references to Model and Controller
- Constructor automatically registers as listener with model
- Provides `setModel()` that updates listener registration

**AbstractController**:
- Implements `Controller` interface
- Stores references to Model and View
- Provides setters for both references
- Minimal implementation (mainly storage)

### File Locations

```
src/main/java/mvc/
├── AbstractModel.java
├── AbstractView.java
└── AbstractController.java
```

### Implementation Details

**AbstractModel**:
```java
public abstract class AbstractModel implements Model {
    private final List<ModelListener> listeners = new ArrayList<>();
    
    public void addModelListener(ModelListener listener) {
        // Add if not null and not already present
    }
    
    public void removeModelListener(ModelListener listener) {
        // Remove from list
    }
    
    @Override
    public void notifyChanged(ModelEvent event) {
        // Iterate through listeners and call modelChanged()
    }
    
    // Convenience methods for subclasses
    protected void fireModelChanged(String eventName) { ... }
    protected void fireModelChanged(String eventName, Object data) { ... }
}
```

**AbstractView**:
```java
public abstract class AbstractView implements View, ModelListener {
    private Model model;
    private Controller controller;
    
    public AbstractView(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        // Register as listener if model is AbstractModel
        if (model instanceof AbstractModel) {
            ((AbstractModel) model).addModelListener(this);
        }
    }
    
    public void setModel(Model model) {
        // Unregister from old model, register with new model
    }
    
    // Abstract method that subclasses must implement
    public abstract void modelChanged(ModelEvent event);
}
```

### Critical Implementation Details

**Listener Safety**:
- Check for null before adding
- Check for duplicates (don't add same listener twice)
- Handle case where model is not AbstractModel (use instanceof check)

**Thread Safety Consideration**:
- In this implementation, we're using ArrayList (not thread-safe)
- For production, consider CopyOnWriteArrayList
- UI updates should be on Event Dispatch Thread (SwingUtilities.invokeLater)

**Memory Leak Prevention**:
- When model changes, remove old listener
- When view is disposed, remove listener
- Reference management is crucial

### Expected Outcome

After this prompt, you should have:
- Three abstract classes that implement the core interfaces
- Working observer pattern (can add/remove listeners)
- Automatic listener registration in AbstractView
- Convenience methods for firing events
- Foundation for concrete implementations

### Verification Halt

**Check before proceeding**:

- [ ] AbstractModel manages listener list properly
- [ ] AbstractView automatically registers as listener in constructor
- [ ] AbstractView properly updates listener registration in setModel()
- [ ] notifyChanged() iterates through all listeners
- [ ] No memory leaks in listener management
- [ ] Can explain CopyOnWriteArrayList vs ArrayList tradeoffs

**Test Scenario**:
```java
// Create a test model extending AbstractModel
TestModel model = new TestModel();
TestView view = new TestView(model, null);

// When model fires event, view should receive it
model.fireModelChanged("TEST_EVENT");
// view.modelChanged() should have been called with ModelEvent
```

---

**Next**: PROMPT-D: Domain Models (Student, Course, Task)  
**Prev**: PROMPT-B: ModelEvent and ModelListener Infrastructure

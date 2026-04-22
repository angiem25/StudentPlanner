# PROMPT B: MODEL EVENT & LISTENER INFRASTRUCTURE

## **CRITICAL**: Implement the observer pattern infrastructure that enables automatic UI updates.

## Module of Thought

**Phase**: Observer Pattern Implementation  
**Purpose**: Create the event system for model change notifications  
**Dependencies**: PROMPT-A (Core MVC Contracts)

### Conceptual Understanding

**The Observer Pattern in MVC**:
```
Model (Observable) ---notifies---> ModelListener (Observer)
        |                              |
        | fires ModelEvent             | receives ModelEvent
        |                              |
        v                              v
   notifyChanged()               modelChanged()
```

**Key Insight**: This decouples the Model from the View. The Model doesn't know WHO is listening, only THAT someone might be listening.

### Design Requirements

**ModelEvent**:
- Encapsulates information about a model change
- Must include: event name, source (model), optional data payload
- Immutable (created, then passed around, never modified)
- Serializable information only (no UI components)

**ModelListener**:
- Simple callback interface
- Single method: `modelChanged(ModelEvent event)`
- Implemented by Views to receive update notifications
- Must be able to handle events on any thread (UI thread safety)

### File Locations

```
src/main/java/mvc/
├── ModelEvent.java
└── ModelListener.java
```

### Example Signatures

```java
// ModelEvent.java
package mvc;

public class ModelEvent {
    private final String eventName;
    private final Object source;
    private final Object data;
    
    public ModelEvent(String eventName, Object source, Object data) {
        this.eventName = eventName;
        this.source = source;
        this.data = data;
    }
    
    // Getters...
}

// ModelListener.java
package mvc;

public interface ModelListener {
    void modelChanged(ModelEvent event);
}
```

### Design Decisions

**Why Object for data?**:
- Allows flexibility (any type of data can be passed)
- Caller knows what type to expect based on event name
- Could use generics, but Object provides simplicity

**Why Object for source?**:
- Could be any Model implementation
- Listener can cast if needed
- Keeps event class generic and reusable

**Why final fields?**:
- Events should be immutable
- Prevents accidental modification after creation
- Thread-safe by design

### Expected Outcome

After this prompt, you should have:
- ModelEvent class with proper encapsulation
- ModelListener interface for callback registration
- Understanding of event-driven architecture
- Foundation for automatic UI updates

### Event Naming Convention

Establish these event names early (used later):
- `"STUDENT_CHANGED"` - Student profile updated
- `"COURSE_ADDED"` / `"COURSE_REMOVED"` / `"COURSE_UPDATED"`
- `"TASK_ADDED"` / `"TASK_REMOVED"` / `"TASK_UPDATED"`
- `"TASK_COMPLETED"` / `"TASK_INCOMPLETED"`

### Verification Halt

**Check before proceeding**:

- [ ] ModelEvent has immutable fields (final)
- [ ] ModelEvent has proper getters
- [ ] ModelListener is a functional interface (single method)
- [ ] No UI dependencies in event system
- [ ] Event names are documented
- [ ] Can explain why Object type is used for data

**Test Scenario**:
```java
// Should compile and make sense
ModelEvent event = new ModelEvent("TASK_ADDED", model, newTask);
System.out.println(event.getEventName()); // "TASK_ADDED"
System.out.println(event.getSource());    // model
System.out.println(event.getData());      // newTask
```

---

**Next**: PROMPT-C: Base MVC Framework Classes  
**Prev**: PROMPT-A: Core MVC Contracts

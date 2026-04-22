# PROMPT H: MAIN APPLICATION ENTRY POINT

## **MANDATORY**: Create the application entry point that wires all MVC components together.

## Module of Thought

**Phase**: Application Integration  
**Purpose**: Bootstrap the application and initialize all components  
**Dependencies**: PROMPT-G (Persistence Layer)

### Wiring All Components

The Main class is the composition root where all pieces come together:

```
┌─────────────────────────────────────────────────────────────┐
│                        Main.java                             │
│                                                              │
│  1. Create Model                                             │
│  2. Create View (passing Model)                               │
│  3. Create Service (passing Model)                          │
│  4. Create Controller (passing Model, View, Service)        │
│  5. Set Controller in View                                   │
│  6. Load existing data                                       │
│  7. Set up shutdown hook for saving                         │
└─────────────────────────────────────────────────────────────┘
```

### Dependency Injection Pattern

```java
public class Main {
    public static void main(String[] args) {
        // 1. Model first (no dependencies)
        PlannerModel model = new PlannerModel();
        
        // 2. View (needs Model, will get Controller later)
        PlannerView view = new PlannerView(model, null);
        
        // 3. Service (needs Model)
        PlannerService service = new PlannerService(model);
        
        // 4. Controller (needs Model, View, Service)
        PlannerController controller = new PlannerController(model, view, service);
        
        // 5. Wire Controller into View
        view.setController(controller);
        
        // 6. Load existing data
        PlannerRepository repository = new PlannerRepository();
        repository.loadPlannerData(model);
        
        // 7. Setup auto-save on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                repository.savePlannerData(model);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not save planner data", e);
            }
        }));
    }
}
```

### The Circular Reference Problem

**Problem**: View needs Controller, Controller needs View

**Solution**: Two-phase initialization
1. Create View with null Controller
2. Create Controller with View
3. Set Controller in View

This breaks the circular dependency at construction time.

### Look and Feel

```java
// Set system look and feel for native appearance
try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {
    LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
}
```

### Logging Setup

```java
private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
```

**Why logging vs. System.out?**:
- Configurable (can disable in production)
- Levels (INFO, WARNING, SEVERE)
- Can log to file, console, or external service

### Expected Outcome

After this prompt, you should have:
- Main.java with main() method
- All components wired together
- Circular reference resolved
- Data loading on startup
- Auto-save on shutdown
- System look and feel
- Logging configuration

### Verification Halt

**Check before proceeding**:

- [ ] All components created in correct order
- [ ] Circular reference (View ↔ Controller) resolved
- [ ] Data loaded on startup
- [ ] Shutdown hook registered
- [ ] System look and feel set
- [ ] Logging working
- [ ] Application launches without errors

**Test Scenario**:
```java
// Run the application
public static void main(String[] args) {
    // Should launch without errors
    // Window should appear
    // Can create student, courses, tasks
    // Close window, reopen - data should persist
}
```

---

**Next**: PROMPT-I: Documentation and README  
**Prev**: PROMPT-G: Persistence Layer

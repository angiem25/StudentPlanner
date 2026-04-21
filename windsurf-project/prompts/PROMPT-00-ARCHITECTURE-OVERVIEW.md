# PROMPT 0: ARCHITECTURE OVERVIEW & MENTAL MODEL

## **CRITICAL**: Before generating any code, internalize the following diagram and component relationships.

**MANDATORY Mental Commitments:**
- The **Model** owns the data and notifies listeners via `ModelEvent`
- The **View** displays data and captures user input; it knows its Controller
- The **Controller** interprets user actions and updates the Model
- The **Framework** provides reusable observer-pattern plumbing
- The **Planner** components are concrete implementations of framework interfaces

## Module of Thought

**Phase**: Foundation  
**Purpose**: Establish architectural foundation before any code generation  
**Dependencies**: None

### Conceptual Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      MVC FRAMEWORK                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │   Model     │    │    View     │    │   Controller    │  │
│  │  (Interface)│◄───│  (Interface)│◄───│   (Interface)   │  │
│  └──────┬──────┘    └──────┬──────┘    └────────┬────────┘  │
│         │                  │                     │           │
│  ┌──────▼──────────────────▼─────────────────────▼────────┐ │
│  │              Observer Pattern (Event/Listener)          │ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    PLANNER APPLICATION                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │
│  │ PlannerModel │  │ PlannerView │  │PlannerController │   │
│  │   (extends)  │  │  (extends)   │  │    (extends)     │   │
│  │ AbstractModel│  │ AbstractView │  │ AbstractController│  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘   │
│         │                 │                    │            │
│         └─────────────────┴────────────────────┘             │
│                          │                                   │
│         ┌────────────────┴────────────────┐                  │
│         │         Domain Models           │                  │
│         │  ┌─────────┐ ┌─────────┐ ┌──────┐ │                │
│         │  │ Student │ │ Course  │ │ Task │ │                │
│         │  └─────────┘ └─────────┘ └──────┘ │                │
│         └───────────────────────────────────┘                │
└─────────────────────────────────────────────────────────────┘
```

### Expected Outcome

After this prompt, you should be able to **explain the flow of an "Add Task" action** before writing any code.

### Verification Halt

Write down the sequence of method calls that occur when a user clicks an "Add Task" button in the final application:

```
1. View.actionPerformed() → captures user input
2. Controller.actionPerformed() → interprets action
3. Model.addTask() → updates data
4. Model.notifyChanged(ModelEvent) → triggers notification
5. View.modelChanged() → updates UI
```

**Confirm you understand:**
- The role of `ModelListener` (observer interface)
- The role of `ModelEvent` (encapsulates change information)
- The separation between framework (reusable) and application (specific)

### Pre-Implementation Checklist

- [ ] Can explain MVC pattern without reference to planner specifics
- [ ] Understand observer pattern for decoupled updates
- [ ] Can trace data flow from user action to UI update
- [ ] Recognize which components are framework vs. application

---

**Next**: PROMPT-A: Core MVC Contracts
**Prev**: N/A

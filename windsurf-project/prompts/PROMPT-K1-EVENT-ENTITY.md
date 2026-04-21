# PROMPT K1: CALENDAR EVENT ENTITY

## **MANDATORY**: Create the Event entity for calendar scheduling distinct from Tasks.

## Module of Thought

**Phase**: Domain Model Extension  
**Purpose**: Add Event entity for time-based calendar scheduling (SRS Requirement R2.3)  
**Dependencies**: PROMPT-D (Domain Models), PROMPT-E (Service Layer)

### Feature Requirements

**SRS Specification**:
- Events are distinct from Tasks (different use cases)
- Events have start time and end time (not just due date)
- Events appear on calendar in time slots
- Events can span multiple hours
- Events can be created, edited, and deleted

**Event vs Task Distinction**:

| Feature | Task | Event |
|---------|------|-------|
| **Purpose** | To-do item, assignment | Scheduled activity, meeting |
| **Time** | Due date (deadline) | Start time + End time (duration) |
| **Completion** | Can be marked complete | No completion status |
| **Priority** | High/Medium/Low | N/A |
| **Display** | Month view (due date) | Day/Week view (time slot) |

### Entity Design

```java
public class Event {
    private final String id;           // UUID (immutable)
    private String title;              // Event name
    private String description;        // Event details
    private LocalDateTime startDateTime;  // When event starts
    private LocalDateTime endDateTime;    // When event ends
}
```

### Implementation Details

**Constructors**:
```java
public Event(String title, String description, 
             LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.description = description;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
}
```

**Key Methods**:

```java
// Check if event occurs on a specific date
public boolean occursOn(LocalDate date) {
    LocalDateTime dayStart = date.atStartOfDay();
    LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
    return overlapsWith(dayStart, dayEnd);
}

// Check if event overlaps with a time range
public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
    return !this.startDateTime.isAfter(end) && 
           !this.endDateTime.isBefore(start);
}
```

### Integration with PlannerModel

**Add to PlannerModel**:
```java
public class PlannerModel extends AbstractModel {
    private final List<Event> events;
    
    public PlannerModel() {
        this.events = new ArrayList<>();
    }
    
    // Event management
    public List<Event> getEvents() { return new ArrayList<>(events); }
    
    public List<Event> getEventsForDate(LocalDate date) {
        return events.stream()
            .filter(event -> event.occursOn(date))
            .sorted((e1, e2) -> e1.getStartDateTime()
                .compareTo(e2.getStartDateTime()))
            .toList();
    }
    
    public List<Event> getEventsInRange(LocalDateTime start, 
                                         LocalDateTime end) {
        return events.stream()
            .filter(event -> event.overlapsWith(start, end))
            .sorted((e1, e2) -> e1.getStartDateTime()
                .compareTo(e2.getStartDateTime()))
            .toList();
    }
    
    public void addEvent(Event event) {
        if (event != null && !events.contains(event)) {
            events.add(event);
            fireModelChanged("EVENT_ADDED", event);
        }
    }
    
    public void removeEvent(Event event) {
        if (events.remove(event)) {
            fireModelChanged("EVENT_REMOVED", event);
        }
    }
    
    public void updateEvent(Event event) {
        int index = events.indexOf(event);
        if (index >= 0) {
            events.set(index, event);
            fireModelChanged("EVENT_UPDATED", event);
        }
    }
}
```

**Update clearAll()**:
```java
public void clearAll() {
    courses.clear();
    tasks.clear();
    events.clear();  // Add this
    currentStudent = null;
    fireModelChanged("PLANNER_CLEARED", null);
}
```

### Persistence Integration

**File Format** (CSV):
```
id,title,description,startDateTime,endDateTime
uuid-123,Team Meeting,Weekly sync,2026-04-21 14:00:00,2026-04-21 15:00:00
uuid-456,Lunch Break,Quick lunch,2026-04-21 12:00:00,2026-04-21 13:00:00
```

**PlannerRepository Methods**:

```java
// Add constant
private static final String EVENTS_FILE = "events.csv";

// Save events
private void saveEvents(List<Event> events) throws IOException {
    Path eventsFile = Paths.get(DATA_DIR, EVENTS_FILE);
    try (PrintWriter writer = new PrintWriter(
            new FileWriter(eventsFile.toFile()))) {
        for (Event event : events) {
            writer.printf("%s,%s,%s,%s,%s%n",
                event.getId(),
                escapeCsv(event.getTitle()),
                escapeCsv(event.getDescription()),
                event.getStartDateTime().format(DATE_FORMATTER),
                event.getEndDateTime().format(DATE_FORMATTER)
            );
        }
    }
}

// Load events
private List<Event> loadEvents() throws IOException {
    List<Event> events = new ArrayList<>();
    Path eventsFile = Paths.get(DATA_DIR, EVENTS_FILE);
    if (!Files.exists(eventsFile)) {
        return events;
    }
    
    try (BufferedReader reader = new BufferedReader(
            new FileReader(eventsFile.toFile()))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",", -1);
            if (parts.length >= 5) {
                String id = parts[0];
                String title = unescapeCsv(parts[1]);
                String description = unescapeCsv(parts[2]);
                LocalDateTime startDateTime = LocalDateTime
                    .parse(parts[3], DATE_FORMATTER);
                LocalDateTime endDateTime = LocalDateTime
                    .parse(parts[4], DATE_FORMATTER);
                
                Event event = new Event(title, description, 
                    startDateTime, endDateTime);
                events.add(event);
            }
        }
    }
    
    return events;
}
```

**Update savePlannerData()**:
```java
public void savePlannerData(PlannerModel model) throws IOException {
    createDataDirectory();
    
    saveStudent(model.getCurrentStudent());
    saveCourses(model.getCourses());
    saveTasks(model.getTasks());
    saveEvents(model.getEvents());  // Add this
}
```

**Update loadPlannerData()**:
```java
public void loadPlannerData(PlannerModel model) throws IOException {
    // ... existing load code ...
    
    List<Event> events = loadEvents();
    events.forEach(model::addEvent);  // Add this
}
```

### Files Created/Modified

**New File**:
- `src/main/java/planner/model/Event.java`

**Modified Files**:
- `src/main/java/planner/model/PlannerModel.java`
- `src/main/java/planner/persistence/PlannerRepository.java`

### Event Types Added

New ModelEvent types fired:
- `"EVENT_ADDED"` - New event created
- `"EVENT_REMOVED"` - Event deleted
- `"EVENT_UPDATED"` - Event modified

### Expected Outcome

After this prompt, you should have:
- [ ] Event entity class with UUID identity
- [ ] Event management in PlannerModel
- [ ] Event queries (by date, by range)
- [ ] Event persistence in PlannerRepository
- [ ] Events saved to events.csv
- [ ] Event change notifications
- [ ] Events cleared with clearAll()

### Verification Halt

**Test Event Entity**:
```java
// Create event
Event event = new Event("Meeting", "Team sync",
    LocalDateTime.of(2026, 4, 21, 14, 0),
    LocalDateTime.of(2026, 4, 21, 15, 0));

// Test occursOn
assertTrue(event.occursOn(LocalDate.of(2026, 4, 21)));
assertFalse(event.occursOn(LocalDate.of(2026, 4, 22)));

// Test overlapsWith
LocalDateTime morning = LocalDateTime.of(2026, 4, 21, 9, 0);
LocalDateTime evening = LocalDateTime.of(2026, 4, 21, 17, 0);
assertTrue(event.overlapsWith(morning, evening));

// Test model integration
PlannerModel model = new PlannerModel();
model.addEvent(event);
assertEquals(1, model.getEvents().size());
assertEquals(1, model.getEventsForDate(
    LocalDate.of(2026, 4, 21)).size());
```

**Test Persistence**:
```java
// Save and load
PlannerRepository repo = new PlannerRepository();
PlannerModel model = new PlannerModel();
model.addEvent(new Event("Test", "Desc", 
    LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

repo.savePlannerData(model);

PlannerModel newModel = new PlannerModel();
repo.loadPlannerData(newModel);
assertEquals(1, newModel.getEvents().size());
```

### Design Decisions

**Why separate Event from Task?**
- SRS explicitly specifies them as different entities
- Different use cases (deadline vs scheduled time)
- Different attributes (due date vs start/end)
- Different UI representations

**Why UUID as String?**
- Consistent with other entities
- Human-readable for debugging
- CSV serialization friendly

**Why LocalDateTime (not Date)?**
- Modern Java 8+ time API
- Immutable and thread-safe
- Better API for date/time arithmetic

**Why no priority for Events?**
- SRS doesn't specify event priorities
- Events are scheduled by time, not urgency
- Can be added later if needed

**Why no completion status?**
- Events happen at specific times
- Not to-do items to be completed
- Past events remain on calendar

### Future Enhancements

- **Event Categories**: Work, Personal, School
- **Event Colors**: Different colors per category
- **Recurring Events**: Weekly meetings, daily reminders
- **Event Notifications**: Alerts before event starts
- **All-Day Events**: Full-day events (no time)
- **Event Location**: Where event takes place
- **Event Attendees**: Who else is attending

---

**Next**: PROMPT-K2: Calendar Day/Week/Month Views  
**Prev**: PROMPT-K: Calendar Foundation

**Implementation Date**: 2026-04-21  
**Complexity**: Medium (entity + integration)  
**Lines Added**: ~150 (Event class) + ~100 (integration)

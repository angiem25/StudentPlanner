# PROMPT K2: CALENDAR DAY/WEEK/MONTH VIEWS

## **MANDATORY**: Implement Day, Week, and Month views for the calendar with 24-hour timeline.

## Module of Thought

**Phase**: UI Enhancement (Calendar Extension)  
**Purpose**: Add multiple calendar views similar to Outlook/macOS calendar  
**Dependencies**: PROMPT-K (Calendar Foundation)

### Feature Requirements

**View Types**:
1. **Day View**: Single day with 24-hour timeline
2. **Week View**: 7 days (Sun-Sat) with 24-hour timeline columns
3. **Month View**: Traditional calendar grid (existing)

**Common Features Across All Views**:
- View toggle buttons (Day/Week/Month)
- Previous/Next/Today navigation
- 24-hour timeline (12 AM - 12 AM)
- Event display in time slots
- Auto-scroll to 8 AM on load
- Dynamic title based on current view

### Implementation Architecture

```
CalendarView
├── Header Panel
│   ├── View Toggle (Day/Week/Month)
│   ├── Title Label (dynamic)
│   └── Navigation (Previous/Today/Next)
│
└── Calendar Content (switches based on view)
    ├── Day View: 24 hour rows with events
    ├── Week View: 7 day columns + time column
    └── Month View: 6x7 day grid with tasks
```

### Configuration Constants

```java
private static final int START_HOUR = 0;     // 12 AM
private static final int END_HOUR = 24;      // 12 AM next day
private static final int HOUR_HEIGHT = 50;   // Pixels per hour
```

### View State Management

```java
public enum ViewType { DAY, WEEK, MONTH }

private ViewType currentView = ViewType.MONTH;
private LocalDate currentDate;           // Selected date
private YearMonth currentYearMonth;      // Selected month
```

### Day View Implementation

```java
private JPanel createDayView() {
    // Header with date
    // Timeline with 24 hour rows
    // Each row: time label + event area
    // Scroll pane with auto-scroll to 8 AM
    // Events displayed with time prefix
}
```

**Layout**:
```
┌──────────────────────────────┐
│ Monday, Apr 21              │
├──────────────────────────────┤
│ 12 AM │                      │
│  1 AM │ [Event]              │
│  ...  │                      │
│  8 AM │ ▼ (scroll here)      │
│  ...  │                      │
│ 12 PM │ [Lunch Event]        │
│  ...  │                      │
│ 11 PM │                      │
└──────────────────────────────┘
```

### Week View Implementation

```java
private JPanel createWeekView() {
    // Header with week range
    // 8 columns: time + 7 days
    // Each column: header + 24 hour cells
    // Today highlighting
    // Scroll pane with auto-scroll to 8 AM
}
```

**Layout**:
```
┌─────────────────────────────────────────────────────┐
│ Week of Apr 19 - Apr 25                              │
├──────┬────────┬────────┬────────┬────────────────────┤
│      │  Sun   │  Mon   │  Tue   │  ...              │
│ 12AM │        │        │[Event] │                   │
│  1AM │        │[Eve]   │        │                   │
│ ...  │        │        │        │                   │
│  8AM │        │        │   ▼    │ (scroll here)     │
│ ...  │        │        │        │                   │
│ 12PM │        │[Lunch] │        │                   │
└──────┴────────┴────────┴────────┴────────────────────┘
```

### Month View Implementation

```java
private JPanel createMonthView() {
    // Existing calendar grid
    // Populate with tasks on due dates
    // Today highlighting
}
```

### Navigation Logic

```java
private void navigatePrevious() {
    switch (currentView) {
        case DAY:
            currentDate = currentDate.minusDays(1);
            break;
        case WEEK:
            currentDate = currentDate.minusWeeks(1);
            break;
        case MONTH:
            currentYearMonth = currentYearMonth.minusMonths(1);
            currentDate = currentYearMonth.atDay(1);
            break;
    }
    refreshCalendar();
}
```

### View Switching

```java
public void setViewType(ViewType viewType) {
    this.currentView = viewType;
    refreshCalendar();
}

private void refreshCalendar() {
    // Clear calendar panel
    // Create appropriate view
    // Update title label
    // Revalidate and repaint
}
```

### Event Display

```java
private JLabel createEventLabel(Event event, boolean showTime) {
    // Format: "9:00 AM Event Title"
    // Truncate if > 20 chars
    // Blue background, white text
    // Rounded corners via padding
}

private String formatHour(int hour) {
    // 0 → "12 AM"
    // 12 → "12 PM"
    // 13 → "1 PM"
    // etc.
}
```

### UI Components

**Header Panel Structure**:
```java
private JPanel createHeaderPanel() {
    // North: View toggle buttons (Day/Week/Month)
    // Center: Dynamic title label
    // South: Navigation buttons (Previous/Today/Next)
}
```

**View Toggle Buttons**:
```java
JToggleButton dayButton = new JToggleButton("Day");
dayButton.addActionListener(e -> setViewType(ViewType.DAY));

JToggleButton weekButton = new JToggleButton("Week");
weekButton.addActionListener(e -> setViewType(ViewType.WEEK));

JToggleButton monthButton = new JToggleButton("Month");
monthButton.setSelected(true);
monthButton.addActionListener(e -> setViewType(ViewType.MONTH));
```

### File Changes

**Modified Files**:
- `src/main/java/planner/ui/calendar/CalendarView.java`

**Key Changes**:
1. Add ViewType enum
2. Add view state fields (currentView, currentDate)
3. Add configuration constants
4. Add view creation methods (createDayView, createWeekView)
5. Add navigation methods (navigatePrevious, navigateNext)
6. Modify header panel to include view toggle
7. Modify refreshCalendar to switch views
8. Keep month view with task display
9. Add event display to Day/Week views

### Expected Outcome

After this prompt, the calendar should have:
- [ ] Three view types: Day, Week, Month
- [ ] Toggle buttons to switch views
- [ ] Context-aware navigation (day/week/month)
- [ ] 24-hour timeline in Day and Week views
- [ ] Events displayed in hourly slots
- [ ] Auto-scroll to 8 AM
- [ ] Dynamic title showing current period
- [ ] Today highlighting in Week view
- [ ] Task display preserved in Month view

### Verification Halt

**Test Each View**:

**Day View**:
- [ ] Shows current date in header
- [ ] 24 hour rows visible
- [ ] Events appear in correct hour slots
- [ ] Previous/Next moves by day
- [ ] Today button returns to today
- [ ] Auto-scrolls to 8 AM

**Week View**:
- [ ] Shows week range in header
- [ ] 7 day columns + time column
- [ ] Events appear in correct day/hour
- [ ] Today column highlighted
- [ ] Previous/Next moves by week
- [ ] Auto-scrolls to 8 AM

**Month View**:
- [ ] Shows month/year in header
- [ ] 6x7 day grid
- [ ] Tasks appear on due dates
- [ ] Today highlighted
- [ ] Previous/Next moves by month

### MVC Integration

**Observer Pattern**:
```java
@Override
public void modelChanged(ModelEvent event) {
    if (eventName.startsWith("TASK_") || 
        eventName.startsWith("EVENT_") || 
        eventName.equals("PLANNER_CLEARED")) {
        SwingUtilities.invokeLater(this::refreshCalendar);
    }
}
```

### Design Decisions

**Why 50px per hour?**
- Enough space for multiple events
- Readable event labels
- Total height 1200px (24 hours) fits in scroll pane

**Why auto-scroll to 8 AM?**
- Typical start of day
- Users usually view 8 AM - 6 PM
- Avoids showing midnight hours initially

**Why Sunday start for Week view?**
- Standard US calendar format
- Can be changed to Monday if needed

**Why separate Event and Task display?**
- SRS specifies separate entities
- Tasks = due dates (shown in Month)
- Events = time slots (shown in Day/Week)

### Edge Cases Handled

1. **Empty days**: Show empty hour rows
2. **No events**: Display blank timeline
3. **Event spanning hours**: Show in all overlapping hour cells
4. **Many events**: Vertical stacking with spacing
5. **View switching mid-navigation**: Maintains current date context
6. **Today button from any view**: Returns to current period

### Future Enhancements

- **Event Creation**: Double-click to add event
- **Drag and Drop**: Move events between time slots
- **Event Editing**: Click to edit details
- **All-Day Events**: Top row in Day/Week views
- **Recurring Events**: Show multiple instances
- **Calendar Colors**: Different colors per event type

---

**Next**: PROMPT-L: Calendar Event Dialog  
**Prev**: PROMPT-K: Calendar Foundation

**Implementation Date**: 2026-04-21  
**Complexity**: High (multiple coordinated views)  
**Lines Added**: ~300

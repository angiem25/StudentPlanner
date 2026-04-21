# PROMPT K: CALENDAR GRID VIEW

## **MANDATORY**: Implement a calendar grid view that displays tasks organized by due date.

## Module of Thought

**Phase**: UI Enhancement  
**Purpose**: Add calendar visualization for task management  
**Dependencies**: PROMPT-F (User Interface)

### Feature Requirements

**Calendar Display**:
- Show current month in grid format (6 rows x 7 columns)
- Display day names (Sun-Sat) as headers
- Show date numbers in each cell
- Highlight current day
- Navigate between months (Previous/Next buttons)
- "Today" button to return to current month

**Task Integration**:
- Display tasks on their due dates
- Color-code by priority (High=Red, Medium=Yellow, Low=Green)
- Show completed tasks with strikethrough
- Truncate long titles with ellipsis
- Scrollable task list within each day cell

**Live Updates**:
- Auto-refresh when tasks are added/updated/removed
- Respond to model change events
- Thread-safe UI updates (SwingUtilities.invokeLater)

### File Location

```
src/main/java/planner/ui/calendar/
└── CalendarView.java
```

### Implementation Structure

```java
public class CalendarView extends JPanel implements View, ModelListener {
    // Configuration
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final int ROWS = 6; // Weeks
    private static final int COLS = 7;  // Days
    private static final String[] DAY_NAMES = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    
    // State
    private YearMonth currentYearMonth;
    private Model model;
    private Controller controller;
    
    // UI Components
    private JLabel monthLabel;
    private JButton prevMonthButton, nextMonthButton, todayButton;
    private JPanel[][] dayCells;
    private JLabel[][] dateLabels;
    private JPanel[][] taskPanels;
    
    // MVC Methods
    public CalendarView(PlannerModel model, PlannerController controller)
    public Model getModel()
    public Controller getController()
    public void setModel(Model model)
    public void setController(Controller controller)
    public void modelChanged(ModelEvent event)
    
    // Calendar Methods
    private void initializeUI()
    private JPanel createHeaderPanel()
    private JPanel createCalendarGrid()
    private JPanel createDayCell(int row, int col)
    private void refreshCalendar()
    private void addTasksForDate(LocalDate date, JPanel taskPanel)
    private JLabel createTaskLabel(Task task)
    private void navigateMonth(int months)
    private void goToToday()
}
```

### Key Implementation Details

**MVC Pattern**:
- Implements both `View` and `ModelListener` interfaces
- Extends `JPanel` for Swing component functionality
- Registers as listener to model in constructor
- Updates UI via `modelChanged()` callback

**Calendar Logic**:
```java
// Calculate first day and number of days
LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
int daysInMonth = currentYearMonth.lengthOfMonth();
int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

// Fill cells starting from first day
int day = 1;
for (int row = 0; row < ROWS; row++) {
    for (int col = 0; col < COLS; col++) {
        int cellIndex = row * COLS + col;
        if (cellIndex >= firstDayOfWeek && day <= daysInMonth) {
            // Set date label and add tasks
            day++;
        }
    }
}
```

**Task Display**:
```java
private JLabel createTaskLabel(Task task) {
    JLabel label = new JLabel(truncate(task.getTitle(), 15));
    
    // Color by priority
    switch (task.getPriority()) {
        case HIGH:
            label.setBackground(new Color(255, 200, 200));
            label.setForeground(Color.RED);
            break;
        // ... etc
    }
    
    // Strikethrough if completed
    if (task.isCompleted()) {
        label.setText("<html><strike>" + text + "</strike></html>");
    }
    
    return label;
}
```

**Event Handling**:
```java
@Override
public void modelChanged(ModelEvent event) {
    // Update calendar when task-related events occur
    if (event == null || event.getEventName().startsWith("TASK_")) {
        SwingUtilities.invokeLater(this::refreshCalendar);
    }
}
```

### Integration with PlannerView

Add calendar as a new tab in the existing tabbed interface:

```java
// In PlannerView.java
CalendarView calendarView = new CalendarView(model, controller);
tabbedPane.addTab("Calendar", calendarView);
```

### Design Decisions

**Why JPanel[][] for cells?**:
- Direct access to any day cell for updates
- Easy to refresh individual cells if needed
- Simple grid layout management

**Why YearMonth instead of Date/Calendar?**:
- Modern Java 8+ time API
- Immutable and thread-safe
- Better API for month arithmetic

**Why color-code by priority?**:
- Visual indication of task urgency
- Quick identification of high-priority items
- Consistent with planner priority system

**Why truncate task titles?**:
- Calendar cells have limited space
- Full title visible in task list tab
- Hover tooltip could show full title (future enhancement)

### Expected Outcome

After this prompt, you should have:
- CalendarView class in planner.ui.calendar package
- 6x7 grid showing monthly calendar
- Tasks displayed on due dates
- Month navigation (previous/next/today)
- Current day highlighting
- Priority-based color coding
- Live updates via observer pattern

### Verification Halt

**Check before completing**:

- [ ] Calendar displays 6 weeks x 7 days
- [ ] Current month displayed in header
- [ ] Month navigation buttons work
- [ ] "Today" button returns to current month
- [ ] Current day is highlighted
- [ ] Tasks appear on their due dates
- [ ] Tasks color-coded by priority
- [ ] Completed tasks show strikethrough
- [ ] Calendar updates when tasks are added/updated/removed
- [ ] Thread-safe UI updates (SwingUtilities.invokeLater)

**Test Scenario**:
```java
// Create calendar
CalendarView calendar = new CalendarView(model, controller);

// Add task for today
Task task = new Task("Test Task", "Description", LocalDateTime.now(), Priority.HIGH);
model.addTask(task);

// Calendar should automatically show the task
// (via modelChanged -> refreshCalendar)

// Navigate to next month
calendar.navigateMonth(1);
// Should show next month, task not visible (different month)

// Navigate back
calendar.navigateMonth(-1);
// Should show current month with task visible
```

### Future Enhancements

- **Week View**: Alternative view showing one week in detail
- **Day View**: Single day with hourly schedule
- **Drag & Drop**: Move tasks between dates
- **Click to Add**: Click empty date to create task
- **Tooltips**: Show full task details on hover
- **Recurring Tasks**: Show recurring instances
- **Calendar Export**: Export to iCal/Google Calendar format
- **Date Picker**: Use calendar to pick dates in forms

---

**Next**: PROMPT-L: Grade Tracking (potential)  
**Prev**: PROMPT-F: User Interface

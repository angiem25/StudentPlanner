package planner.ui.calendar;

import mvc.AbstractModel;
import mvc.Controller;
import mvc.Model;
import mvc.ModelEvent;
import mvc.ModelListener;
import mvc.View;
import planner.model.Event;
import planner.model.PlannerModel;
import planner.model.Task;
import planner.ui.AppTheme;
import planner.ui.PlannerController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Calendar view with Day, Week, and Month views.
 * Similar to Outlook/macOS calendar with 24-hour timeline.
 */
public class CalendarView extends JPanel implements View, ModelListener {
    
    // View types
    public enum ViewType { DAY, WEEK, MONTH }
    
    // Formatters
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d");
    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("MMM d");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    // Hour configuration
    private static final int START_HOUR = 0;  // 12 AM
    private static final int END_HOUR = 24;   // 12 AM next day
    private static final int HOUR_HEIGHT = 50; // Pixels per hour
    
    // Model and Controller
    private Model model;
    private Controller controller;
    
    // UI Components
    private JPanel calendarPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JPanel viewTogglePanel;
    
    // View state
    private ViewType currentView = ViewType.MONTH;
    private LocalDate currentDate; // Currently selected date
    private YearMonth currentYearMonth;
    
    // Month view components
    private JPanel[][] dayCells;
    private JLabel[][] dateLabels;
    private JPanel[][] eventPanels;
    
    // Day/Week view components
    private JScrollPane timelineScrollPane;
    
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final String[] DAY_NAMES = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    
    /**
     * Creates a new CalendarView with the specified model and controller.
     * @param model The planner model
     * @param controller The planner controller
     */
    public CalendarView(PlannerModel model, PlannerController controller) {
        this.model = model;
        this.controller = controller;
        
        // Register as model listener
        if (model instanceof AbstractModel) {
            ((AbstractModel) model).addModelListener(this);
        }
        
        this.currentYearMonth = YearMonth.now();
        initializeUI();
        refreshCalendar();
    }
    
    /**
     * Re-applies calendar colors after light/dark theme change.
     */
    public void refreshTheme() {
        refreshCalendar();
    }
    
    @Override
    public Model getModel() {
        return model;
    }
    
    @Override
    public Controller getController() {
        return controller;
    }
    
    /**
     * Sets the model for this view.
     * @param model The new model
     */
    public void setModel(Model model) {
        if (this.model instanceof AbstractModel) {
            ((AbstractModel) this.model).removeModelListener(this);
        }
        this.model = model;
        if (model instanceof AbstractModel) {
            ((AbstractModel) model).addModelListener(this);
        }
        modelChanged(null);
    }
    
    /**
     * Sets the controller for this view.
     * @param controller The new controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    /**
     * Initializes the calendar user interface.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Initialize current date to today
        this.currentDate = LocalDate.now();
        this.currentYearMonth = YearMonth.now();
        
        // Header panel with navigation
        headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Calendar panel container
        calendarPanel = new JPanel(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER);
        
        // Create initial view
        refreshCalendar();
    }
    
    /**
     * Creates the header panel with navigation and view toggle.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Title label (center)
        titleLabel = new JLabel("", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Top row: View toggle buttons
        viewTogglePanel = new JPanel(new FlowLayout());
        
        ButtonGroup viewGroup = new ButtonGroup();
        
        JToggleButton dayButton = new JToggleButton("Day");
        dayButton.addActionListener(e -> setViewType(ViewType.DAY));
        viewGroup.add(dayButton);
        viewTogglePanel.add(dayButton);
        
        JToggleButton weekButton = new JToggleButton("Week");
        weekButton.addActionListener(e -> setViewType(ViewType.WEEK));
        viewGroup.add(weekButton);
        viewTogglePanel.add(weekButton);
        
        JToggleButton monthButton = new JToggleButton("Month");
        monthButton.setSelected(true); // Default
        monthButton.addActionListener(e -> setViewType(ViewType.MONTH));
        viewGroup.add(monthButton);
        viewTogglePanel.add(monthButton);
        
        headerPanel.add(viewTogglePanel, BorderLayout.NORTH);
        
        // Bottom row: Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout());
        
        JButton prevButton = new JButton("◀ Previous");
        prevButton.addActionListener(e -> navigatePrevious());
        navPanel.add(prevButton);
        
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> goToToday());
        navPanel.add(todayButton);
        
        JButton nextButton = new JButton("Next ▶");
        nextButton.addActionListener(e -> navigateNext());
        navPanel.add(nextButton);
        
        headerPanel.add(navPanel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    /**
     * Creates the calendar grid with day cells.
     */
    private JPanel createCalendarGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(ROWS + 1, COLS, 2, 2));
        
        // Day name headers
        for (String dayName : DAY_NAMES) {
            JLabel dayHeader = new JLabel(dayName, JLabel.CENTER);
            dayHeader.setFont(new Font("Arial", Font.BOLD, 12));
            dayHeader.setOpaque(true);
            dayHeader.setForeground(AppTheme.calDayHeaderFg());
            dayHeader.setBackground(AppTheme.calDayHeaderBg());
            dayHeader.setBorder(BorderFactory.createLineBorder(AppTheme.calDayHeaderBorder()));
            gridPanel.add(dayHeader);
        }
        
        // Initialize day cells
        dayCells = new JPanel[ROWS][COLS];
        dateLabels = new JLabel[ROWS][COLS];
        eventPanels = new JPanel[ROWS][COLS];
        
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JPanel cell = createDayCell(row, col);
                dayCells[row][col] = cell;
                gridPanel.add(cell);
            }
        }
        
        return gridPanel;
    }
    
    /**
     * Creates a single day cell in the calendar grid.
     */
    private JPanel createDayCell(int row, int col) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBorder(BorderFactory.createLineBorder(AppTheme.calCellBorder()));
        cell.setBackground(AppTheme.calCellBg());
        cell.setPreferredSize(new Dimension(120, 80));
        
        // Date label (top)
        JLabel dateLabel = new JLabel("", JLabel.RIGHT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(AppTheme.calCellFg());
        dateLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        cell.add(dateLabel, BorderLayout.NORTH);
        dateLabels[row][col] = dateLabel;
        
        // Event panel (center)
        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        eventPanel.setBackground(AppTheme.calEventPanelBg());
        eventPanel.setOpaque(true);
        eventPanels[row][col] = eventPanel;
        
        JScrollPane scrollPane = new JScrollPane(eventPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        cell.add(scrollPane, BorderLayout.CENTER);
        
        return cell;
    }
    
    /**
     * Refreshes the calendar display based on current view type.
     */
    private void refreshCalendar() {
        // Clear current view
        calendarPanel.removeAll();
        
        // Create appropriate view
        JPanel viewPanel;
        switch (currentView) {
            case DAY:
                viewPanel = createDayView();
                titleLabel.setText(currentDate.format(DATE_FORMATTER));
                break;
            case WEEK:
                viewPanel = createWeekView();
                LocalDate weekStart = currentDate.with(java.time.DayOfWeek.SUNDAY);
                LocalDate weekEnd = weekStart.plusDays(6);
                titleLabel.setText(weekStart.format(WEEK_FORMATTER) + " - " + weekEnd.format(WEEK_FORMATTER));
                break;
            case MONTH:
            default:
                viewPanel = createMonthView();
                titleLabel.setText(currentYearMonth.format(MONTH_FORMATTER));
                break;
        }
        
        calendarPanel.add(viewPanel, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    
    /**
     * Creates the month view (original calendar grid).
     */
    private JPanel createMonthView() {
        JPanel gridPanel = createCalendarGrid();
        
        // Get first day of month and number of days
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        
        LocalDate today = LocalDate.now();
        
        // Fill in days
        int day = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int cellIndex = row * COLS + col;
                
                if (cellIndex >= firstDayOfWeek && day <= daysInMonth) {
                    LocalDate cellDate = currentYearMonth.atDay(day);
                    
                    // Set date label
                    dateLabels[row][col].setText(String.valueOf(day));
                    dateLabels[row][col].setForeground(AppTheme.calCellFg());
                    
                    // Highlight today
                    if (cellDate.equals(today)) {
                        dayCells[row][col].setBackground(AppTheme.calTodayBg());
                        dayCells[row][col].setBorder(BorderFactory.createLineBorder(AppTheme.calTodayBorder(), 2));
                    }
                    
                    // Add tasks for this date
                    addTasksForDate(cellDate, eventPanels[row][col]);
                    
                    day++;
                }
            }
        }
        
        return gridPanel;
    }
    
    /**
     * Navigates to previous period based on current view.
     */
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
    
    /**
     * Navigates to next period based on current view.
     */
    private void navigateNext() {
        switch (currentView) {
            case DAY:
                currentDate = currentDate.plusDays(1);
                break;
            case WEEK:
                currentDate = currentDate.plusWeeks(1);
                break;
            case MONTH:
                currentYearMonth = currentYearMonth.plusMonths(1);
                currentDate = currentYearMonth.atDay(1);
                break;
        }
        refreshCalendar();
    }
    
    /**
     * Goes to today based on current view.
     */
    private void goToToday() {
        currentDate = LocalDate.now();
        currentYearMonth = YearMonth.now();
        refreshCalendar();
    }
    
    /**
     * Adds task indicators to a day cell for the specified date.
     */
    private void addTasksForDate(LocalDate date, JPanel taskPanel) {
        PlannerModel model = (PlannerModel) getModel();
        List<Task> tasks = model.getTasks();
        
        for (Task task : tasks) {
            LocalDateTime dueDateTime = task.getDueDate();
            LocalDate dueDate = dueDateTime.toLocalDate();
            
            if (dueDate.equals(date)) {
                JLabel taskLabel = createTaskLabel(task);
                taskPanel.add(taskLabel);
                taskPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            }
        }
        
        taskPanel.revalidate();
        taskPanel.repaint();
    }
    
    /**
     * Creates a label for a task with appropriate styling.
     */
    private JLabel createTaskLabel(Task task) {
        String text = task.getTitle();
        if (text.length() > 15) {
            text = text.substring(0, 12) + "...";
        }
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 9));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
        
        // Color based on priority
        switch (task.getPriority()) {
            case HIGH:
                label.setBackground(AppTheme.taskHighBg());
                label.setForeground(AppTheme.taskHighFg());
                break;
            case MEDIUM:
                label.setBackground(AppTheme.taskMediumBg());
                label.setForeground(AppTheme.taskMediumFg());
                break;
            case LOW:
                label.setBackground(AppTheme.taskLowBg());
                label.setForeground(AppTheme.taskLowFg());
                break;
        }
        
        // Strikethrough if completed
        if (task.isCompleted()) {
            label.setText("<html><strike>" + text + "</strike></html>");
            label.setBackground(AppTheme.taskCompletedBg());
            label.setForeground(AppTheme.taskCompletedFg());
        }
        
        return label;
    }
    
    // ============================================
    // VIEW SWITCHING
    // ============================================
    
    /**
     * Switches to a different calendar view (Day, Week, or Month).
     * @param viewType The view type to switch to
     */
    public void setViewType(ViewType viewType) {
        this.currentView = viewType;
        refreshCalendar();
    }
    
    /**
     * Gets the current view type.
     * @return The current view type
     */
    public ViewType getViewType() {
        return currentView;
    }
    
    // ============================================
    // DAY VIEW IMPLEMENTATION
    // ============================================
    
    /**
     * Creates the day view with 24-hour timeline.
     */
    private JPanel createDayView() {
        JPanel dayViewPanel = new JPanel(new BorderLayout());
        
        // Header showing current date
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel dateLabel = new JLabel(currentDate.format(DATE_FORMATTER));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(dateLabel);
        dayViewPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Timeline panel with hours
        JPanel timelineContent = new JPanel(new GridBagLayout());
        timelineContent.setBackground(AppTheme.timelineBg());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Add each hour
        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            gbc.gridy = hour;
            
            // Hour row panel
            JPanel hourRow = new JPanel(new BorderLayout());
            hourRow.setPreferredSize(new Dimension(0, HOUR_HEIGHT));
            hourRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.hourDivider()));
            hourRow.setBackground(AppTheme.timelineBg());
            
            // Time label
            String timeText = formatHour(hour);
            JLabel timeLabel = new JLabel(timeText, JLabel.RIGHT);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            timeLabel.setPreferredSize(new Dimension(60, HOUR_HEIGHT));
            timeLabel.setBorder(new EmptyBorder(0, 5, 0, 10));
            hourRow.add(timeLabel, BorderLayout.WEST);
            
            // Event area for this hour
            JPanel eventArea = new JPanel();
            eventArea.setLayout(null); // Absolute positioning for overlapping events
            eventArea.setBackground(AppTheme.timelineBg());
            eventArea.setOpaque(true);
            hourRow.add(eventArea, BorderLayout.CENTER);
            
            // Add events for this hour
            addEventsToHour(eventArea, currentDate, hour);
            
            timelineContent.add(hourRow, gbc);
        }
        
        // Scroll pane for timeline
        timelineScrollPane = new JScrollPane(timelineContent);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Scroll to 8 AM
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = timelineScrollPane.getVerticalScrollBar();
            verticalBar.setValue(8 * HOUR_HEIGHT);
        });
        
        dayViewPanel.add(timelineScrollPane, BorderLayout.CENTER);
        
        return dayViewPanel;
    }
    
    /**
     * Adds events to an hour row in the day view.
     */
    private void addEventsToHour(JPanel eventArea, LocalDate date, int hour) {
        PlannerModel model = (PlannerModel) getModel();
        List<Event> events = model.getEvents();
        
        LocalDateTime hourStart = date.atTime(hour, 0);
        LocalDateTime hourEnd = date.atTime(hour, 59);
        
        int yPosition = 5;
        int eventHeight = HOUR_HEIGHT - 10;
        
        for (Event event : events) {
            LocalDateTime eventStart = event.getStartDateTime();
            LocalDateTime eventEnd = event.getEndDateTime();
            
            // Check if event overlaps with this hour
            if (eventStart.isBefore(hourEnd) && eventEnd.isAfter(hourStart)) {
                JLabel eventLabel = createEventLabel(event, true);
                eventLabel.setBounds(5, yPosition, 200, eventHeight);
                eventArea.add(eventLabel);
                yPosition += eventHeight + 2;
            }
        }
    }
    
    // ============================================
    // WEEK VIEW IMPLEMENTATION
    // ============================================
    
    /**
     * Creates the week view with 7 columns and 24-hour timeline.
     */
    private JPanel createWeekView() {
        JPanel weekViewPanel = new JPanel(new BorderLayout());
        
        // Header showing week range
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        LocalDate weekStart = currentDate.with(java.time.DayOfWeek.SUNDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        String weekLabel = weekStart.format(WEEK_FORMATTER) + " - " + weekEnd.format(WEEK_FORMATTER);
        
        JLabel weekLabelComp = new JLabel("Week of " + weekLabel);
        weekLabelComp.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(weekLabelComp);
        weekViewPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content with day columns (7 days, no separate time column)
        JPanel contentPanel = new JPanel(new GridLayout(1, 7));
        
        // Day columns with embedded time labels
        for (int i = 0; i < 7; i++) {
            LocalDate dayDate = weekStart.plusDays(i);
            JPanel dayColumn = createWeekDayColumn(dayDate);
            contentPanel.add(dayColumn);
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Scroll to 8 AM
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(8 * HOUR_HEIGHT);
        });
        
        weekViewPanel.add(scrollPane, BorderLayout.CENTER);
        
        return weekViewPanel;
    }
    
    /**
     * Creates a day column for week view with embedded time labels.
     */
    private JPanel createWeekDayColumn(LocalDate date) {
        JPanel dayColumn = new JPanel(new BorderLayout());
        dayColumn.setBackground(AppTheme.timelineBg());
        
        // Column header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AppTheme.weekColumnHeaderBorder()));
        header.setBackground(AppTheme.weekColumnHeaderBg());
        header.setPreferredSize(new Dimension(0, 30));
        
        String dayName = date.getDayOfWeek().toString().substring(0, 3);
        int dayNum = date.getDayOfMonth();
        JLabel headerLabel = new JLabel(dayName + " " + dayNum, JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        // Highlight today
        if (date.equals(LocalDate.now())) {
            header.setBackground(AppTheme.weekTodayHeaderBg());
            headerLabel.setForeground(AppTheme.weekTodayHeaderFg());
        } else {
            headerLabel.setForeground(AppTheme.weekColumnHeaderFg());
        }
        
        header.add(headerLabel, BorderLayout.CENTER);
        dayColumn.add(header, BorderLayout.NORTH);
        
        // Hour cells panel with grid
        JPanel hoursPanel = new JPanel(new GridLayout(END_HOUR - START_HOUR, 1));
        hoursPanel.setBackground(AppTheme.timelineBg());
        
        // Hour cells with time labels
        for (int hour = START_HOUR; hour < END_HOUR; hour++) {
            JPanel hourCell = createWeekHourCellWithTime(date, hour);
            hoursPanel.add(hourCell);
        }
        
        dayColumn.add(hoursPanel, BorderLayout.CENTER);
        
        return dayColumn;
    }
    
    /**
     * Creates a single hour cell for week view with time label.
     */
    private JPanel createWeekHourCellWithTime(LocalDate date, int hour) {
        JPanel hourCell = new JPanel(new BorderLayout());
        hourCell.setPreferredSize(new Dimension(0, HOUR_HEIGHT));
        hourCell.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, AppTheme.hourDivider()));
        hourCell.setBackground(AppTheme.timelineBg());
        
        // Time label on the left side of each hour cell
        JLabel timeLabel = new JLabel(formatHour(hour));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        timeLabel.setForeground(AppTheme.timeMuted());
        timeLabel.setPreferredSize(new Dimension(45, HOUR_HEIGHT));
        timeLabel.setBorder(new EmptyBorder(2, 3, 0, 2));
        hourCell.add(timeLabel, BorderLayout.WEST);
        
        // Event area
        JPanel eventArea = new JPanel();
        eventArea.setLayout(new BoxLayout(eventArea, BoxLayout.Y_AXIS));
        eventArea.setBackground(AppTheme.timelineBg());
        eventArea.setOpaque(true);
        
        PlannerModel model = (PlannerModel) getModel();
        List<Event> events = model.getEvents();
        
        LocalDateTime hourStart = date.atTime(hour, 0);
        LocalDateTime hourEnd = date.atTime(hour, 59);
        
        // Add events for this hour
        for (Event event : events) {
            LocalDateTime eventStart = event.getStartDateTime();
            LocalDateTime eventEnd = event.getEndDateTime();
            
            if (eventStart.isBefore(hourEnd) && eventEnd.isAfter(hourStart)) {
                JLabel eventLabel = createEventLabel(event, false);
                eventArea.add(eventLabel);
                eventArea.add(Box.createRigidArea(new Dimension(0, 2)));
            }
        }
        
        hourCell.add(eventArea, BorderLayout.CENTER);
        
        return hourCell;
    }
    
    // ============================================
    // EVENT LABEL CREATION
    // ============================================
    
    /**
     * Creates a label for an event.
     */
    private JLabel createEventLabel(Event event, boolean showTime) {
        String text = event.getTitle();
        if (showTime) {
            text = event.getStartDateTime().format(TIME_FORMATTER) + " " + text;
        }
        
        if (text.length() > 20) {
            text = text.substring(0, 17) + "...";
        }
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        label.setBackground(AppTheme.eventBarBg());
        label.setForeground(AppTheme.eventBarFg());
        
        return label;
    }
    
    /**
     * Formats an hour for display.
     */
    private String formatHour(int hour) {
        if (hour == 0) return "12 AM";
        if (hour == 12) return "12 PM";
        if (hour < 12) return hour + " AM";
        return (hour - 12) + " PM";
    }
    
    // ============================================
    // MVC INTERFACE METHODS
    // ============================================
    
    @Override
    public void modelChanged(ModelEvent event) {
        // Update calendar when event-related or task-related events occur
        if (event == null) {
            SwingUtilities.invokeLater(this::refreshCalendar);
            return;
        }
        
        String eventName = event.getEventName();
        if (eventName.startsWith("TASK_") || 
            eventName.startsWith("EVENT_") || 
            eventName.equals("PLANNER_CLEARED")) {
            SwingUtilities.invokeLater(this::refreshCalendar);
        }
    }
}

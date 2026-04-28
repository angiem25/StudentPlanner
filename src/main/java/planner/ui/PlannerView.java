package planner.ui;

import mvc.AbstractView;
import mvc.ModelEvent;
import planner.model.*;
import planner.ui.calendar.CalendarView;
import planner.ui.task.TimeWheelDialog;
import planner.ui.timer.TimerPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main view for the student planner application.
 * Provides a tabbed interface for managing students, courses, and tasks.
 */
public class PlannerView extends AbstractView {
    private static final Logger LOGGER = Logger.getLogger(PlannerView.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_BUTTON_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a").withLocale(Locale.US);
    
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private CalendarView calendarView;
    private TimerPanel timerPanel;
    
    // Student panel components
    private JPanel studentPanel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField studentIdField;
    private JComboBox<Student.AcademicYear> yearComboBox;
    private JTextField majorField;
    private JButton saveStudentButton;
    private JLabel studentInfoLabel;
    
    // Course panel components
    private JPanel coursePanel;
    private JList<Course> courseList;
    private DefaultListModel<Course> courseListModel;
    private JTextField courseNameField;
    private JTextField courseCodeField;
    private JTextField instructorField;
    private JTextField creditsField;
    private JButton addCourseButton;
    private JButton updateCourseButton;
    private JButton removeCourseButton;
    
    // Task panel components
    private JPanel taskPanel;
    private JList<Task> taskList;
    private DefaultListModel<Task> taskListModel;
    private JTextField taskTitleField;
    private JTextArea taskDescriptionArea;
    private JSpinner taskDateSpinner;
    private JButton taskTimeButton;
    /** Due time for the task form; paired with {@link #taskDateSpinner}. */
    private LocalTime taskDueTime;
    private JComboBox<Task.Priority> taskPriorityCombo;
    private JComboBox<String> taskCourseCombo;
    private JButton addTaskButton;
    private JButton updateTaskButton;
    private JButton removeTaskButton;
    private JButton completeTaskButton;
    /** Selected accent for add/update task form; persisted on Add/Update. */
    private String taskFormAccentHex;
    private final List<JPanel> taskColorSwatchPanels = new ArrayList<>();
    private JToggleButton taskColorPreviewToggle;
    private JButton resetTaskColorButton;
    
    // Weekly priorities tab components
    private JPanel weeklyPrioritiesPanel;
    private JLabel weeklyPrioritiesRangeLabel;
    private JList<Task> weeklyPrioritiesList;
    private DefaultListModel<Task> weeklyPrioritiesListModel;
    
    // Today tab components
    private JPanel todayPanel;
    private JLabel todayDateLabel;
    private JList<Task> todayTaskList;
    private DefaultListModel<Task> todayTaskListModel;
    private JLabel todayTaskCountLabel;
    
    private Timer dueReminderTimer;
    private final Set<String> shownDailyReminderKeys = new HashSet<>();
    
    /**
     * Creates a new PlannerView with the specified model and controller.
     * @param model The planner model
     * @param controller The planner controller
     */
    public PlannerView(PlannerModel model, PlannerController controller) {
        super(model, controller);
        initializeUI();
    }
    
    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        frame = new JFrame("Student Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        createStudentPanel();
        createCoursePanel();
        createTaskPanel();
        createWeeklyPrioritiesPanel();
        createTodayPanel();
        
        tabbedPane.addTab("Student Profile", studentPanel);
        tabbedPane.addTab("Courses", coursePanel);
        tabbedPane.addTab("Tasks", taskPanel);
        tabbedPane.addTab("Today", todayPanel);
        tabbedPane.addTab("Weekly Priorities", weeklyPrioritiesPanel);
        
        // Add Calendar tab
        calendarView = new CalendarView((PlannerModel) getModel(),
            (PlannerController) getController());
        tabbedPane.addTab("Calendar", calendarView);
        
        // Add Timer tab
        timerPanel = new TimerPanel();
        tabbedPane.addTab("Timer", timerPanel);
        
        frame.add(tabbedPane);
        attachMenuBar();
        frame.setVisible(true);
        initializeDueReminders();
    }
    
    /**
     * View menu: dark mode toggle.
     */
    private void attachMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkItem = new JCheckBoxMenuItem("Dark mode");
        darkItem.setSelected(AppTheme.isDark());
        darkItem.addActionListener(e -> {
            AppTheme.setDark(darkItem.isSelected());
            try {
                AppTheme.installLookAndFeel();
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Could not switch theme", ex);
            }
            AppTheme.persistTheme();
            AppTheme.applyFrame(frame);
            if (calendarView != null) {
                calendarView.refreshTheme();
            }
            if (timerPanel != null) {
                timerPanel.refreshTheme();
            }
            if (taskList != null) {
                taskList.repaint();
            }
        });
        viewMenu.add(darkItem);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);
    }
    
    /**
     * Creates the student profile panel.
     */
    private void createStudentPanel() {
        studentPanel = new JPanel(new BorderLayout());
        studentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(20);
        formPanel.add(studentIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        yearComboBox = new JComboBox<>(Student.AcademicYear.values());
        yearComboBox.setSelectedItem(Student.AcademicYear.FRESHMAN);
        formPanel.add(yearComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Major:"), gbc);
        gbc.gridx = 1;
        majorField = new JTextField(20);
        formPanel.add(majorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        saveStudentButton = new JButton("Save Student Profile");
        saveStudentButton.addActionListener(e -> onSaveStudent());
        formPanel.add(saveStudentButton, gbc);
        
        studentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Info panel
        studentInfoLabel = new JLabel("No student profile set");
        studentInfoLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        studentPanel.add(studentInfoLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the course management panel.
     */
    private void createCoursePanel() {
        coursePanel = new JPanel(new BorderLayout());
        coursePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Course list
        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course course) {
                    String name = course.getName() != null ? course.getName().trim() : "";
                    String code = course.getCode() != null ? course.getCode().trim() : "";
                    if (!name.isEmpty() && !code.isEmpty()) {
                        setText(name + " — " + code);
                    } else if (!name.isEmpty()) {
                        setText(name);
                    } else if (!code.isEmpty()) {
                        setText(code);
                    } else {
                        setText("(untitled course)");
                    }
                }
                return this;
            }
        });
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.addListSelectionListener(e -> onCourseSelected());
        
        JScrollPane courseScrollPane = new JScrollPane(courseList);
        coursePanel.add(courseScrollPane, BorderLayout.CENTER);
        
        // Course form panel
        JPanel courseFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        courseFormPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        courseNameField = new JTextField(20);
        courseFormPanel.add(courseNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        courseFormPanel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1;
        courseCodeField = new JTextField(20);
        courseFormPanel.add(courseCodeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        courseFormPanel.add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1;
        instructorField = new JTextField(20);
        courseFormPanel.add(instructorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        courseFormPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        creditsField = new JTextField(20);
        courseFormPanel.add(creditsField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(e -> onAddCourse());
        updateCourseButton = new JButton("Update Course");
        updateCourseButton.addActionListener(e -> onUpdateCourse());
        removeCourseButton = new JButton("Remove Course");
        removeCourseButton.addActionListener(e -> onRemoveCourse());
        
        buttonPanel.add(addCourseButton);
        buttonPanel.add(updateCourseButton);
        buttonPanel.add(removeCourseButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        courseFormPanel.add(buttonPanel, gbc);
        
        coursePanel.add(courseFormPanel, BorderLayout.EAST);
    }
    
    /**
     * Creates the task management panel.
     */
    private void createTaskPanel() {
        taskPanel = new JPanel(new BorderLayout());
        taskPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Task list
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task task) {
                    String title = task.getTitle() != null ? task.getTitle().trim() : "";
                    if (title.isEmpty()) {
                        title = "(untitled task)";
                    }
                    StringBuilder line = new StringBuilder();
                    if (task.isCompleted()) {
                        line.append("[Done] ");
                    }
                    line.append(title);
                    String due = task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : "";
                    if (!due.isEmpty()) {
                        line.append(" · ").append(due);
                    }
                    PlannerModel pm = (PlannerModel) PlannerView.this.getModel();
                    if (task.getCourseId() != null && pm != null) {
                        for (Course c : pm.getCourses()) {
                            if (c.getId().equals(task.getCourseId())) {
                                String code = c.getCode() != null ? c.getCode().trim() : "";
                                if (!code.isEmpty()) {
                                    line.append(" · ").append(code);
                                }
                                break;
                            }
                        }
                    }
                    if (task.getPriority() != null) {
                        line.append(" · ").append(task.getPriority());
                    }
                    setText(line.toString());
                    if (!isSelected) {
                        if (task.isCompleted()) {
                            setBackground(AppTheme.taskCompletedBg());
                            setForeground(AppTheme.taskCompletedFg());
                        } else {
                            Color accent = AppTheme.colorFromHex(task.getAccentColorHex());
                            setBackground(AppTheme.taskAccentChipBackground(accent));
                            setForeground(AppTheme.taskAccentChipForeground(accent));
                        }
                    }
                }
                return this;
            }
        });
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.addListSelectionListener(e -> onTaskSelected());
        
        JScrollPane taskScrollPane = new JScrollPane(taskList);
        taskPanel.add(taskScrollPane, BorderLayout.CENTER);
        
        // Task form panel
        JPanel taskFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        taskFormPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        taskTitleField = new JTextField(20);
        taskFormPanel.add(taskTitleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        taskFormPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        taskDescriptionArea = new JTextArea(3, 20);
        taskDescriptionArea.setLineWrap(true);
        taskDescriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(taskDescriptionArea);
        taskFormPanel.add(descScrollPane, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        taskFormPanel.add(new JLabel("Due date:"), gbc);
        gbc.gridx = 1;
        Calendar today = Calendar.getInstance();
        SpinnerDateModel taskDateModel = new SpinnerDateModel(today.getTime(), null, null, Calendar.DAY_OF_MONTH);
        taskDateSpinner = new JSpinner(taskDateModel);
        taskDateSpinner.setEditor(new JSpinner.DateEditor(taskDateSpinner, "MMMM d, yyyy"));
        taskFormPanel.add(taskDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        taskFormPanel.add(new JLabel("Due time:"), gbc);
        gbc.gridx = 1;
        taskTimeButton = new JButton();
        taskDueTime = LocalTime.now().withSecond(0).withNano(0);
        refreshTaskTimeButtonLabel();
        taskTimeButton.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(taskTimeButton);
            LocalTime picked = TimeWheelDialog.showDialog(w, taskDueTime);
            if (picked != null) {
                taskDueTime = picked.withSecond(0).withNano(0);
                refreshTaskTimeButtonLabel();
            }
        });
        taskFormPanel.add(taskTimeButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        taskFormPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        taskPriorityCombo = new JComboBox<>(Task.Priority.values());
        taskFormPanel.add(taskPriorityCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        taskFormPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        taskCourseCombo = new JComboBox<>();
        taskCourseCombo.addItem("None");
        taskFormPanel.add(taskCourseCombo, gbc);
        
        taskFormAccentHex = Task.DEFAULT_ACCENT_COLOR_HEX;
        
        gbc.gridx = 0; gbc.gridy = 6;
        taskFormPanel.add(new JLabel("Task color:"), gbc);
        gbc.gridx = 1;
        JPanel colorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        taskColorSwatchPanels.clear();
        for (String hex : TaskPalette.HEX_CHOICES) {
            JPanel sw = new JPanel();
            sw.setPreferredSize(new Dimension(28, 28));
            sw.setBackground(AppTheme.colorFromHex(hex));
            sw.setOpaque(true);
            sw.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String hexCapture = hex;
            sw.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    taskFormAccentHex = TaskPalette.canonicalHex(hexCapture);
                    refreshTaskColorFormWidgets();
                }
            });
            taskColorSwatchPanels.add(sw);
            colorRow.add(sw);
        }
        taskColorPreviewToggle = new JToggleButton("Task");
        taskColorPreviewToggle.setOpaque(true);
        taskColorPreviewToggle.setFocusable(false);
        taskColorPreviewToggle.setToolTipText("Preview of task chip color");
        colorRow.add(taskColorPreviewToggle);
        resetTaskColorButton = new JButton("Reset color");
        resetTaskColorButton.setToolTipText("Reset to default (red)");
        resetTaskColorButton.addActionListener(e -> {
            taskFormAccentHex = Task.DEFAULT_ACCENT_COLOR_HEX;
            refreshTaskColorFormWidgets();
        });
        colorRow.add(resetTaskColorButton);
        taskFormPanel.add(colorRow, gbc);
        refreshTaskColorFormWidgets();
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> onAddTask());
        updateTaskButton = new JButton("Update Task");
        updateTaskButton.addActionListener(e -> onUpdateTask());
        removeTaskButton = new JButton("Remove Task");
        removeTaskButton.addActionListener(e -> onRemoveTask());
        completeTaskButton = new JButton("Toggle Complete");
        completeTaskButton.addActionListener(e -> onToggleTaskComplete());
        
        buttonPanel.add(addTaskButton);
        buttonPanel.add(updateTaskButton);
        buttonPanel.add(removeTaskButton);
        buttonPanel.add(completeTaskButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        taskFormPanel.add(buttonPanel, gbc);
        
        taskPanel.add(taskFormPanel, BorderLayout.EAST);
    }
    
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
                case "TASK_INCOMPLETED":
                    refreshTaskList();
                    refreshWeeklyPriorities();
                    refreshTodayTasks();
                    checkDueDateReminders();
                    break;
                case "PLANNER_CLEARED":
                    refreshAll();
                    break;
            }
        });
    }
    
    /**
     * Refreshes all UI components.
     */
    private void refreshAll() {
        refreshStudentInfo();
        refreshCourseList();
        refreshTaskList();
        refreshTaskCourseCombo();
        refreshWeeklyPriorities();
        refreshTodayTasks();
        checkDueDateReminders();
    }
    
    /**
     * Refreshes the student information display.
     */
    private void refreshStudentInfo() {
        PlannerModel model = (PlannerModel) getModel();
        Student student = model.getCurrentStudent();
        if (student != null) {
            studentInfoLabel.setText(String.format("Current Student: %s (%s) - %s, Year %d, %s", 
                student.getFullName(), student.getStudentId(), 
                student.getEmail(), student.getYear(), student.getMajor()));
        } else {
            studentInfoLabel.setText("No student profile set");
        }
    }
    
    /**
     * Refreshes the course list.
     */
    private void refreshCourseList() {
        PlannerModel model = (PlannerModel) getModel();
        courseListModel.clear();
        for (Course course : model.getCourses()) {
            courseListModel.addElement(course);
        }
    }
    
    /**
     * Refreshes the task list.
     */
    private void refreshTaskList() {
        PlannerModel model = (PlannerModel) getModel();
        taskListModel.clear();
        for (Task task : model.getTasks()) {
            taskListModel.addElement(task);
        }
        pruneReminderKeysForRemovedTasks(model.getTasks());
    }
    
    /**
     * Refreshes the task course combo box.
     */
    private void refreshTaskCourseCombo() {
        PlannerModel model = (PlannerModel) getModel();
        taskCourseCombo.removeAllItems();
        taskCourseCombo.addItem("None");
        for (Course course : model.getCourses()) {
            taskCourseCombo.addItem(course.getCode() + " - " + course.getName());
        }
    }

    private void createWeeklyPrioritiesPanel() {
        weeklyPrioritiesPanel = new JPanel(new BorderLayout());
        weeklyPrioritiesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        weeklyPrioritiesRangeLabel = new JLabel("", JLabel.LEFT);
        weeklyPrioritiesRangeLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        weeklyPrioritiesPanel.add(weeklyPrioritiesRangeLabel, BorderLayout.NORTH);

        weeklyPrioritiesListModel = new DefaultListModel<>();
        weeklyPrioritiesList = new JList<>(weeklyPrioritiesListModel);
        weeklyPrioritiesList.setCellRenderer(new ListCellRenderer<Task>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JCheckBox box = new JCheckBox();
                box.setOpaque(true);
                box.setBorder(new EmptyBorder(6, 8, 6, 8));
                box.setSelected(task != null && task.isCompleted());
                if (task != null) {
                    String title = task.getTitle() != null && !task.getTitle().isBlank()
                            ? task.getTitle().trim()
                            : "(untitled task)";
                    box.setText(title + " · " + task.getPriority() + " · " + task.getDueDate().format(DATE_FORMATTER));
                    if (isSelected) {
                        box.setBackground(list.getSelectionBackground());
                        box.setForeground(list.getSelectionForeground());
                    } else if (task.isCompleted()) {
                        box.setBackground(AppTheme.taskCompletedBg());
                        box.setForeground(AppTheme.taskCompletedFg());
                    } else {
                        Color accent = AppTheme.colorFromHex(task.getAccentColorHex());
                        box.setBackground(AppTheme.taskAccentChipBackground(accent));
                        box.setForeground(AppTheme.taskAccentChipForeground(accent));
                    }
                }
                return box;
            }
        });
        weeklyPrioritiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        weeklyPrioritiesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = weeklyPrioritiesList.locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }
                Rectangle cellBounds = weeklyPrioritiesList.getCellBounds(index, index);
                if (cellBounds == null) {
                    return;
                }
                int relativeX = e.getX() - cellBounds.x;
                if (relativeX > 28) {
                    return;
                }
                Task task = weeklyPrioritiesListModel.get(index);
                if (getController() instanceof PlannerController controller) {
                    try {
                        controller.toggleTaskCompletionSilently(task.getId());
                    } catch (IllegalArgumentException ex) {
                        showError(ex.getMessage());
                    }
                }
            }
        });

        weeklyPrioritiesPanel.add(new JScrollPane(weeklyPrioritiesList), BorderLayout.CENTER);
        refreshWeeklyPriorities();
    }

    private void refreshWeeklyPriorities() {
        if (weeklyPrioritiesListModel == null) {
            return;
        }
        PlannerModel model = (PlannerModel) getModel();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        weeklyPrioritiesRangeLabel.setText(
                "Most important tasks due this week (" + weekStart + " to " + weekEnd + ")"
        );

        List<Task> weeklyTasks = model.getTasks().stream()
                .filter(task -> {
                    LocalDate dueDate = task.getDueDate().toLocalDate();
                    return !dueDate.isBefore(weekStart) && !dueDate.isAfter(weekEnd);
                })
                .sorted((a, b) -> {
                    int prio = Integer.compare(priorityRank(a.getPriority()), priorityRank(b.getPriority()));
                    if (prio != 0) return prio;
                    int due = a.getDueDate().compareTo(b.getDueDate());
                    if (due != 0) return due;
                    return Boolean.compare(a.isCompleted(), b.isCompleted());
                })
                .toList();

        weeklyPrioritiesListModel.clear();
        for (Task task : weeklyTasks) {
            weeklyPrioritiesListModel.addElement(task);
        }
    }

    private int priorityRank(Task.Priority priority) {
        if (priority == null) return 3;
        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }
    
    /**
     * Creates the Today panel showing tasks due today.
     */
    private void createTodayPanel() {
        todayPanel = new JPanel(new BorderLayout());
        todayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header with current date
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        todayDateLabel = new JLabel("Today: " + today.format(dateFormatter), JLabel.LEFT);
        todayDateLabel.setFont(todayDateLabel.getFont().deriveFont(Font.BOLD, 16f));
        todayDateLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        todayPanel.add(todayDateLabel, BorderLayout.NORTH);
        
        // Task count label
        todayTaskCountLabel = new JLabel("", JLabel.LEFT);
        todayTaskCountLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        todayPanel.add(todayTaskCountLabel, BorderLayout.NORTH);
        
        // Today's tasks list
        todayTaskListModel = new DefaultListModel<>();
        todayTaskList = new JList<>(todayTaskListModel);
        todayTaskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task task) {
                    String title = task.getTitle() != null ? task.getTitle().trim() : "";
                    if (title.isEmpty()) {
                        title = "(untitled task)";
                    }
                    StringBuilder line = new StringBuilder();
                    if (task.isCompleted()) {
                        line.append("[Done] ");
                    }
                    line.append(title);
                    
                    // Add time if available
                    if (task.getDueDate() != null) {
                        LocalTime time = task.getDueDate().toLocalTime();
                        if (!time.equals(LocalTime.MIDNIGHT)) {
                            line.append(" · ").append(time.format(DateTimeFormatter.ofPattern("h:mm a")));
                        }
                    }
                    
                    // Add priority
                    if (task.getPriority() != null) {
                        line.append(" · ").append(task.getPriority());
                    }
                    
                    // Add course if available
                    PlannerModel pm = (PlannerModel) PlannerView.this.getModel();
                    if (task.getCourseId() != null && pm != null) {
                        for (Course c : pm.getCourses()) {
                            if (c.getId().equals(task.getCourseId())) {
                                String code = c.getCode() != null ? c.getCode().trim() : "";
                                if (!code.isEmpty()) {
                                    line.append(" · ").append(code);
                                }
                                break;
                            }
                        }
                    }
                    
                    setText(line.toString());
                    if (!isSelected) {
                        if (task.isCompleted()) {
                            setBackground(AppTheme.taskCompletedBg());
                            setForeground(AppTheme.taskCompletedFg());
                        } else {
                            Color accent = AppTheme.colorFromHex(task.getAccentColorHex());
                            setBackground(AppTheme.taskAccentChipBackground(accent));
                            setForeground(AppTheme.taskAccentChipForeground(accent));
                        }
                    }
                }
                return this;
            }
        });
        todayTaskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane todayScrollPane = new JScrollPane(todayTaskList);
        todayPanel.add(todayScrollPane, BorderLayout.CENTER);
        
        refreshTodayTasks();
    }
    
    /**
     * Refreshes the Today tab with current day's tasks.
     */
    private void refreshTodayTasks() {
        if (todayTaskListModel == null) {
            return;
        }
        
        PlannerModel model = (PlannerModel) getModel();
        List<Task> todayTasks = model.getTasksForToday();
        
        // Update task count label
        int incompleteCount = (int) todayTasks.stream().filter(task -> !task.isCompleted()).count();
        int totalCount = todayTasks.size();
        todayTaskCountLabel.setText(String.format("%d task%s due today (%d incomplete)", 
                totalCount, totalCount != 1 ? "s" : "", incompleteCount));
        
        // Update task list
        todayTaskListModel.clear();
        for (Task task : todayTasks) {
            todayTaskListModel.addElement(task);
        }
    }
    
    // Event handlers
    private void onSaveStudent() {
        if (getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            Student.AcademicYear selectedYear = (Student.AcademicYear) yearComboBox.getSelectedItem();
            controller.saveStudentProfile(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                studentIdField.getText().trim(),
                selectedYear,
                majorField.getText().trim()
            );
        }
    }
    
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
    
    private void onUpdateCourse() {
        Course selected = courseList.getSelectedValue();
        if (selected != null && getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.updateCourse(
                selected.getId(),
                courseNameField.getText().trim(),
                courseCodeField.getText().trim(),
                instructorField.getText().trim(),
                creditsField.getText().trim()
            );
        }
    }
    
    private void onRemoveCourse() {
        Course selected = courseList.getSelectedValue();
        if (selected != null && getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.removeCourse(selected.getId());
        }
    }
    
    private void onAddTask() {
        if (getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.addTask(
                taskTitleField.getText().trim(),
                taskDescriptionArea.getText().trim(),
                getTaskDueDateTime(),
                (Task.Priority) taskPriorityCombo.getSelectedItem(),
                taskCourseCombo.getSelectedIndex() > 0 ? 
                    ((PlannerModel) getModel()).getCourses().get(taskCourseCombo.getSelectedIndex() - 1).getId() : null,
                taskFormAccentHex
            );
        }
    }
    
    private void onUpdateTask() {
        Task selected = taskList.getSelectedValue();
        if (selected != null && getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.updateTask(
                selected.getId(),
                taskTitleField.getText().trim(),
                taskDescriptionArea.getText().trim(),
                getTaskDueDateTime(),
                (Task.Priority) taskPriorityCombo.getSelectedItem(),
                taskCourseCombo.getSelectedIndex() > 0 ? 
                    ((PlannerModel) getModel()).getCourses().get(taskCourseCombo.getSelectedIndex() - 1).getId() : null,
                taskFormAccentHex
            );
        }
    }
    
    private void onRemoveTask() {
        Task selected = taskList.getSelectedValue();
        if (selected != null && getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.removeTask(selected.getId());
        }
    }
    
    private void onToggleTaskComplete() {
        Task selected = taskList.getSelectedValue();
        if (selected != null && getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.toggleTaskCompletion(selected.getId());
        }
    }
    
    private void onCourseSelected() {
        Course selected = courseList.getSelectedValue();
        if (selected != null) {
            courseNameField.setText(selected.getName());
            courseCodeField.setText(selected.getCode());
            instructorField.setText(selected.getInstructor());
            creditsField.setText(String.valueOf(selected.getCredits()));
        }
    }
    
    private void onTaskSelected() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            taskTitleField.setText(selected.getTitle());
            taskDescriptionArea.setText(selected.getDescription());
            LocalDateTime due = selected.getDueDate();
            setSpinnerToLocalDate(taskDateSpinner, due.toLocalDate());
            taskDueTime = due.toLocalTime().withSecond(0).withNano(0);
            refreshTaskTimeButtonLabel();
            taskPriorityCombo.setSelectedItem(selected.getPriority());
            
            if (selected.getCourseId() != null) {
                // Find and select the course
                PlannerModel model = (PlannerModel) getModel();
                for (int i = 0; i < model.getCourses().size(); i++) {
                    if (model.getCourses().get(i).getId().equals(selected.getCourseId())) {
                        taskCourseCombo.setSelectedIndex(i + 1); // +1 because of "None" option
                        break;
                    }
                }
            } else {
                taskCourseCombo.setSelectedIndex(0);
            }
            taskFormAccentHex = TaskPalette.canonicalHex(selected.getAccentColorHex());
            refreshTaskColorFormWidgets();
        }
    }

    private void refreshTaskColorFormWidgets() {
        if (taskColorPreviewToggle == null) {
            return;
        }
        String canonical = TaskPalette.canonicalHex(taskFormAccentHex);
        taskFormAccentHex = canonical;
        Color ac = AppTheme.colorFromHex(canonical);
        taskColorPreviewToggle.setBackground(ac);
        taskColorPreviewToggle.setForeground(AppTheme.contrastingForeground(ac));
        for (int i = 0; i < taskColorSwatchPanels.size() && i < TaskPalette.HEX_CHOICES.length; i++) {
            JPanel p = taskColorSwatchPanels.get(i);
            boolean sel = TaskPalette.HEX_CHOICES[i].equalsIgnoreCase(canonical);
            p.setBorder(BorderFactory.createLineBorder(sel ? new Color(40, 100, 220) : Color.GRAY, sel ? 3 : 1));
        }
    }

    private void refreshTaskTimeButtonLabel() {
        if (taskTimeButton != null && taskDueTime != null) {
            taskTimeButton.setText(TIME_BUTTON_FORMAT.format(taskDueTime));
        }
    }

    private void initializeDueReminders() {
        dueReminderTimer = new Timer(60_000, e -> checkDueDateReminders());
        dueReminderTimer.setInitialDelay(10_000);
        dueReminderTimer.start();
        checkDueDateReminders();
    }

    private void checkDueDateReminders() {
        PlannerModel model = (PlannerModel) getModel();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        for (Task task : model.getTasks()) {
            if (task.isCompleted()) {
                continue;
            }
            LocalDateTime due = task.getDueDate();
            LocalDate dueDate = due.toLocalDate();
            LocalDate reminderStart = dueDate.minusDays(7);
            boolean shouldRemind = !today.isBefore(reminderStart) && !today.isAfter(dueDate);
            if (!shouldRemind) {
                continue;
            }
            String reminderKey = task.getId() + "|" + due + "|" + today;
            if (shownDailyReminderKeys.contains(reminderKey)) {
                continue;
            }
            shownDailyReminderKeys.add(reminderKey);
            long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);
            showDueReminderPopup(task, due, daysUntilDue);
        }
    }

    private void showDueReminderPopup(Task task, LocalDateTime due, long daysUntilDue) {
        String status;
        if (daysUntilDue > 1) {
            status = "Due in " + daysUntilDue + " days";
        } else if (daysUntilDue == 1) {
            status = "Due tomorrow";
        } else if (daysUntilDue == 0) {
            status = "Due today";
        } else {
            status = "Overdue";
        }

        JDialog reminderDialog = new JDialog(frame, "Task Reminder", false);
        reminderDialog.setLayout(new BorderLayout(10, 8));
        reminderDialog.getRootPane().setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel title = new JLabel(task.getTitle() != null && !task.getTitle().isBlank()
                ? task.getTitle()
                : "(untitled task)");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        JLabel detail = new JLabel(status + " · " + due.format(DATE_FORMATTER));
        reminderDialog.add(title, BorderLayout.NORTH);
        reminderDialog.add(detail, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton dismiss = new JButton("Dismiss");
        dismiss.addActionListener(e -> reminderDialog.dispose());
        actions.add(dismiss);
        reminderDialog.add(actions, BorderLayout.SOUTH);

        reminderDialog.pack();
        reminderDialog.setLocationRelativeTo(frame);
        reminderDialog.setAlwaysOnTop(true);
        reminderDialog.setVisible(true);

        Timer autoClose = new Timer(10_000, e -> reminderDialog.dispose());
        autoClose.setRepeats(false);
        autoClose.start();
    }

    private void pruneReminderKeysForRemovedTasks(List<Task> currentTasks) {
        Set<String> activeTaskIds = new HashSet<>();
        for (Task task : currentTasks) {
            activeTaskIds.add(task.getId());
        }
        shownDailyReminderKeys.removeIf(key -> {
            int sep = key.indexOf('|');
            if (sep <= 0) {
                return true;
            }
            String taskId = key.substring(0, sep);
            return !activeTaskIds.contains(taskId);
        });
    }

    private LocalDateTime getTaskDueDateTime() {
        LocalDate date = localDateFromSpinner(taskDateSpinner);
        LocalTime time = taskDueTime != null ? taskDueTime : LocalTime.NOON;
        return LocalDateTime.of(date, time);
    }

    private static LocalDate localDateFromSpinner(JSpinner spinner) {
        Date d = (Date) spinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static void setSpinnerToLocalDate(JSpinner spinner, LocalDate ld) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth(), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        spinner.setValue(cal.getTime());
    }
    
    /**
     * Shows an error message dialog.
     * @param message The error message to display
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an information message dialog.
     * @param message The information message to display
     */
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}

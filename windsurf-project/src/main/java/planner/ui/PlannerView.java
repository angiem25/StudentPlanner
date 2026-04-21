package planner.ui;

import mvc.AbstractView;
import mvc.ModelEvent;
import planner.model.*;
import planner.ui.calendar.CalendarView;
import planner.ui.timer.TimerPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main view for the student planner application.
 * Provides a tabbed interface for managing students, courses, and tasks.
 */
public class PlannerView extends AbstractView {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private JFrame frame;
    private JTabbedPane tabbedPane;
    
    // Student panel components
    private JPanel studentPanel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField studentIdField;
    private JTextField yearField;
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
    private JTextField taskDueDateField;
    private JComboBox<Task.Priority> taskPriorityCombo;
    private JComboBox<String> taskCourseCombo;
    private JButton addTaskButton;
    private JButton updateTaskButton;
    private JButton removeTaskButton;
    private JButton completeTaskButton;
    
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
        
        tabbedPane.addTab("Student Profile", studentPanel);
        tabbedPane.addTab("Courses", coursePanel);
        tabbedPane.addTab("Tasks", taskPanel);
        
        // Add Calendar tab
        CalendarView calendarView = new CalendarView((PlannerModel) getModel(), 
            (PlannerController) getController());
        tabbedPane.addTab("Calendar", calendarView);
        
        // Add Timer tab
        TimerPanel timerPanel = new TimerPanel();
        tabbedPane.addTab("Timer", timerPanel);
        
        frame.add(tabbedPane);
        frame.setVisible(true);
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
        yearField = new JTextField(20);
        formPanel.add(yearField, gbc);
        
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
        taskFormPanel.add(new JLabel("Due Date (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        taskDueDateField = new JTextField(20);
        taskFormPanel.add(taskDueDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        taskFormPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        taskPriorityCombo = new JComboBox<>(Task.Priority.values());
        taskFormPanel.add(taskPriorityCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        taskFormPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        taskCourseCombo = new JComboBox<>();
        taskCourseCombo.addItem("None");
        taskFormPanel.add(taskCourseCombo, gbc);
        
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
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
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
    
    // Event handlers
    private void onSaveStudent() {
        if (getController() instanceof PlannerController) {
            PlannerController controller = (PlannerController) getController();
            controller.saveStudentProfile(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                studentIdField.getText().trim(),
                yearField.getText().trim(),
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
                taskDueDateField.getText().trim(),
                (Task.Priority) taskPriorityCombo.getSelectedItem(),
                taskCourseCombo.getSelectedIndex() > 0 ? 
                    ((PlannerModel) getModel()).getCourses().get(taskCourseCombo.getSelectedIndex() - 1).getId() : null
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
                taskDueDateField.getText().trim(),
                (Task.Priority) taskPriorityCombo.getSelectedItem(),
                taskCourseCombo.getSelectedIndex() > 0 ? 
                    ((PlannerModel) getModel()).getCourses().get(taskCourseCombo.getSelectedIndex() - 1).getId() : null
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
            taskDueDateField.setText(selected.getDueDate().format(DATE_FORMATTER));
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
        }
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

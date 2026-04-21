package planner;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import mvc.JFrameView;
import mvc.ModelEvent;

public class PlannerView extends JFrameView {
    private final DefaultListModel<String> taskListModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(taskListModel);
    private final JTextArea dayArea = new JTextArea();
    private final JTextArea weekArea = new JTextArea();
    private final JTextArea monthArea = new JTextArea();
    private final JLabel timerLabel = new JLabel("00:00");

    public PlannerView(PlannerModel model, PlannerController controller) {
        super(model, controller);
        setTitle("Student Planner MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("To-Do List", createTodoPanel());
        tabs.addTab("Calendar", createCalendarPanel());
        tabs.addTab("Timer", createTimerPanel());
        getContentPane().add(tabs, BorderLayout.CENTER);

        Timer swingTimer = new Timer(1000, e -> ((PlannerController) getController()).tickTimer());
        swingTimer.start();

        refreshData();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createTodoPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(2, 1, 6, 6));
        JPanel addRow = new JPanel(new GridLayout(1, 4, 6, 6));
        JTextField descField = new JTextField();
        JTextField dueDateField = new JTextField("YYYY-MM-DD");
        JComboBox<PlannerModel.Priority> priorityBox = new JComboBox<>(PlannerModel.Priority.values());
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> {
            LocalDate due = parseDateOrNull(dueDateField.getText());
            ((PlannerController) getController()).addTask(
                    descField.getText(),
                    due,
                    (PlannerModel.Priority) priorityBox.getSelectedItem()
            );
            descField.setText("");
        });
        addRow.add(descField);
        addRow.add(dueDateField);
        addRow.add(priorityBox);
        addRow.add(addButton);

        JPanel actions = new JPanel(new GridLayout(1, 4, 6, 6));
        JButton editButton = new JButton("Edit Task");
        JButton completeButton = new JButton("Mark Complete");
        JButton incompleteButton = new JButton("Mark Incomplete");
        JButton deleteButton = new JButton("Delete Task");
        editButton.addActionListener(e -> editSelectedTask());
        completeButton.addActionListener(e -> toggleSelectedTask(true));
        incompleteButton.addActionListener(e -> toggleSelectedTask(false));
        deleteButton.addActionListener(e -> {
            int idx = taskList.getSelectedIndex();
            if (idx >= 0 && confirmDelete()) {
                ((PlannerController) getController()).deleteTask(idx);
            }
        });
        actions.add(editButton);
        actions.add(completeButton);
        actions.add(incompleteButton);
        actions.add(deleteButton);

        form.add(addRow);
        form.add(actions);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel eventInput = new JPanel(new GridLayout(2, 1, 6, 6));
        JPanel topRow = new JPanel(new GridLayout(1, 4, 6, 6));
        JTextField titleField = new JTextField();
        JTextField startField = new JTextField("YYYY-MM-DDTHH:MM");
        JTextField endField = new JTextField("YYYY-MM-DDTHH:MM");
        JButton addEventButton = new JButton("Add Event");
        addEventButton.addActionListener(e -> {
            try {
                LocalDateTime start = LocalDateTime.parse(startField.getText().trim());
                LocalDateTime end = LocalDateTime.parse(endField.getText().trim());
                ((PlannerController) getController()).addEvent(titleField.getText(), start, end, "");
                titleField.setText("");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Use datetime format YYYY-MM-DDTHH:MM", "Invalid datetime", JOptionPane.ERROR_MESSAGE);
            }
        });
        topRow.add(titleField);
        topRow.add(startField);
        topRow.add(endField);
        topRow.add(addEventButton);

        eventInput.add(topRow);
        eventInput.add(new JLabel("Calendar displays current day, week, and month events."));

        JPanel views = new JPanel(new GridLayout(1, 3, 8, 8));
        dayArea.setEditable(false);
        weekArea.setEditable(false);
        monthArea.setEditable(false);
        views.add(new JScrollPane(dayArea));
        views.add(new JScrollPane(weekArea));
        views.add(new JScrollPane(monthArea));

        panel.add(eventInput, BorderLayout.NORTH);
        panel.add(views, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 8, 8));

        JPanel config = new JPanel(new GridLayout(1, 2, 6, 6));
        JTextField minutesField = new JTextField("30");
        JButton setButton = new JButton("Set Minutes");
        setButton.addActionListener(e -> {
            try {
                ((PlannerController) getController()).setTimerMinutes(Integer.parseInt(minutesField.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a whole number of minutes", "Invalid minutes", JOptionPane.ERROR_MESSAGE);
            }
        });
        config.add(minutesField);
        config.add(setButton);

        JPanel controls = new JPanel(new GridLayout(1, 3, 6, 6));
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");
        startButton.addActionListener(e -> ((PlannerController) getController()).startTimer());
        pauseButton.addActionListener(e -> ((PlannerController) getController()).pauseTimer());
        resetButton.addActionListener(e -> ((PlannerController) getController()).resetTimer());
        controls.add(startButton);
        controls.add(pauseButton);
        controls.add(resetButton);

        panel.add(timerLabel);
        panel.add(config);
        panel.add(controls);
        return panel;
    }

    private void editSelectedTask() {
        int idx = taskList.getSelectedIndex();
        if (idx < 0) {
            return;
        }
        PlannerModel.Task task = ((PlannerModel) getModel()).getTasks().get(idx);
        JTextField descField = new JTextField(task.getDescription());
        JTextField dueDateField = new JTextField(task.getDueDate() == null ? "" : task.getDueDate().toString());
        JComboBox<PlannerModel.Priority> priorityBox = new JComboBox<>(PlannerModel.Priority.values());
        priorityBox.setSelectedItem(task.getPriority());
        JCheckBox completedBox = new JCheckBox("Completed", task.isCompleted());

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Description"));
        form.add(descField);
        form.add(new JLabel("Due Date (YYYY-MM-DD, optional)"));
        form.add(dueDateField);
        form.add(new JLabel("Priority"));
        form.add(priorityBox);
        form.add(completedBox);

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            ((PlannerController) getController()).editTask(
                    idx,
                    descField.getText(),
                    parseDateOrNull(dueDateField.getText()),
                    (PlannerModel.Priority) priorityBox.getSelectedItem()
            );
            ((PlannerController) getController()).setTaskCompleted(idx, completedBox.isSelected());
        }
    }

    private void toggleSelectedTask(boolean completed) {
        int idx = taskList.getSelectedIndex();
        if (idx >= 0) {
            ((PlannerController) getController()).setTaskCompleted(idx, completed);
        }
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private LocalDate parseDateOrNull(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty() || "YYYY-MM-DD".equals(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Use date format YYYY-MM-DD", "Invalid date", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void modelChanged(ModelEvent event) {
        refreshData();
    }

    private void refreshData() {
        PlannerModel model = (PlannerModel) getModel();
        taskListModel.clear();
        for (PlannerModel.Task task : model.getTasks()) {
            String due = task.getDueDate() == null ? "No due date" : task.getDueDate().toString();
            String state = task.isCompleted() ? "[Done]" : "[Todo]";
            taskListModel.addElement(state + " " + task.getDescription() + " (Due: " + due + ", " + task.getPriority() + ")");
        }

        dayArea.setText("Day View\n");
        appendEvents(dayArea, model.getDayEvents(LocalDate.now()));

        weekArea.setText("Week View\n");
        appendEvents(weekArea, model.getWeekEvents());

        monthArea.setText("Month View\n");
        appendEvents(monthArea, model.getMonthEvents());

        int sec = model.getTimerRemainingSeconds();
        timerLabel.setText(String.format("%02d:%02d", sec / 60, sec % 60));
    }

    private void appendEvents(JTextArea area, Map<LocalDate, List<PlannerModel.CalendarEvent>> events) {
        if (events.isEmpty()) {
            area.append("No events.\n");
            return;
        }
        for (Map.Entry<LocalDate, List<PlannerModel.CalendarEvent>> entry : events.entrySet()) {
            for (PlannerModel.CalendarEvent event : entry.getValue()) {
                area.append(entry.getKey() + " - " + event.getTitle() + " [" + event.getStart().toLocalTime() + " to " + event.getEnd().toLocalTime() + "]\n");
            }
        }
    }
}

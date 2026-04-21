package planner;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import mvc.JFrameView;
import mvc.ModelEvent;

public class PlannerView extends JFrameView {
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String CLEAR = "Clr";
    public static final String EQUALS = "=";

    private final JTextField textField = new JTextField();
    private final DefaultListModel<String> todoModel = new DefaultListModel<>();
    private final JTextArea weekArea = new JTextArea();
    private final JTextArea monthArea = new JTextArea();

    public PlannerView(PlannerModel model, PlannerController controller) {
        super(model, controller);
        setTitle("Student Planner MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Calculator", createCalculatorPanel());
        tabs.addTab("Planner", createPlannerPanel());
        getContentPane().add(tabs, BorderLayout.CENTER);

        refreshPlannerData();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createCalculatorPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        textField.setText("0");
        panel.add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        Handler handler = new Handler();

        for (int i = 1; i <= 9; i++) {
            JButton numberButton = new JButton(String.valueOf(i));
            numberButton.addActionListener(handler);
            buttonPanel.add(numberButton);
        }

        JButton zero = new JButton("0");
        JButton minus = new JButton(MINUS);
        JButton plus = new JButton(PLUS);
        JButton clear = new JButton(CLEAR);
        JButton equals = new JButton(EQUALS);
        zero.addActionListener(handler);
        minus.addActionListener(handler);
        plus.addActionListener(handler);
        clear.addActionListener(handler);
        equals.addActionListener(handler);

        buttonPanel.add(zero);
        buttonPanel.add(minus);
        buttonPanel.add(plus);
        buttonPanel.add(clear);
        buttonPanel.add(equals);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlannerPanel() {
        JPanel plannerPanel = new JPanel(new BorderLayout(8, 8));

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 8, 8));

        JPanel todoInput = new JPanel(new BorderLayout(5, 5));
        JTextField todoField = new JTextField();
        JButton addTodoBtn = new JButton("Add To-Do Today");
        addTodoBtn.addActionListener(e -> {
            ((PlannerController) getController()).addTodo(todoField.getText());
            todoField.setText("");
        });
        todoInput.add(todoField, BorderLayout.CENTER);
        todoInput.add(addTodoBtn, BorderLayout.EAST);

        JPanel eventInput = new JPanel(new GridLayout(1, 3, 5, 5));
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField eventField = new JTextField();
        JButton addEventBtn = new JButton("Add Event");
        addEventBtn.addActionListener(e -> addEventFromInputs(dateField.getText(), eventField.getText()));
        eventInput.add(dateField);
        eventInput.add(eventField);
        eventInput.add(addEventBtn);

        inputPanel.add(todoInput);
        inputPanel.add(eventInput);

        JPanel dataPanel = new JPanel(new GridLayout(1, 3, 8, 8));
        dataPanel.add(new JScrollPane(new JList<>(todoModel)));
        weekArea.setEditable(false);
        monthArea.setEditable(false);
        dataPanel.add(new JScrollPane(weekArea));
        dataPanel.add(new JScrollPane(monthArea));

        plannerPanel.add(inputPanel, BorderLayout.NORTH);
        plannerPanel.add(new JLabel("Today To-Do | Week View | Month View"), BorderLayout.CENTER);
        plannerPanel.add(dataPanel, BorderLayout.SOUTH);
        return plannerPanel;
    }

    private void addEventFromInputs(String dateText, String eventText) {
        try {
            LocalDate date = LocalDate.parse(dateText.trim());
            ((PlannerController) getController()).addEvent(date, eventText);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Use date format YYYY-MM-DD", "Invalid date", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modelChanged(ModelEvent event) {
        textField.setText(String.valueOf(event.getAmount()));
        refreshPlannerData();
    }

    private void refreshPlannerData() {
        PlannerModel model = (PlannerModel) getModel();
        todoModel.clear();
        for (String todo : model.getTodayTodos()) {
            todoModel.addElement(todo);
        }

        weekArea.setText("Week View\n");
        appendEvents(weekArea, model.getWeekEvents());

        monthArea.setText("Month View\n");
        appendEvents(monthArea, model.getMonthEvents());
    }

    private void appendEvents(JTextArea area, Map<LocalDate, List<String>> events) {
        if (events.isEmpty()) {
            area.append("No events.\n");
            return;
        }
        for (Map.Entry<LocalDate, List<String>> entry : events.entrySet()) {
            area.append(entry.getKey() + ": " + String.join(", ", entry.getValue()) + "\n");
        }
    }

    class Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((PlannerController) getController()).operation(e.getActionCommand());
        }
    }
}

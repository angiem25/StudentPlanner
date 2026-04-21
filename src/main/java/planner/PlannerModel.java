package planner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mvc.AbstractModel;
import mvc.ModelEvent;

public class PlannerModel extends AbstractModel {
    private int total = 0;
    private int current = 0;
    private String state = "add";
    private final List<String> todayTodos = new ArrayList<>();
    private final Map<LocalDate, List<String>> eventsByDate = new LinkedHashMap<>();

    public void clear() {
        total = 0;
        store(0);
    }

    public void store(int value) {
        current = value;
        ModelEvent me = new ModelEvent(this, 1, "", current);
        notifyChanged(me);
    }

    public void add() {
        state = "add";
        total = current;
    }

    public void subtract() {
        state = "subtract";
        total = current;
    }

    public void equals() {
        if (state.equals("add")) {
            total += current;
        } else {
            total -= current;
        }
        current = total;
        ModelEvent me = new ModelEvent(this, 1, "", total);
        notifyChanged(me);
    }

    public void addTodoForToday(String todo) {
        if (todo != null && !todo.isBlank()) {
            todayTodos.add(todo.trim());
            notifyChanged(new ModelEvent(this, 2, "todo", current));
        }
    }

    public void addEvent(LocalDate date, String eventText) {
        if (date == null || eventText == null || eventText.isBlank()) {
            return;
        }
        eventsByDate.computeIfAbsent(date, ignored -> new ArrayList<>()).add(eventText.trim());
        notifyChanged(new ModelEvent(this, 3, "event", current));
    }

    public List<String> getTodayTodos() {
        return Collections.unmodifiableList(todayTodos);
    }

    public Map<LocalDate, List<String>> getWeekEvents() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(6);
        Map<LocalDate, List<String>> weekEvents = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, List<String>> entry : eventsByDate.entrySet()) {
            if (!entry.getKey().isBefore(start) && !entry.getKey().isAfter(end)) {
                weekEvents.put(entry.getKey(), List.copyOf(entry.getValue()));
            }
        }
        return weekEvents;
    }

    public Map<LocalDate, List<String>> getMonthEvents() {
        LocalDate now = LocalDate.now();
        Map<LocalDate, List<String>> monthEvents = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, List<String>> entry : eventsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            if (date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
                monthEvents.put(date, List.copyOf(entry.getValue()));
            }
        }
        return monthEvents;
    }
}

package planner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mvc.AbstractModel;
import mvc.ModelEvent;

public class PlannerModel extends AbstractModel {
    public enum Priority { LOW, MEDIUM, HIGH }

    public static final class Task {
        private final String description;
        private final LocalDate dueDate;
        private final Priority priority;
        private final boolean completed;

        public Task(String description, LocalDate dueDate, Priority priority, boolean completed) {
            this.description = description;
            this.dueDate = dueDate;
            this.priority = priority;
            this.completed = completed;
        }

        public String getDescription() {
            return description;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public Priority getPriority() {
            return priority;
        }

        public boolean isCompleted() {
            return completed;
        }
    }

    public static final class CalendarEvent {
        private final String title;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final String description;

        public CalendarEvent(String title, LocalDateTime start, LocalDateTime end, String description) {
            this.title = title;
            this.start = start;
            this.end = end;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public String getDescription() {
            return description;
        }
    }

    private final List<Task> tasks = new ArrayList<>();
    private final Map<LocalDate, List<CalendarEvent>> eventsByDate = new LinkedHashMap<>();
    private int timerInitialSeconds = 0;
    private int timerRemainingSeconds = 0;
    private boolean timerRunning = false;

    private static final Path DATA_FILE = Path.of(System.getProperty("user.home"), ".studentplanner-data.txt");

    public PlannerModel() {
        loadData();
    }

    public void addTask(String description, LocalDate dueDate, Priority priority) {
        if (description == null || description.isBlank()) {
            return;
        }
        tasks.add(new Task(description.trim(), dueDate, priority == null ? Priority.MEDIUM : priority, false));
        saveData();
        notifyChanged(new ModelEvent("taskAdded", this, tasks.size()));
    }

    public void editTask(int index, String description, LocalDate dueDate, Priority priority) {
        if (index < 0 || index >= tasks.size() || description == null || description.isBlank()) {
            return;
        }
        Task old = tasks.get(index);
        tasks.set(index, new Task(description.trim(), dueDate, priority == null ? old.getPriority() : priority, old.isCompleted()));
        saveData();
        notifyChanged(new ModelEvent("taskEdited", this, index));
    }

    public void setTaskCompleted(int index, boolean completed) {
        if (index < 0 || index >= tasks.size()) {
            return;
        }
        Task old = tasks.get(index);
        tasks.set(index, new Task(old.getDescription(), old.getDueDate(), old.getPriority(), completed));
        saveData();
        notifyChanged(new ModelEvent("taskCompletionChanged", this, index));
    }

    public void deleteTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            return;
        }
        tasks.remove(index);
        saveData();
        notifyChanged(new ModelEvent("taskDeleted", this, tasks.size()));
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void addEvent(String title, LocalDateTime start, LocalDateTime end, String description) {
        if (title == null || title.isBlank() || start == null || end == null || end.isBefore(start)) {
            return;
        }
        eventsByDate.computeIfAbsent(start.toLocalDate(), ignored -> new ArrayList<>())
                .add(new CalendarEvent(title.trim(), start, end, description == null ? "" : description.trim()));
        saveData();
        notifyChanged(new ModelEvent("eventAdded", this, eventsByDate.size()));
    }

    public void editEvent(LocalDate date, int index, String title, LocalDateTime start, LocalDateTime end, String description) {
        List<CalendarEvent> events = eventsByDate.get(date);
        if (events == null || index < 0 || index >= events.size() || title == null || title.isBlank() || start == null || end == null || end.isBefore(start)) {
            return;
        }
        events.set(index, new CalendarEvent(title.trim(), start, end, description == null ? "" : description.trim()));
        saveData();
        notifyChanged(new ModelEvent("eventEdited", this, index));
    }

    public void deleteEvent(LocalDate date, int index) {
        List<CalendarEvent> events = eventsByDate.get(date);
        if (events == null || index < 0 || index >= events.size()) {
            return;
        }
        events.remove(index);
        if (events.isEmpty()) {
            eventsByDate.remove(date);
        }
        saveData();
        notifyChanged(new ModelEvent("eventDeleted", this, eventsByDate.size()));
    }

    public Map<LocalDate, List<CalendarEvent>> getDayEvents(LocalDate day) {
        Map<LocalDate, List<CalendarEvent>> dayEvents = new LinkedHashMap<>();
        List<CalendarEvent> events = eventsByDate.get(day);
        if (events != null) {
            dayEvents.put(day, List.copyOf(events));
        }
        return dayEvents;
    }

    public Map<LocalDate, List<CalendarEvent>> getWeekEvents() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(6);
        Map<LocalDate, List<CalendarEvent>> weekEvents = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, List<CalendarEvent>> entry : eventsByDate.entrySet()) {
            if (!entry.getKey().isBefore(start) && !entry.getKey().isAfter(end)) {
                weekEvents.put(entry.getKey(), List.copyOf(entry.getValue()));
            }
        }
        return weekEvents;
    }

    public Map<LocalDate, List<CalendarEvent>> getMonthEvents() {
        LocalDate now = LocalDate.now();
        Map<LocalDate, List<CalendarEvent>> monthEvents = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, List<CalendarEvent>> entry : eventsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            if (date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
                monthEvents.put(date, List.copyOf(entry.getValue()));
            }
        }
        return monthEvents;
    }

    public void setTimerMinutes(int minutes) {
        if (minutes <= 0) {
            return;
        }
        timerInitialSeconds = minutes * 60;
        timerRemainingSeconds = timerInitialSeconds;
        timerRunning = false;
        notifyChanged(new ModelEvent("timerSet", this, timerRemainingSeconds));
    }

    public void startTimer() {
        timerRunning = true;
        notifyChanged(new ModelEvent("timerStarted", this, timerRemainingSeconds));
    }

    public void pauseTimer() {
        timerRunning = false;
        notifyChanged(new ModelEvent("timerPaused", this, timerRemainingSeconds));
    }

    public void resetTimer() {
        timerRemainingSeconds = timerInitialSeconds;
        timerRunning = false;
        notifyChanged(new ModelEvent("timerReset", this, timerRemainingSeconds));
    }

    public void tickTimer() {
        if (!timerRunning || timerRemainingSeconds <= 0) {
            return;
        }
        timerRemainingSeconds--;
        if (timerRemainingSeconds == 0) {
            timerRunning = false;
        }
        notifyChanged(new ModelEvent("timerTick", this, timerRemainingSeconds));
    }

    public int getTimerRemainingSeconds() {
        return timerRemainingSeconds;
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add("TASK|" + encode(task.getDescription()) + "|" + nullSafeDate(task.getDueDate()) + "|" + task.getPriority() + "|" + task.isCompleted());
        }
        for (Map.Entry<LocalDate, List<CalendarEvent>> entry : eventsByDate.entrySet()) {
            for (CalendarEvent event : entry.getValue()) {
                lines.add("EVENT|" + encode(event.getTitle()) + "|" + event.getStart() + "|" + event.getEnd() + "|" + encode(event.getDescription()));
            }
        }
        try {
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Keep app functional even if persistence fails.
        }
    }

    private void loadData() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 0) {
                    continue;
                }
                if ("TASK".equals(parts[0]) && parts.length >= 5) {
                    String desc = decode(parts[1]);
                    LocalDate due = parts[2].isBlank() ? null : LocalDate.parse(parts[2]);
                    Priority pri = Priority.valueOf(parts[3]);
                    boolean done = Boolean.parseBoolean(parts[4]);
                    tasks.add(new Task(desc, due, pri, done));
                } else if ("EVENT".equals(parts[0]) && parts.length >= 5) {
                    String title = decode(parts[1]);
                    LocalDateTime start = LocalDateTime.parse(parts[2]);
                    LocalDateTime end = LocalDateTime.parse(parts[3]);
                    String desc = decode(parts[4]);
                    eventsByDate.computeIfAbsent(start.toLocalDate(), ignored -> new ArrayList<>())
                            .add(new CalendarEvent(title, start, end, desc));
                }
            }
        } catch (Exception ignored) {
            tasks.clear();
            eventsByDate.clear();
        }
    }

    private String nullSafeDate(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}

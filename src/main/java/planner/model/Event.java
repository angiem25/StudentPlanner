package planner.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a calendar event in the student planner.
 * Events have a title, description, start time, and end time.
 * This is distinct from Tasks - events are for scheduling time blocks.
 */
public class Event {
    private final String id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    /**
     * Creates a new event with a generated unique ID.
     * @param title The event title
     * @param description The event description
     * @param startDateTime The start date and time
     * @param endDateTime The end date and time
     */
    public Event(String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    
    /**
     * Checks if this event overlaps with a given time range.
     * @param start The start of the range
     * @param end The end of the range
     * @return true if the event overlaps with the range
     */
    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        return !this.startDateTime.isAfter(end) && !this.endDateTime.isBefore(start);
    }
    
    /**
     * Checks if this event occurs on a specific date.
     * @param date The date to check
     * @return true if the event occurs on that date
     */
    public boolean occursOn(java.time.LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        return overlapsWith(dayStart, dayEnd);
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id.equals(event.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

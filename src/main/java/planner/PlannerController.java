package planner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import mvc.AbstractController;
import mvc.JFrameView;

public class PlannerController extends AbstractController {
    public PlannerController() {
        setModel(new PlannerModel());
        setView(new PlannerView((PlannerModel) getModel(), this));
        ((JFrameView) getView()).setVisible(true);
    }

    public void addTask(String description, LocalDate dueDate, PlannerModel.Priority priority) {
        ((PlannerModel) getModel()).addTask(description, dueDate, priority);
    }

    public void editTask(int index, String description, LocalDate dueDate, PlannerModel.Priority priority) {
        ((PlannerModel) getModel()).editTask(index, description, dueDate, priority);
    }

    public void setTaskCompleted(int index, boolean completed) {
        ((PlannerModel) getModel()).setTaskCompleted(index, completed);
    }

    public void deleteTask(int index) {
        ((PlannerModel) getModel()).deleteTask(index);
    }

    public void addEvent(String title, LocalDateTime start, LocalDateTime end, String description) {
        ((PlannerModel) getModel()).addEvent(title, start, end, description);
    }

    public void editEvent(LocalDate date, int index, String title, LocalDateTime start, LocalDateTime end, String description) {
        ((PlannerModel) getModel()).editEvent(date, index, title, start, end, description);
    }

    public void deleteEvent(LocalDate date, int index) {
        ((PlannerModel) getModel()).deleteEvent(date, index);
    }

    public void setTimerMinutes(int minutes) {
        ((PlannerModel) getModel()).setTimerMinutes(minutes);
    }

    public void startTimer() {
        ((PlannerModel) getModel()).startTimer();
    }

    public void pauseTimer() {
        ((PlannerModel) getModel()).pauseTimer();
    }

    public void resetTimer() {
        ((PlannerModel) getModel()).resetTimer();
    }

    public void tickTimer() {
        ((PlannerModel) getModel()).tickTimer();
    }
}

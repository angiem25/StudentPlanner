# PROMPT K6: DUE DATE REMINDER POPUPS

## **MANDATORY**: Add daily reminder popups for incomplete tasks beginning 7 days before due date.

## Module of Thought

**Phase**: UI Feature Addition  
**Purpose**: Improve task follow-through by surfacing upcoming deadlines without opening task details  
**Dependencies**: PROMPT-F (UI), PROMPT-D (Task model)

## Feature Requirements

- Show reminders only for tasks that are **not completed**
- Start reminders **7 days before due date**
- Show reminders **daily** (once per task per day)
- Reminder copy should indicate:
  - `Due in X days`
  - `Due tomorrow`
  - `Due today`
- Avoid duplicate popup spam in the same day

---

## Implementation Summary

### File Updated

```
src/main/java/planner/ui/PlannerView.java
```

### New Reminder Components

- `dueReminderTimer` (Swing `Timer`) checks reminders on a schedule
- `shownDailyReminderKeys` tracks already-shown reminders for the current day/task combination
- `checkDueDateReminders()` evaluates reminder eligibility
- `showDueReminderPopup(...)` renders non-modal popup dialog
- `pruneReminderKeysForRemovedTasks(...)` removes stale reminder keys

---

## Reminder Logic

For each task:
1. Skip if completed
2. Compute `reminderStartDate = dueDate - 7 days`
3. Remind when `today` is in `[reminderStartDate, dueDate]`
4. Build unique key: `taskId | dueDateTime | today`
5. If key was already shown, skip
6. Otherwise show popup and record key

This guarantees one reminder per task per day while still allowing reminders on following days.

---

## Popup Behavior

- Non-modal dialog (`JDialog`) so it does not block UI workflow
- Includes task title + due status + due timestamp
- Has `Dismiss` button
- Auto-closes after ~10 seconds
- Runs on Swing event thread via existing UI flow

---

## Edge Cases Covered

- **Task completed after reminder**: future reminders stop
- **Task deleted**: reminder keys cleaned up
- **Task due date changed**: key includes due timestamp, so new schedule is respected
- **No tasks / all completed**: no popups shown

---

## Suggested Test Cases

1. Create incomplete task due in 7 days -> reminder appears today  
2. Keep task incomplete next day -> reminder appears again once  
3. Same day re-check -> no second popup for same task  
4. Mark task complete -> reminders stop  
5. Task due tomorrow -> popup says `Due tomorrow`  
6. Task due today -> popup says `Due today`  

---

## Verification Checklist

- [ ] Daily popup starts at dueDate - 7 days
- [ ] Completed tasks are excluded
- [ ] Only one popup per task per day
- [ ] Popup is dismissible and auto-closes
- [ ] Reminder keys are pruned for removed tasks
- [ ] No compile/lint issues

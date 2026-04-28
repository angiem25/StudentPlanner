# PROMPT K3: STUDY TIMER FEATURE

## **MANDATORY**: Implement countdown timer for study sessions with alerts.

## Module of Thought

**Phase**: UI Feature Addition  
**Purpose**: Add countdown timer for study sessions (SRS Requirement R3.1-R3.6)  
**Dependencies**: PROMPT-F (User Interface)

### Feature Requirements (SRS)

**R3.1**: Graphical countdown timer display  
**R3.2**: Set timer duration in minutes  
**R3.3**: Start button to begin countdown  
**R3.4**: Pause button to pause countdown  
**R3.5**: Reset button to reset timer  
**R3.6**: Audible and visual alert when timer reaches zero

### Timer Behavior

**User Flow**:
```
1. User enters minutes (e.g., 25)
2. User clicks "Start"
3. Timer counts down: 25:00 → 24:59 → ... → 00:00
4. At 00:00: Visual flash + Audio alert + Dialog
5. User can Pause/Resume during countdown
6. User can Reset to initial time at any point
```

**Default Duration**: 25 minutes (Pomodoro technique)

### Implementation

```java
public class TimerPanel extends JPanel implements ActionListener {
    // Timer configuration
    private static final int TIMER_DELAY = 1000; // 1 second updates
    private static final Font DISPLAY_FONT = new Font("Arial", Font.BOLD, 48);
    
    // UI Components
    private JLabel timeDisplay;        // Shows "25:00"
    private JTextField minutesInput;   // Input field for minutes
    private JButton startButton;       // ▶ Start
    private JButton pauseButton;       // ⏸ Pause/Resume
    private JButton resetButton;       // ⏹ Reset
    
    // Timer state
    private Timer swingTimer;          // Swing Timer for countdown
    private int totalSeconds;          // Initial duration in seconds
    private int remainingSeconds;      // Current countdown value
    private boolean isRunning;         // Timer active state
    private boolean isPaused;          // Timer paused state
}
```

### UI Layout

```
┌─────────────────────────────────┐
│         Study Timer             │ ← Title
├─────────────────────────────────┤
│                                 │
│         25:00                   │ ← Time Display (Large)
│                                 │
├─────────────────────────────────┤
│  Minutes: [  25  ]             │ ← Input Field
├─────────────────────────────────┤
│  [▶ Start] [⏸ Pause] [⏹ Reset] │ ← Control Buttons
└─────────────────────────────────┘
```

### Color Coding

| State | Display Color | Button State |
|-------|---------------|--------------|
| **Idle** | Dark Gray | Start: Enabled, Pause: Disabled, Reset: Enabled |
| **Running** | Green | Start: Disabled, Pause: Enabled, Reset: Enabled |
| **Paused** | Orange | Start: Disabled, Pause: Shows "Resume", Reset: Enabled |
| **Completed** | Red (flashing) | All: Enabled |

### Timer Logic

**Start Timer**:
```java
private void startTimer() {
    if (!isRunning) {
        // Parse minutes from input
        int minutes = Integer.parseInt(minutesInput.getText().trim());
        totalSeconds = minutes * 60;
        remainingSeconds = totalSeconds;
    }
    
    isRunning = true;
    isPaused = false;
    swingTimer.start();
    
    // Update UI
    startButton.setEnabled(false);
    pauseButton.setEnabled(true);
    pauseButton.setText("⏸ Pause");
    minutesInput.setEnabled(false);
    timeDisplay.setForeground(new Color(0, 150, 0)); // Green
}
```

**Timer Tick (Every 1 second)**:
```java
private void onTimerTick() {
    if (isRunning && !isPaused && remainingSeconds > 0) {
        remainingSeconds--;
        updateDisplay();
        
        if (remainingSeconds == 0) {
            timerComplete();
        }
    }
}
```

**Pause/Resume**:
```java
private void pauseTimer() {
    if (isRunning) {
        isPaused = !isPaused;
        
        if (isPaused) {
            pauseButton.setText("▶ Resume");
            timeDisplay.setForeground(new Color(200, 150, 0)); // Orange
        } else {
            pauseButton.setText("⏸ Pause");
            timeDisplay.setForeground(new Color(0, 150, 0)); // Green
        }
    }
}
```

**Reset Timer**:
```java
private void resetTimer() {
    swingTimer.stop();
    isRunning = false;
    isPaused = false;
    
    // Reset to input value
    try {
        int minutes = Integer.parseInt(minutesInput.getText().trim());
        totalSeconds = minutes * 60;
    } catch (NumberFormatException e) {
        totalSeconds = 0;
    }
    
    remainingSeconds = totalSeconds;
    updateDisplay();
    
    // Reset UI
    startButton.setEnabled(true);
    pauseButton.setEnabled(false);
    pauseButton.setText("⏸ Pause");
    minutesInput.setEnabled(true);
    timeDisplay.setForeground(Color.DARK_GRAY);
}
```

**Timer Completion**:
```java
private void timerComplete() {
    swingTimer.stop();
    isRunning = false;
    isPaused = false;
    
    // Reset UI
    startButton.setEnabled(true);
    pauseButton.setEnabled(false);
    minutesInput.setEnabled(true);
    timeDisplay.setText("00:00");
    timeDisplay.setForeground(Color.RED);
    
    // Play alert sound
    playAlertSound();
    
    // Show visual notification
    showTimerCompleteNotification();
}
```

### Audio Alert

**Implementation**:
```java
private void playAlertSound() {
    // System beep (always works)
    Toolkit.getDefaultToolkit().beep();
    
    // Try to play custom sound (optional)
    try {
        java.applet.AudioClip clip = java.applet.Applet.newAudioClip(
            getClass().getResource("/sounds/alert.wav")
        );
        if (clip != null) {
            clip.play();
        }
    } catch (Exception e) {
        // Fall back to system beep
    }
}
```

**Note**: Uses system beep as primary alert. Custom sound file is optional.

### Visual Alert

**Flash Display**:
```java
private void showTimerCompleteNotification() {
    // Flash the display 3 times
    Timer flashTimer = new Timer(500, new ActionListener() {
        private int count = 0;
        private boolean on = true;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (count >= 6) { // 3 flashes
                ((Timer) e.getSource()).stop();
                timeDisplay.setForeground(Color.RED);
                return;
            }
            
            timeDisplay.setForeground(on ? Color.RED : Color.DARK_GRAY);
            on = !on;
            count++;
        }
    });
    flashTimer.start();
    
    // Show completion dialog
    SwingUtilities.invokeLater(() -> {
        JOptionPane.showMessageDialog(this,
            "Timer completed!\nYour study session is finished.",
            "Time's Up!",
            JOptionPane.INFORMATION_MESSAGE);
    });
}
```

### Integration with PlannerView

```java
// In PlannerView.java
import planner.ui.timer.TimerPanel;

// Add as new tab
TimerPanel timerPanel = new TimerPanel();
tabbedPane.addTab("Timer", timerPanel);
```

**Tab Order**:
```
1. Student Profile
2. Courses
3. Tasks
4. Calendar
5. Timer  ← New tab
```

### Edge Cases Handled

1. **Invalid Input**: Non-numeric minutes → Show error dialog
2. **Zero/Negative Minutes**: Show warning dialog
3. **Very Large Input**: Cap at reasonable limit (e.g., 999 minutes)
4. **App Close While Running**: Timer stops (no background persistence)
5. **Multiple Clicks**: Button state management prevents conflicts
6. **Pause at 00:00**: No effect (timer already stopped)
7. **Reset While Paused**: Resets to initial state

### File Location

```
src/main/java/planner/ui/timer/
└── TimerPanel.java
```

### Expected Outcome

After this prompt, you should have:
- [ ] TimerPanel class extending JPanel
- [ ] Large time display (MM:SS format)
- [ ] Minutes input field
- [ ] Start/Pause/Reset buttons
- [ ] Countdown logic (1-second updates)
- [ ] Pause/Resume functionality
- [ ] Reset to initial time
- [ ] Visual flash alert on completion
- [ ] Audio alert (system beep)
- [ ] Completion dialog
- [ ] State-based button enabling
- [ ] Color-coded display states
- [ ] Integration in PlannerView tabs

### Verification Halt

**Test Scenarios**:

**Basic Countdown**:
```
1. Enter 1 minute
2. Click Start
3. Verify countdown: 01:00 → 00:59 → ... → 00:00
4. Verify alert plays and dialog appears
```

**Pause/Resume**:
```
1. Start 5 minute timer
2. At 04:30, click Pause
3. Verify display shows orange, button shows "Resume"
4. Click Resume
5. Verify countdown continues from 04:30
6. Verify display shows green, button shows "Pause"
```

**Reset**:
```
1. Start timer, let it run to 03:45
2. Click Reset
3. Verify display resets to initial value
4. Verify Start button enabled, Pause disabled
5. Can start new countdown immediately
```

**Invalid Input**:
```
1. Enter "abc" in minutes field
2. Click Start
3. Verify error dialog: "Please enter a valid number"
4. Timer doesn't start
```

**Completion**:
```
1. Start 1 second timer (for quick test)
2. Wait for 00:00
3. Verify display flashes red/dark gray 3 times
4. Verify system beep plays
5. Verify dialog: "Timer completed!"
6. Verify buttons reset to idle state
```

### Design Decisions

**Why Swing Timer (not Thread)?**
- Runs on EDT (Event Dispatch Thread)
- Safe UI updates without invokeLater
- Easy to start/stop/pause
- Integrates with Swing lifecycle

**Why no persistence?**
- Timer is ephemeral (study session)
- SRS doesn't specify resume after restart
- Simpler implementation

**Why no background running?**
- Desktop app focus
- User typically watches timer
- Avoids complexity of background threads

**Why 25-minute default?**
- Pomodoro Technique standard
- Popular study method
- User can change to any value

**Why system beep over custom sound?**
- Always available (no file dependencies)
- Cross-platform compatible
- No missing file errors
- Optional custom sound can be added

### Future Enhancements

- **Sound Selection**: Choose alert sound
- ~~**Preset Durations**: 25min, 45min, 60min buttons~~ ✅ Implemented as 30/60/90 in PROMPT-K5
- **Multiple Timers**: Track different activities
- **History Log**: Track completed sessions
- **Auto-Start Break**: Auto-start 5-min break after session
- **Background Mode**: Minimize to system tray
- **Fullscreen Alert**: Block screen until acknowledged

---

**Next**: PROMPT-L: Calendar Event Dialog  
**Prev**: PROMPT-K2: Calendar Day/Week/Month Views

**Implementation Date**: 2026-04-21  
**Complexity**: Low-Medium (self-contained component)  
**Lines Added**: ~250

package planner.ui.timer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Timer panel for study sessions with countdown functionality.
 * Provides start, pause, and reset functionality with visual and audio alerts.
 */
public class TimerPanel extends JPanel implements ActionListener {
    private static final int TIMER_DELAY = 1000; // 1 second
    private static final Font DISPLAY_FONT = new Font("Arial", Font.BOLD, 48);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    
    private JLabel timeDisplay;
    private JTextField minutesInput;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private Timer swingTimer;
    
    private int totalSeconds;
    private int remainingSeconds;
    private boolean isRunning;
    private boolean isPaused;
    
    /**
     * Creates a new TimerPanel with default settings.
     */
    public TimerPanel() {
        this.totalSeconds = 0;
        this.remainingSeconds = 0;
        this.isRunning = false;
        this.isPaused = false;
        
        initializeUI();
        setupTimer();
    }
    
    /**
     * Initializes the timer user interface.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Study Timer", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Center panel with display and input
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        // Time display
        timeDisplay = new JLabel("00:00", JLabel.CENTER);
        timeDisplay.setFont(DISPLAY_FONT);
        timeDisplay.setForeground(Color.DARK_GRAY);
        centerPanel.add(timeDisplay, gbc);
        
        // Input panel
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Minutes:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        minutesInput = new JTextField("25", 5); // Default 25 minutes (Pomodoro)
        minutesInput.setFont(LABEL_FONT);
        minutesInput.setHorizontalAlignment(JTextField.CENTER);
        centerPanel.add(minutesInput, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        startButton = new JButton("▶ Start");
        startButton.setFont(LABEL_FONT);
        startButton.addActionListener(this);
        startButton.setBackground(new Color(200, 255, 200));
        
        pauseButton = new JButton("⏸ Pause");
        pauseButton.setFont(LABEL_FONT);
        pauseButton.addActionListener(this);
        pauseButton.setEnabled(false);
        pauseButton.setBackground(new Color(255, 255, 200));
        
        resetButton = new JButton("⏹ Reset");
        resetButton.setFont(LABEL_FONT);
        resetButton.addActionListener(this);
        resetButton.setBackground(new Color(255, 200, 200));
        
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets up the Swing timer for countdown.
     */
    private void setupTimer() {
        swingTimer = new Timer(TIMER_DELAY, e -> {
            if (isRunning && !isPaused && remainingSeconds > 0) {
                remainingSeconds--;
                updateDisplay();
                
                if (remainingSeconds == 0) {
                    timerComplete();
                }
            }
        });
    }
    
    /**
     * Handles button actions.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == startButton) {
            startTimer();
        } else if (source == pauseButton) {
            pauseTimer();
        } else if (source == resetButton) {
            resetTimer();
        }
    }
    
    /**
     * Starts the timer countdown.
     */
    private void startTimer() {
        if (!isRunning) {
            // Get minutes from input
            try {
                int minutes = Integer.parseInt(minutesInput.getText().trim());
                if (minutes <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a positive number of minutes.", 
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                totalSeconds = minutes * 60;
                remainingSeconds = totalSeconds;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid number of minutes.", 
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        isRunning = true;
        isPaused = false;
        swingTimer.start();
        
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        minutesInput.setEnabled(false);
        
        timeDisplay.setForeground(new Color(0, 150, 0)); // Green when running
    }
    
    /**
     * Pauses the timer countdown.
     */
    private void pauseTimer() {
        if (isRunning) {
            isPaused = !isPaused;
            
            if (isPaused) {
                pauseButton.setText("▶ Resume");
                timeDisplay.setForeground(new Color(200, 150, 0)); // Orange when paused
            } else {
                pauseButton.setText("⏸ Pause");
                timeDisplay.setForeground(new Color(0, 150, 0)); // Green when running
            }
        }
    }
    
    /**
     * Resets the timer to initial state.
     */
    private void resetTimer() {
        swingTimer.stop();
        isRunning = false;
        isPaused = false;
        
        // Reset to input value or 0 if input is invalid
        try {
            int minutes = Integer.parseInt(minutesInput.getText().trim());
            totalSeconds = minutes * 60;
        } catch (NumberFormatException ex) {
            totalSeconds = 0;
        }
        
        remainingSeconds = totalSeconds;
        updateDisplay();
        
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("⏸ Pause");
        minutesInput.setEnabled(true);
        
        timeDisplay.setForeground(Color.DARK_GRAY);
    }
    
    /**
     * Updates the time display label.
     */
    private void updateDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timeDisplay.setText(String.format("%02d:%02d", minutes, seconds));
    }
    
    /**
     * Called when timer reaches zero.
     */
    private void timerComplete() {
        swingTimer.stop();
        isRunning = false;
        isPaused = false;
        
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("⏸ Pause");
        minutesInput.setEnabled(true);
        
        timeDisplay.setText("00:00");
        timeDisplay.setForeground(Color.RED);
        
        // Play alert sound
        playAlertSound();
        
        // Show visual notification
        showTimerCompleteNotification();
    }
    
    /**
     * Plays an audible alert when timer completes.
     */
    private void playAlertSound() {
        try {
            // Use system beep as fallback
            Toolkit.getDefaultToolkit().beep();
            
            // Try to play a more pleasant alert sound
            java.applet.AudioClip clip = java.applet.Applet.newAudioClip(
                getClass().getResource("/sounds/alert.wav")
            );
            if (clip != null) {
                clip.play();
            }
        } catch (Exception e) {
            // If sound file not found, system beep already played above
            System.out.println("Timer completed - Alert sound played");
        }
    }
    
    /**
     * Shows a visual notification when timer completes.
     */
    private void showTimerCompleteNotification() {
        // Flash the display
        Timer flashTimer = new Timer(500, new ActionListener() {
            private int count = 0;
            private boolean on = true;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count >= 6) { // Flash 3 times
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
        
        // Show dialog
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Timer completed!\nYour study session is finished.",
                "Time's Up!",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * Gets the remaining time in seconds.
     * @return Remaining seconds
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
    }
    
    /**
     * Checks if the timer is currently running.
     * @return true if running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Checks if the timer is currently paused.
     * @return true if paused
     */
    public boolean isPaused() {
        return isPaused;
    }
}

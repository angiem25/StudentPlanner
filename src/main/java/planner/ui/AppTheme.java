package planner.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Application-wide light/dark theme. Uses FlatLaf for standard controls;
 * custom calendar and timer colors are exposed for manually painted areas.
 */
public final class AppTheme {

    private static final String PREF_NODE = "planner.ui";
    private static final String PREF_DARK_MODE = "darkMode";

    private static volatile boolean dark;

    private AppTheme() {
    }

    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean value) {
        dark = value;
    }

    /**
     * Loads the saved light/dark preference (defaults to light).
     */
    public static void loadPersistedTheme() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        dark = prefs.getBoolean(PREF_DARK_MODE, false);
    }

    /**
     * Persists the current light/dark preference for the next session.
     */
    public static void persistTheme() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.putBoolean(PREF_DARK_MODE, dark);
    }

    /**
     * Installs Flat Light or Flat Dark look and feel based on {@link #isDark()}.
     */
    public static void installLookAndFeel() throws UnsupportedLookAndFeelException {
        if (dark) {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } else {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatLightLaf());
        }
    }

    /**
     * Re-applies the current look and feel to the frame and its descendants.
     */
    public static void applyFrame(JFrame frame) {
        if (frame == null) {
            return;
        }
        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }

    // --- Calendar (custom colors) ---

    public static Color calDayHeaderBg() {
        return dark ? new Color(55, 58, 64) : new Color(220, 220, 220);
    }

    public static Color calDayHeaderFg() {
        return dark ? new Color(220, 222, 228) : Color.BLACK;
    }

    public static Color calDayHeaderBorder() {
        return dark ? new Color(75, 78, 85) : new Color(160, 160, 160);
    }

    public static Color calCellBorder() {
        return dark ? new Color(70, 73, 80) : new Color(210, 210, 210);
    }

    public static Color calCellBg() {
        return dark ? new Color(43, 46, 52) : Color.WHITE;
    }

    public static Color calCellFg() {
        return dark ? new Color(230, 232, 238) : Color.BLACK;
    }

    public static Color calEventPanelBg() {
        return calCellBg();
    }

    public static Color calTodayBg() {
        return dark ? new Color(35, 55, 78) : new Color(230, 245, 255);
    }

    public static Color calTodayBorder() {
        return dark ? new Color(80, 140, 200) : new Color(100, 150, 200);
    }

    public static Color taskHighBg() {
        return dark ? new Color(90, 40, 45) : new Color(255, 200, 200);
    }

    public static Color taskHighFg() {
        return dark ? new Color(255, 140, 145) : Color.RED;
    }

    public static Color taskMediumBg() {
        return dark ? new Color(85, 70, 35) : new Color(255, 240, 200);
    }

    public static Color taskMediumFg() {
        return dark ? new Color(255, 210, 120) : new Color(200, 150, 0);
    }

    public static Color taskLowBg() {
        return dark ? new Color(40, 70, 45) : new Color(220, 255, 220);
    }

    public static Color taskLowFg() {
        return dark ? new Color(140, 220, 150) : new Color(0, 150, 0);
    }

    public static Color taskCompletedBg() {
        return dark ? new Color(58, 60, 65) : Color.LIGHT_GRAY;
    }

    public static Color taskCompletedFg() {
        return dark ? new Color(150, 152, 160) : Color.GRAY;
    }

    public static Color timelineBg() {
        return calCellBg();
    }

    public static Color hourDivider() {
        return calCellBorder();
    }

    public static Color weekColumnHeaderBg() {
        return dark ? new Color(50, 53, 58) : new Color(240, 240, 240);
    }

    public static Color weekColumnHeaderFg() {
        return dark ? new Color(220, 222, 228) : Color.BLACK;
    }

    public static Color weekColumnHeaderBorder() {
        return dark ? new Color(85, 88, 95) : Color.GRAY;
    }

    public static Color weekTodayHeaderBg() {
        return dark ? new Color(35, 55, 78) : new Color(200, 230, 255);
    }

    public static Color weekTodayHeaderFg() {
        return dark ? new Color(130, 190, 255) : new Color(0, 100, 200);
    }

    public static Color timeMuted() {
        return dark ? new Color(150, 152, 162) : Color.GRAY;
    }

    public static Color eventBarBg() {
        return dark ? new Color(70, 110, 175) : new Color(100, 150, 220);
    }

    public static Color eventBarFg() {
        return Color.WHITE;
    }

    // --- Timer ---

    public static Color timerIdle() {
        return dark ? new Color(175, 178, 188) : Color.DARK_GRAY;
    }

    public static Color timerRunning() {
        return new Color(0, 180, 90);
    }

    public static Color timerPaused() {
        return new Color(230, 170, 60);
    }

    public static Color timerAlert() {
        return new Color(255, 80, 80);
    }

    public static Color timerStartBg() {
        return dark ? new Color(35, 75, 42) : new Color(200, 255, 200);
    }

    public static Color timerPauseBg() {
        return dark ? new Color(85, 78, 30) : new Color(255, 255, 200);
    }

    public static Color timerResetBg() {
        return dark ? new Color(85, 40, 40) : new Color(255, 200, 200);
    }
}

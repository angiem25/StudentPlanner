package planner.ui.task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Modal dialog with column-style hour / minute / AM-PM lists, similar to the iOS Clock time picker.
 */
public final class TimeWheelDialog extends JDialog {

    private static final DateTimeFormatter PREVIEW = DateTimeFormatter.ofPattern("h:mm a").withLocale(Locale.US);

    private final JList<String> hourList;
    private final JList<String> minuteList;
    private final JList<String> amPmList;
    private boolean confirmed;
    private LocalTime result;

    private TimeWheelDialog(Window owner, LocalTime initial) {
        super(owner, "Choose time", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        int hour12;
        int minute = initial.getMinute();
        boolean pm = initial.getHour() >= 12;
        int h = initial.getHour() % 12;
        if (h == 0) {
            hour12 = 12;
        } else {
            hour12 = h;
        }

        DefaultListModel<String> hours = new DefaultListModel<>();
        for (int i = 1; i <= 12; i++) {
            hours.addElement(String.valueOf(i));
        }
        hourList = new JList<>(hours);
        styleWheelList(hourList);
        hourList.setSelectedIndex(hour12 - 1);

        DefaultListModel<String> minutes = new DefaultListModel<>();
        for (int i = 0; i < 60; i++) {
            minutes.addElement(String.format("%02d", i));
        }
        minuteList = new JList<>(minutes);
        styleWheelList(minuteList);
        minuteList.setSelectedIndex(minute);

        DefaultListModel<String> meridiem = new DefaultListModel<>();
        meridiem.addElement("AM");
        meridiem.addElement("PM");
        amPmList = new JList<>(meridiem);
        styleWheelList(amPmList);
        amPmList.setSelectedIndex(pm ? 1 : 0);

        JPanel columns = new JPanel(new GridLayout(1, 3, 10, 0));
        columns.add(wrapColumn("Hour", hourList));
        columns.add(wrapColumn("Minute", minuteList));
        columns.add(wrapColumn("", amPmList));

        JLabel preview = new JLabel(PREVIEW.format(LocalTime.of(initial.getHour(), initial.getMinute())), SwingConstants.CENTER);
        preview.setFont(preview.getFont().deriveFont(Font.BOLD, 18f));
        preview.setBorder(new EmptyBorder(0, 0, 8, 0));

        Runnable updatePreview = () -> {
            LocalTime t = readTimeFromLists();
            if (t != null) {
                preview.setText(PREVIEW.format(t));
            }
        };
        hourList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview.run();
            }
        });
        minuteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview.run();
            }
        });
        amPmList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview.run();
            }
        });

        attachWheelScroll(hourList);
        attachWheelScroll(minuteList);
        attachWheelScroll(amPmList);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            LocalTime t = readTimeFromLists();
            if (t != null) {
                result = t;
                confirmed = true;
            }
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 8, 0));
        buttons.add(cancel);
        buttons.add(ok);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(preview, BorderLayout.NORTH);
        root.add(columns, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);

        SwingUtilities.invokeLater(() -> {
            ensureVisibleSelection(hourList);
            ensureVisibleSelection(minuteList);
            ensureVisibleSelection(amPmList);
        });
    }

    private static void styleWheelList(JList<String> list) {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(32);
        list.setVisibleRowCount(5);
        list.setFont(list.getFont().deriveFont(Font.PLAIN, 16f));
        list.setLayoutOrientation(JList.VERTICAL);
    }

    private static JPanel wrapColumn(String title, JList<String> list) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        if (title != null && !title.isEmpty()) {
            JLabel lab = new JLabel(title, SwingConstants.CENTER);
            lab.setFont(lab.getFont().deriveFont(Font.PLAIN, 11f));
            p.add(lab, BorderLayout.NORTH);
        }
        JScrollPane sp = new JScrollPane(list);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(new Dimension(92, 32 * 5 + 8));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void ensureVisibleSelection(JList<?> list) {
        int i = list.getSelectedIndex();
        if (i >= 0) {
            list.ensureIndexIsVisible(i);
        }
    }

    private static void attachWheelScroll(JList<String> list) {
        list.addMouseWheelListener(e -> {
            int n = e.getWheelRotation();
            int size = list.getModel().getSize();
            if (size == 0) {
                return;
            }
            int i = list.getSelectedIndex();
            if (i < 0) {
                i = 0;
            }
            i = Math.max(0, Math.min(size - 1, i + (n > 0 ? 1 : -1)));
            list.setSelectedIndex(i);
            list.ensureIndexIsVisible(i);
        });
    }

    private LocalTime readTimeFromLists() {
        int hi = hourList.getSelectedIndex();
        int mi = minuteList.getSelectedIndex();
        int ai = amPmList.getSelectedIndex();
        if (hi < 0 || mi < 0 || ai < 0) {
            return null;
        }
        int hour12 = hi + 1;
        int minute = mi;
        boolean pm = ai == 1;
        int h24;
        if (pm) {
            h24 = (hour12 == 12) ? 12 : hour12 + 12;
        } else {
            h24 = (hour12 == 12) ? 0 : hour12;
        }
        return LocalTime.of(h24, minute);
    }

    /**
     * Shows the picker and returns the chosen time, or null if cancelled.
     */
    public static LocalTime showDialog(Window owner, LocalTime initial) {
        LocalTime start = initial != null ? initial : LocalTime.NOON;
        TimeWheelDialog d = new TimeWheelDialog(owner, start);
        d.setVisible(true);
        return d.confirmed ? d.result : null;
    }
}

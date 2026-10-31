package org.app.gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class InputForm extends JPanel {
    private final JTextField[] fields;
    public InputForm(String... labels) {
        super(new GridLayout(0, 2, 10, 8));
        fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            add(new JLabel(labels[i]));
            fields[i] = new JTextField(24);
            add(fields[i]);
        }
    }
    public String text(int index) { return fields[index].getText().trim(); }
    public void preset(int index, String value) { fields[index].setText(value); }
    public int number(int index) { return Integer.parseInt(text(index)); }
    public long money(int index) { return Long.parseLong(text(index)); }
    public LocalDateTime time(int index) {
        return LocalDateTime.parse(text(index), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                .withResolverStyle(ResolverStyle.STRICT));
    }
    public boolean show(Component parent, String title) {
        return JOptionPane.showConfirmDialog(parent, this, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
}


package pl.janek49.iniektor.agent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;

public class AgentGui extends JFrame {

    private JTextPane textPane;
    private StyledDocument doc;
    private Style style;
    private JScrollPane scrollPane;

    private boolean autoScroll = true;

    public AgentGui() {
        super("Iniektor v0.1 - GUI");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        doc = textPane.getStyledDocument();
        style = textPane.addStyle("Style", null);
        StyleConstants.setFontFamily(style, "Consolas");
        textPane.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                textPane.setEditable(true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                textPane.setEditable(false);
            }
        });
        doc.addDocumentListener(new ScrollingDocumentListener());

        scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JCheckBox autoScrollBox = new JCheckBox("Autoscroll");
        autoScrollBox.addActionListener(e -> autoScroll = autoScrollBox.isSelected());

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }


    public void appendLine(String string, Level color) {
        try {
            StyleConstants.setForeground(style, color.colorCode);
            doc.insertString(doc.getLength(), string + "\n", style);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public enum Level {
        DEFAULT(Color.black), ERROR(Color.red), WARNING(Color.orange), DEBUG(Color.blue);

        public Color colorCode;

        Level(Color code) {
            this.colorCode = code;
        }
    }

    class ScrollingDocumentListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            maybeScrollToBottom();
        }

        public void insertUpdate(DocumentEvent e) {
            maybeScrollToBottom();
        }

        public void removeUpdate(DocumentEvent e) {
            maybeScrollToBottom();
        }

        private void maybeScrollToBottom() {
            if (autoScroll) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                scrollToBottom(textPane);
                            }
                        });
                    }
                });
            }
        }
    }

    public static boolean isScrollBarFullyExtended(JScrollBar vScrollBar) {
        BoundedRangeModel model = vScrollBar.getModel();
        return (model.getExtent() + model.getValue()) == model.getMaximum();
    }

    public static void scrollToBottom(JComponent component) {
        Rectangle visibleRect = component.getVisibleRect();
        visibleRect.y = component.getHeight() - visibleRect.height;
        component.scrollRectToVisible(visibleRect);
    }

    public static void SetVisible(Object instance, boolean value) {
        try {
            instance.getClass().getMethod("setVisible", boolean.class).invoke(instance, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static Class enumClass = null;
    private static Method appendMethod = null;

    public static void AppendText(Object instance, String text, String enumLevel) {
        try {
            if (enumClass == null)
                enumClass = Class.forName(Level.class.getName(), true, instance.getClass().getClassLoader());
            if (appendMethod == null)
                appendMethod = instance.getClass().getDeclaredMethod("appendLine", String.class, enumClass);

            Enum en = Enum.valueOf(enumClass, enumLevel);
            appendMethod.invoke(instance, text, en);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

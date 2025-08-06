import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EncodingSimulator extends JFrame {
    private JTextField inputBinaryField;
    private JComboBox<String> encodingSelector;
    private SignalPanel signalPanel;

    private String binaryInput = "";
    private String chosenEncoding = "";

    public EncodingSimulator() {
        setTitle("Digital Signal Encoding Simulator");
        setSize(1100, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Binary Sequence:"));
        inputBinaryField = new JTextField(20);
        controlPanel.add(inputBinaryField);

        String[] encodingSchemes = {
            "Unipolar NRZ", "Unipolar RZ",
            "Polar NRZ", "Polar RZ",
            "NRZ-I", "AMI (Bipolar)",
            "Manchester", "Differential Manchester"
        };

        encodingSelector = new JComboBox<>(encodingSchemes);
        controlPanel.add(encodingSelector);

        JButton visualizeButton = new JButton("Visualize");
        controlPanel.add(visualizeButton);

        visualizeButton.addActionListener(e -> {
            binaryInput = inputBinaryField.getText().trim();
            chosenEncoding = (String) encodingSelector.getSelectedItem();
            signalPanel.renderSignal(binaryInput, chosenEncoding);
        });

        add(controlPanel, BorderLayout.NORTH);

        signalPanel = new SignalPanel();
        add(signalPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EncodingSimulator().setVisible(true);
        });
    }
}

class SignalPanel extends JPanel {
    private String bits = "";
    private String method = "";
    private int index = 0;

    public void renderSignal(String bits, String method) {
        this.bits = bits;
        this.method = method;
        this.index = 0;

        Timer animationTimer = new Timer(450, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (index <= bits.length()) {
                    repaint();
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        animationTimer.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        int centerY = getHeight() / 2;
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, centerY, getWidth(), centerY);

        if (bits == null || method == null || index == 0) return;

        int startX = 50;
        int width = 45;
        int top = centerY - 60;
        int bottom = centerY + 60;
        int middle = centerY;

        int previousLevel = middle;
        boolean oneToggle = true;
        boolean zeroToggle = true;

        for (int i = 0; i < index && i < bits.length(); i++) {
            char bit = bits.charAt(i);
            int endX = startX + width;

            int hi = top, lo = bottom, zero = middle, level = zero;

            switch (method) {
                case "Unipolar NRZ":
                    g2.setColor(new Color(66, 135, 245));
                    level = (bit == '1') ? hi : zero;
                    drawLevel(g2, startX, endX, level, previousLevel);
                    previousLevel = level;
                    break;

                case "Unipolar RZ":
                    g2.setColor(new Color(123, 31, 162));
                    if (bit == '1') {
                        g2.drawLine(startX, hi, startX + width / 2, hi);
                        g2.drawLine(startX + width / 2, hi, startX + width / 2, zero);
                        g2.drawLine(startX + width / 2, zero, endX, zero);
                        previousLevel = zero;
                    } else {
                        g2.drawLine(startX, zero, endX, zero);
                    }
                    break;

                case "Polar NRZ":
                    g2.setColor(Color.RED);
                    level = (bit == '1') ? hi : lo;
                    drawLevel(g2, startX, endX, level, previousLevel);
                    previousLevel = level;
                    break;

                case "Polar RZ":
                    g2.setColor(new Color(255, 153, 0));
                    level = (bit == '1') ? hi : lo;
                    g2.drawLine(startX, level, startX + width / 2, level);
                    g2.drawLine(startX + width / 2, level, startX + width / 2, middle);
                    g2.drawLine(startX + width / 2, middle, endX, middle);
                    previousLevel = middle;
                    break;

                case "NRZ-I":
                    g2.setColor(Color.CYAN);
                    if (bit == '1') {
                        level = (previousLevel == hi) ? lo : hi;
                    } else {
                        level = previousLevel;
                    }
                    drawLevel(g2, startX, endX, level, previousLevel);
                    previousLevel = level;
                    break;

                case "AMI (Bipolar)":
                    g2.setColor(new Color(0, 153, 76));
                    if (bit == '1') {
                        level = oneToggle ? hi : lo;
                        oneToggle = !oneToggle;
                    } else {
                        level = zero;
                    }
                    drawLevel(g2, startX, endX, level, previousLevel);
                    previousLevel = level;
                    break;

                case "Pseudoternary":
                    g2.setColor(Color.BLACK);
                    if (bit == '0') {
                        level = zeroToggle ? hi : lo;
                        zeroToggle = !zeroToggle;
                    } else {
                        level = zero;
                    }
                    drawLevel(g2, startX, endX, level, previousLevel);
                    previousLevel = level;
                    break;

                case "Manchester":
                    g2.setColor(new Color(255, 105, 180));
                    if (bit == '1') {
                        g2.drawLine(startX, lo, startX + width / 2, lo);
                        g2.drawLine(startX + width / 2, lo, startX + width / 2, hi);
                        g2.drawLine(startX + width / 2, hi, endX, hi);
                        previousLevel = hi;
                    } else {
                        g2.drawLine(startX, hi, startX + width / 2, hi);
                        g2.drawLine(startX + width / 2, hi, startX + width / 2, lo);
                        g2.drawLine(startX + width / 2, lo, endX, lo);
                        previousLevel = lo;
                    }
                    break;

                case "Differential Manchester":
                    g2.setColor(new Color(88, 24, 69));
                    if (i == 0) previousLevel = hi;
                    if (bit == '0') {
                        int flipLevel = (previousLevel == hi) ? lo : hi;
                        g2.drawLine(startX, previousLevel, startX, flipLevel);
                        g2.drawLine(startX, flipLevel, startX + width / 2, flipLevel);
                        g2.drawLine(startX + width / 2, flipLevel, startX + width / 2,
                                (flipLevel == hi) ? lo : hi);
                        previousLevel = (flipLevel == hi) ? lo : hi;
                        g2.drawLine(startX + width / 2, previousLevel, endX, previousLevel);
                    } else {
                        g2.drawLine(startX, previousLevel, startX + width / 2, previousLevel);
                        g2.drawLine(startX + width / 2, previousLevel, startX + width / 2,
                                (previousLevel == hi) ? lo : hi);
                        previousLevel = (previousLevel == hi) ? lo : hi;
                        g2.drawLine(startX + width / 2, previousLevel, endX, previousLevel);
                    }
                    break;
            }
            startX += width;
        }
    }

    private void drawLevel(Graphics2D g2, int x1, int x2, int level, int prev) {
        if (prev != level) g2.drawLine(x1, prev, x1, level);
        g2.drawLine(x1, level, x2, level);
    }
}

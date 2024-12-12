package custom_component;

import javax.swing.*;
import java.awt.*;

public class StyledButton extends JButton {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private Color baseColor;

    public StyledButton(String text, int yPosition) {
        this(text, yPosition, Color.YELLOW);
    }

    public StyledButton(String text, Color baseColor) {
        this(text, 0, baseColor);
    }

    private StyledButton(String text, int yPosition, Color baseColor) {
        setText(text);
        this.baseColor = baseColor;

        setBounds(100, yPosition, BUTTON_WIDTH, BUTTON_HEIGHT);
        setBackground(baseColor);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setFont(new Font("Arial", Font.BOLD, 14));
        setupHoverEffect();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground());
        FontMetrics fm = g.getFontMetrics();
        drawCenteredText(g, fm);
    }

    private void drawCenteredText(Graphics g, FontMetrics fm) {
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(getText(), x, y);
    }

    private void setupHoverEffect() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(255, 255, 150));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(baseColor);
            }
        });
    }
    
    public void setAsPlayButton() {
        setBackground(Color.YELLOW);
        baseColor = Color.YELLOW;
    }
}
package Client.Components;

import javax.swing.*;
import java.awt.*;
// Questa classe costruisce il logo di Word Quizzle
public class Logo extends JComponent {
    private int x;
    private int y;

    public Logo(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Faccio il cast a un oggetto 2D
        Graphics2D g2 = (Graphics2D) g;
        // Disegno il logo, era 50
        g2.setFont(new Font("LucidaSans", Font.BOLD,
                calculateXPosition(50)));
        g2.drawString("Word",calculateXPosition(50),calculateYPosition(85));
        g2.drawRect(
                calculateXPosition(53),
                calculateYPosition(90),
                calculateXPosition(40),
                calculateYPosition(40)
        );
        g2.setFont(new Font("LucidaSans", Font.ITALIC, calculateXPosition(45)));
        g2.drawString("Quizzle",calculateXPosition(95),calculateYPosition(125));
    }

    private int calculateXPosition(int old){
        return (int) Math.ceil(old * ( Math.ceil(x / 310.0)));
    }

    private int calculateYPosition(int old){
        return (int) Math.floor(old * ( Math.ceil(y / 420.0)));
    }

}

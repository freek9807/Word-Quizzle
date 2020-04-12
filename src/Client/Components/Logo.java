package Client.Components;

import javax.swing.*;
import java.awt.*;
/**
 * Questa classe costruisce il logo di Word Quizzle
 *
 * @author Federico Pennino
 */
public class Logo extends JComponent {
    /**
     * Le coordinate
     */
    private int x;
    private int y;
    /**
     * Il costruttore
     * @param x Coordinata lungo x
     * @param y Coordinata lungo y
     */
    public Logo(int x, int y){
        this.x = x;
        this.y = y;
    }
    /**
     * Disegno il componente
     * @param g Descrive l'oggetto da disegnare
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Faccio il cast a un oggetto 2D
        Graphics2D g2 = (Graphics2D) g;
        // Imposto il font e la dimensione del font
        g2.setFont(new Font("LucidaSans", Font.BOLD,
                calculateXPosition(50)));
        // Disegno la stringa e la posiziono
        g2.drawString("Word",calculateXPosition(50),calculateYPosition(85));
        // Disegno un rettangolo giusto per estetica
        g2.drawRect(
                calculateXPosition(53),
                calculateYPosition(90),
                calculateXPosition(40),
                calculateYPosition(40)
        );
        // Imposto un nuovo font (non grassetto)
        g2.setFont(new Font("LucidaSans", Font.ITALIC, calculateXPosition(45)));
        // Disegno la Stringa
        g2.drawString("Quizzle",calculateXPosition(95),calculateYPosition(125));
    }
    /**
     * Cerco di mantenere un minimo di proporzioni lungo l'asse X
     * @param old Dimensione precedente alla messa in scala
     * @return Nuova dimensione dopo la messa in scala
     */
    private int calculateXPosition(int old){
        return (int) Math.ceil(old * ( Math.ceil(x / 310.0)));
    }
    /**
     * Cerco di mantenere un minimo di proporzioni lungo l'asse Y
     * @param old Dimensione precedente alla messa in scala
     * @return Nuova dimensione dopo la messa in scala
     */
    private int calculateYPosition(int old){
        return (int) Math.floor(old * ( Math.ceil(y / 420.0)));
    }

}

package Client.Components;

import javax.swing.*;
import java.awt.*;

public class Logo extends JComponent {
    private final int rows;
    private final int columns;
    private boolean[][] isSelected; // selection state of cells

    public Logo(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        isSelected = new boolean[rows][columns];
    }

    private int getRowSize() {return getHeight() / rows;}
    private int getColSize() {return getWidth() / columns;}

    private int[] resolveIndices(int x, int y) {
        int i = y / getRowSize();
        int j = x / getColSize();
        return new int[] {i, j};
    }

    public void selectCell(int x, int y) {
        int[] indices = resolveIndices(x, y);
        int i = indices[0];
        int j = indices[1];
        isSelected[i][j] = true;
        repaint();
    }
    public void unselectCell(int x, int y) {
        int[] indices = resolveIndices(x, y);
        int i = indices[0];
        int j = indices[1];
        isSelected[i][j] = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();

        g2.setFont(new Font("LucidaSans", Font.BOLD, 50));
        g2.drawString("Word",50,85);
        g2.drawRect(53,90,40,40);
        g2.setFont(new Font("LucidaSans", Font.ITALIC, 45));
        g2.drawString("Quizzle",95,125);
        // draw rows
        /*int rowSize = getRowSize();
        for (int i = 0; i < columns; i++) {
            int yOffset = i * rowSize;
            g2.drawLine(0, yOffset, width, yOffset);
        }

        // draw lines
        int colSize = getColSize();
        for (int j = 0; j < rows; j++) {
            int xOffset = j * colSize;
            g2.drawLine(xOffset, 0, xOffset, height);
        }

        // fill selected cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (isSelected[i][j]) {
                    Color oldColor = g2.getColor();
                    g2.setColor(Color.BLUE);
                    int x = j * colSize;
                    int y = i * rowSize;
                    int w = colSize;
                    int h = rowSize;
                    g2.fillRect(x, y, w, h);
                    g2.setColor(oldColor);
                }
            }
        } */
    }
}

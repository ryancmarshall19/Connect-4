package com.ryan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

    ControlPanel controlPanel;

    int animateRowCo;
    int animateRow;
    int animateCol = -1;

    Set<Integer> xPositions = new HashSet<>();
    Set<Integer> oPositions = new HashSet<>();

    Set<Integer> winCoords = new HashSet<>();

    CntF game;

    Timer animationTimer = new Timer(10,this);

    double x;
    double y = - 150;
    double vy = 1;
    double ay = 0.5;

    Color x_colour;
    Color o_colour;
    Color board_colour;

    RoundRectangle2D.Double background_rectangle = new RoundRectangle2D.Double(60, 120, 930, 700, 30, 30);

    GamePanel(CntF game, ClickListener clickListener, int width, int height) {

        this.game = game;
        this.setPreferredSize(new Dimension(width, height));
        this.addMouseListener(clickListener);

        this.xPositions.addAll(game.xPositions);
        this.oPositions.addAll(game.oPositions);

        this.controlPanel = null;

        x_colour = Color.RED;
        o_colour = Color.BLUE; //Color.YELLOW;
        board_colour = new Color(99, 184, 240);
    }

    public void updateBoard(int animateCol, int animateRow) {
        this.x = 80 + animateCol * 130;
        this.animateRowCo = 130 + animateRow * 115;

        this.animateRow = animateRow;
        this.animateCol = animateCol;

        this.xPositions.addAll(game.xPositions);
        this.oPositions.addAll(game.oPositions);

        animationTimer.start();
    }

    public void actionPerformed(ActionEvent e) {
        y += vy;
        vy += ay;

        repaint();

        if (y >= animateRowCo) {
            y = animateRowCo;
            animationTimer.stop();

            animateCol = -1;
            y = -150;
            vy = 1;

            game.doingMove.countDown();

            repaint();

            controlPanel.text_string = null;
            controlPanel.repaint();

            controlPanel.percentage_bar.setPercentage(0);
        }
    }

    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        x_colour = controlPanel.x_colour_toggle.getCurrent_state();
        o_colour = controlPanel.o_colour_toggle.getCurrent_state();

        drawBoard(g2d);


        if (animateCol != -1) {

            if (game.xPositions.contains(10*animateCol + animateRow))
                g2d.setColor(x_colour);
            else
                g2d.setColor(o_colour);

            Ellipse2D.Double circle = new Ellipse2D.Double();
            circle.setFrame(x, y, 105, 105);
            g2d.fill(circle);
        }
    }

    private void drawBoard(Graphics2D g2d) {

        // Board background
        g2d.setColor(board_colour); // or Color.RED;
        g2d.fill(background_rectangle);

        // Drawing board Circles
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                if (10 * animateCol + animateRow == 10*i + j)
                    g2d.setColor(Color.WHITE);
                else if (xPositions.contains(10*i + j))
                    g2d.setColor(x_colour);
                else if (oPositions.contains(10*i + j))
                    g2d.setColor(o_colour);
                else
                    g2d.setColor(Color.WHITE);
                Ellipse2D.Double circle = new Ellipse2D.Double(80 + i * 130, 130 + j * 115, 105, 105);
                g2d.fill(circle);
                if (winCoords.contains(10*i + j)) {
                    g2d.setColor(Color.GREEN);
                    g2d.setStroke(new BasicStroke(5));
                    g2d.draw(circle);
                }
            }
        }

        Ellipse2D.Double circle = new Ellipse2D.Double(150, 200, 75, 75);
        g2d.setColor(Color.RED);
        //g2d.fill(circle);

        Line2D.Double line = new Line2D.Double(0, 0, 250, 400);
        g2d.setColor(Color.BLUE);
        //g2d.draw(line);

        g2d.setFont(new Font("BM Jua", Font.BOLD, 100));
        g2d.setColor(new Color(99, 184, 240));
        g2d.drawString("Connect 4", 300, 80);
    }

    public void update_positions(Set<Integer> x, Set<Integer> o) {
        xPositions.clear();
        xPositions.addAll(x);
        oPositions.clear();
        oPositions.addAll(o);
    }

    public void addControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }
}
package com.ryan;

import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {

    ClickListener clickListener;
    GamePanel gamePanel;
    ControlPanel controlPanel;
    CntF game;

    public MyFrame(CntF game) {

        this.game = game;

        this.clickListener = new ClickListener(game, this);
        int gamePanelWidth = 1050;
        int controlPanelWidth = Toolkit.getDefaultToolkit().getScreenSize().width - 1050 - 20;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height - 150;

        this.gamePanel = new GamePanel(game, clickListener, gamePanelWidth, height);
        this.controlPanel = new ControlPanel(game, clickListener, gamePanel, controlPanelWidth, height);
        clickListener.set_up_listener(gamePanel, controlPanel);
        gamePanel.addControlPanel(controlPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Connect 4");
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        JPanel assPanel = new JPanel();
        assPanel.setSize(new Dimension(gamePanelWidth + controlPanelWidth, height));
        // assPanel.setSize(gamePanelWidth + controlPanelWidth, height);
        assPanel.add(gamePanel, BorderLayout.WEST);
        assPanel.add(controlPanel, BorderLayout.EAST);


        this.add(assPanel);
        this.pack();
        this.setVisible(true);

    }
}

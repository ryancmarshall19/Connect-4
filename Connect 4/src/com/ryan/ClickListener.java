package com.ryan;

import java.awt.*;
import java.awt.event.*;

public class ClickListener implements MouseListener, MouseMotionListener {

    CntF game;
    MyFrame frame;
    GamePanel gamePanel;
    ControlPanel controlPanel;

    boolean on_depth_slider = false;

    public ClickListener(CntF game, MyFrame frame) {
        this.game = game;
        this.frame = frame;
        this.gamePanel = null;
        this.controlPanel = null;
    }

    public void set_up_listener(GamePanel gamePanel, ControlPanel controlPanel) {
        this.gamePanel = gamePanel;
        this.controlPanel = controlPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Call either gamePanel or controlPanel methods depending on where the mouse is.
        // if on the button line, move button to there.
        int x = e.getX();
        int y = e.getY();

        if (e.getComponent() == gamePanel && x > 60 && x < 60 + 930 && y > 120 && y < 120 + 700) {
            if (game.humanMoveReady.getCount() != 0) {
                if (!game.initialized) {
                    if (controlPanel.human_button.getCurrent_state() == 1) {
                        game.set_players(new Player(1, true), new Player(0, true));
                    } else {
                        if (controlPanel.human_move_first.get_state()) {
                            game.set_players(new Player(1, true), new Player(0, false));
                        } else {
                            game.set_players(new Player(1, false), new Player(0, true));
                        }
                    }
                }
                game.mouseX = x;
                game.mouseY = y;
                game.humanMoveReady.countDown();
            }
        } else {
            if (controlPanel.depth_slider.point_on(x, y)) {
                controlPanel.depth_slider.set_position(x);
                controlPanel.depth_slider.reset_colours();
            } else if (controlPanel.human_button.mouseOn(x, y) || controlPanel.AI_button.mouseOn(x, y)) {
                controlPanel.human_button.changeCurrent_state(false);
                controlPanel.AI_button.changeCurrent_state(false);

                if (controlPanel.human_button.getCurrent_state() == 1) {
                    controlPanel.human_move_first.set_pos(true);
                }

                if (controlPanel.AI_button.getCurrent_state() == 1) {
                    if (!controlPanel.human_move_first.get_state()) {
                        game.set_players(new Player(1, false), new Player(0, true));
                    }
                    controlPanel.human_move_first.setEnabled(true);
                } else {
                    controlPanel.human_move_first.setEnabled(false);
                }
            } else if (controlPanel.human_move_first.mouseOn(x, y)) {
                controlPanel.human_move_first.switch_pos();
            } else if (controlPanel.x_colour_toggle.mouseOn(x, y)) {
                controlPanel.x_colour_toggle.changeCurrent_state(true);
            } else if (controlPanel.o_colour_toggle.mouseOn(x, y)) {
                controlPanel.o_colour_toggle.changeCurrent_state(true);
            } else if (controlPanel.undo_move.mouseOn(x, y)) {
                controlPanel.undo_move.setState(2, true);
                game.undo_move(gamePanel, true);
            } else if (controlPanel.new_game.mouseOn(x, y)) {
                controlPanel.new_game.setState(2, true);
                game.new_game(frame);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        on_depth_slider = false;
        controlPanel.depth_slider.reset_colours();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    // MOTION LISTENER

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getComponent() == controlPanel) {
            if (on_depth_slider) {
                controlPanel.depth_slider.set_position(e.getX());
            }

            if (controlPanel.depth_slider.point_on(e.getX(), e.getY())) {
                on_depth_slider = true;
                controlPanel.depth_slider.set_position(e.getX());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (controlPanel.undo_move.getState() == 2)
            controlPanel.undo_move.setState(1, true);
        if (controlPanel.new_game.getState() == 2)
            controlPanel.new_game.setState(1, true);
    }
}

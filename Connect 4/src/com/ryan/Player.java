package com.ryan;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Player {

    int letter;
    boolean human;

    int depth;
    int og_empty_sqrs;
    int max_player;
    int move_cal = 42;

    boolean pos_eval = true;

    private final Map<Long, Integer> opening_library;

    private final Set<Long> calculated_positions;
    private final TreeMap<Long, Integer> calculated_scores;

    private MyFrame frame;

    double percentageDone;
    private int howManyMoves;


    public Player(int letter, boolean human) {
        this.letter = letter;
        this.human = human;

        opening_library = new TreeMap<>();
        set_up_openings(opening_library);

        calculated_positions = new HashSet<>();
        calculated_scores = new TreeMap<>();

        frame = null;
        percentageDone = 0;
        howManyMoves = 0;
    }

    public int get_move(CntF game, MyFrame frame) {
        this.frame = frame;
        int pos;
        if (human)
            pos = get_move_human(game);
        else
            pos = get_move_AI(game);
        if (game.available_moves.size() == 42) {
            frame.controlPanel.AI_button.setEnabled(false);
            frame.controlPanel.human_button.setEnabled(false);
            frame.controlPanel.human_move_first.setEnabled(false);
            game.initialized = true;
        }
        return pos;
    }

    public int get_move_AI(CntF game) {

        frame.controlPanel.text_string = "You can change the AI depth throughout the game.";
        frame.controlPanel.repaint();

        int[] position = new int[2];

        frame.controlPanel.percentage_bar.setPercentage(1);

         max_player = letter;
        og_empty_sqrs = game.available_moves.size();

        calculated_scores.clear();
        calculated_positions.clear();

        switch (og_empty_sqrs) {
            case 42, 41, 40, 39, 38, 37, 36 -> move_cal = 9;
            case 35, 34, 33, 32 -> move_cal = 10;
            case 31, 30, 29, 28 -> move_cal = 11;
            case 27, 26 -> move_cal = 12;
            case 25, 24 -> move_cal = 13;
            case 23, 22 -> move_cal = 14;
            case 21, 20 -> move_cal = 16;
            case 19, 18 -> move_cal = 17;
            case 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 -> move_cal = 41;
        }

        move_cal += frame.controlPanel.depth_slider.get_value();
        pos_eval = true;
        if (move_cal < 2) {
            move_cal = 2;
            pos_eval = false;
        }

        percentageDone = 0;
        howManyMoves = game.possible_moves_set.size();

        // System.out.println("Possible moves: " + game.possible_moves_set);

        if (opening_library.containsKey(game.trinarize_board()) && move_cal > 5) {
            position[1] = opening_library.get(game.trinarize_board());
        } else {
            position = minimax(game, letter);
        }

        for (int i = 0; i <= 100 - percentageDone; i+= 5) {
            while (percentageDone + i < 15) {
                i++;
            }
            frame.controlPanel.percentage_bar.setPercentage(percentageDone + i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        frame.controlPanel.percentage_bar.setPercentage(100);

        // System.out.println(Arrays.toString(position));
        // System.out.println("Move cal: " + move_cal)˚µ;

        return position[1];
    }

    private int[] minimax(CntF state, int player) {
        boolean break_cond = false;

        int other_player = -1 * player + 1;

        int bestPos = -1;
        int bestScore;
        if (player == max_player)
            bestScore = -1_000_000;
        else
            bestScore = 1_000_000;
        Set<Integer> moves_to_iterate = new HashSet<>(state.possible_moves_set);

        for (int possible_move : moves_to_iterate) {
            // System.out.println("Trying move " + possible_move + " (player " + player + ").");

            int sim_score;
            int sim_position;
            long state_trinary;
            int num_empty_sqrs = state.available_moves.size() - 1; // -1 because we're about to implement a move

            if (player == 1)
                state.xPositions.add(possible_move);
            else
                state.oPositions.add(possible_move);

            state_trinary = state.trinarize_board();

            if (calculated_positions.contains(state_trinary) && state_trinary != -1) {
                // System.out.println("Already calculated this.");
                sim_score = calculated_scores.get(state_trinary);

                if (player == 1)
                    state.xPositions.remove(possible_move);
                else
                    state.oPositions.remove(possible_move);
            } else {
                // System.out.println("Not already calculated.");
                if (player == 1)
                    state.xPositions.remove(possible_move);
                else
                    state.oPositions.remove(possible_move);
                // step 1: try that spot
                state.implement_move(possible_move, player);

                // check if we won with that move
                if (state.currentWinner) {
                    if (player == max_player) {
                        sim_score = 10 * (num_empty_sqrs + 1);
                    } else {
                        sim_score = -10 * (num_empty_sqrs + 1);
                    }



                    // System.out.println("Found winner.");

                    break_cond = true;


                    if (player == max_player)
                        state.max_3R_nums.add(0, new HashSet<>());
                    else
                        state.other_3R_nums.add(0, new HashSet<>());

                    state.currentWinner = false;
                } else if (state.available_moves.size() == 0) {  // ran out of squares
                    sim_score = 0;

                    if (player == max_player)
                        state.max_3R_nums.add(0, new HashSet<>());
                    else
                        state.other_3R_nums.add(0, new HashSet<>());
                    // System.out.println("Ran out of squares.");
                } else {  // no one wins, and there are moves left
                    if (num_empty_sqrs <= og_empty_sqrs - move_cal) { // done checking further

                        sim_score = state.pos_eval(player, max_player, possible_move);
                        // System.out.println("Calculated score: " + sim_score);
                        if (!pos_eval && player == max_player) {
                            sim_score *= -1;
                        }
                        // System.out.println("Score is pos eval: " + sim_score);
                        if (pos_eval) {
                            if (sim_score >= 8 && max_player == player) { // auto win
                                // System.out.println("Auto win found1.");
                                break_cond = true;
                            } else if (sim_score <= -8 && max_player != player) {
                                // System.out.println("Auto win found2.");
                                break_cond = true;
                            }
                        } else {
                            if (sim_score <= -8) {
                                // System.out.println("Auto win found3.");
                                break_cond = true;
                            }
                        }

                    } else {  // check further
                        // update 3 rows and such
                        state.pos_eval(player, max_player, possible_move);
                        sim_score = minimax(state, other_player)[0];
                    }
                }

                // undo the move, reset all the things. This is essentially an anti-implement_move
                // System.out.println("Undoing move: " + possible_move + ", letter: " + player);
                if (pos_eval) {
                    calculated_scores.put(state_trinary, sim_score);
                    calculated_positions.add(state_trinary);
                }

                state.possible_moves_set.add(possible_move);
                state.possible_moves_set.remove(possible_move - 1);
                state.available_moves.add(possible_move);
                if (player == 1)
                    state.xPositions.remove(possible_move);
                else
                    state.oPositions.remove(possible_move);

                state.reset_3Rs(player, max_player, possible_move);
            }

            sim_position = possible_move;  // have to do it here -- not earlier -- cause of recursion I think

            // System.out.println("Move: " + sim_position + ", letter: " + player + ", sim score: " + sim_score + ", best score (pos " + bestPos + "): " + bestScore);

            // if this is the best score we've found within this for loop, update the variables
            boolean closer_to_mid = Math.abs(3 - sim_position / 10) < Math.abs(3 - bestPos / 10);
            if (player == max_player) {
                    if (sim_score > bestScore) {
                        bestScore = sim_score;
                        bestPos = sim_position;
                        // System.out.println("Sim score > best score. Updated (" + bestPos + ", " + bestScore + ").");
                    } else if (sim_score == bestScore) {
                        if (closer_to_mid && pos_eval) {
                            bestPos = sim_position;
                            // System.out.println("Sim score == best score, closer to mid. Updated. (" + bestPos + ", " + bestScore + ").");
                        } else if (!pos_eval && (Math.abs(1 - sim_position / 10) < Math.abs(1 - bestPos / 10) || Math.abs(5 - sim_position / 10) < Math.abs(5 - bestPos / 10))) {
                            bestPos = sim_position;
                            // System.out.println("Sim score == best score, closer to mid. Updated. (" + bestPos + ", " + bestScore + ").");
                        }
                    }
            } else {
                if (sim_score < bestScore) {
                    bestPos = sim_position;
                    bestScore = sim_score;
                    // System.out.println("Sim score < best score. Updated.(" + bestPos + ", " + bestScore + ").");
                } else if (sim_score == bestScore && closer_to_mid) {
                    bestPos = sim_position;
                    // System.out.println("Sim score == best score, closer to mid. Updated. (" + bestPos + ", " + bestScore + ").");
                }
            }

            // update the progress bar
            if (num_empty_sqrs == og_empty_sqrs - 2) {
                // call a graphics function
                percentageDone += 100D/(Math.pow(howManyMoves, 2));
                frame.controlPanel.percentage_bar.setPercentage(percentageDone);
            }

            if (break_cond) {
                break;
            }
        }

        return new int[] {bestScore, bestPos};
    }

    public int get_move_human(CntF game) {
        frame.controlPanel.text_string = "Click on the board to move.";
        frame.controlPanel.repaint();
        if (game.initialized)
            game.create_human_move_ready();
        try {
            game.humanMoveReady.await();
        } catch (InterruptedException ignored) {}

        frame.controlPanel.text_string = null;
        frame.controlPanel.repaint();

        int column = (game.mouseX - 80)/130;

        for (int i = 0; i < 6; i++) {
            if (game.possible_moves_set.contains(column*10 + 5 - i)) {  // iterate from the bottom of the column
                return column*10 + 5 - i;
            }
        }
        System.out.println("You can't move there");
        // do graphics stuff. Listen again.
        return get_move_human(game);
    }

    private void set_up_openings(Map<Long, Integer> m) {
        // empty board
        m.put(-2934293422202152995L, 35);
        // x in 35
        m.put(-2934293359440033777L, 34);
        // x, o; 35, 34
        m.put(-2934293348979680574L, 33);
        // x, o, x; 35, 34, 33
        m.put(-2934293342006111772L, 32);
        // x, o, x, o; 35, 34, 33, 32
        m.put(-2934293340843850305L, 31);

        // all center is filled, go to the right
        m.put(-2934293339939869164L, 45);

        // all center is filled except top spot. O went 25.
        m.put(-2934293340025962606L, 24);
        // same, but yellow went 45
        m.put(-2934270463276554366L, 44);
        // this strain -- 25/24 version -- X went 24.
        m.put(-2934293339997264792L, 23);
        // same strain -- X went 44.
        m.put(-2934255212081584392L, 43);

        // x goes to the bottom twice
        m.put(-2934247595394770652L, 25);
        m.put(-2934293348893587132L, 45);

        m.put(-2934247587258940383L, 25);
        m.put(-2934293340757756863L, 45);
    }

}


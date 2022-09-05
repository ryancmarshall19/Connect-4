package com.ryan;

import java.io.*;
import java.util.Arrays;

public class Connect4 {

    public static void main(String[] args) {
        while (true) {
            CntF game = new CntF();
            play(game);
        }
    }

        // wait for play again button

        /* Scanner scanner = new Scanner(System.in);

        while (run_again) {
            String p_a_choice = "X";
            run_again = false;

            System.out.print("To play against a computer, enter 'C'. To play against another person, enter 'H': ");
            game_type = scanner.next().toUpperCase();

            if (game_type.equals("C")) {

                boolean valid_letter_choice = false;
                while (!valid_letter_choice) {  // getting letter
                    System.out.print("To play first, enter 'X'. If you want the computer to play first, enter 'O': ");
                    p1_letter = scanner.next().toUpperCase();
                    if (p1_letter.equals("X") || p1_letter.equals("O")) { valid_letter_choice = true; }
                }

                String time_choice = "X";
                while (time_choice.equals("X")) {  // getting time print choice
                    System.out.print("Enter 'P' to print calculation numbers and times, or 'N' to only display the game: ");
                    time_choice = scanner.next().toUpperCase();
                    if (!(time_choice.equals("P") || time_choice.equals("N"))) { time_choice = "X"; }
                }

                String takeback_letter = "X";
                boolean takeback_choice;
                while (!(takeback_letter.equals("T") || takeback_letter.equals("N"))) {
                    System.out.print("Enter 'T' to allow Takebacks (undoing one or multiple moves) or " +
                            "'N' to turn Takebacks off: ");
                   takeback_letter = scanner.next().toUpperCase();
                   if (takeback_letter.equals("T")) {
                       takeback_choice = true;
                       System.out.println("\nTo undo any move, enter 'B' (for 'back') instead of a column number. " +
                                       "You will then be given a choice of which board state you want to return to.\n");
                   }
                   else if (takeback_letter.equals("N")) { takeback_choice = false; }
                   else { System.out.println("Please enter a valid input."); }
                }

                if (p1_letter.equals("X")) {
                    x_player = new Player(1, true, 0); // pass in arguments
                    o_player = new Player(0, false, 0);
                    play(new CntF(), x_player, o_player);
                } else {
                    x_player = new Player(1, false, 2); // pass in arguments
                    o_player = new Player(0, true, 0);
                    play(new CntF(), x_player, o_player);
                }

                while (!(p_a_choice.equals("A") || p_a_choice.equals("E"))) {  // happens either way
                    System.out.print("To play again, enter 'A'. To exit, enter 'E': ");
                    p_a_choice = scanner.next().toUpperCase();
                    if (p_a_choice.equals("A")) { run_again = true; }
                    else if (!p_a_choice.equals("E")) { System.out.println("Please enter a valid input."); }
                }

            } else if (game_type.equals("H")) {
                x_player = new Player(1, true, 0);
                o_player = new Player(0, true, 0);
                play(new CntF(), x_player, o_player);

                while (!(p_a_choice.equals("A") || p_a_choice.equals("E"))) {  // happens either way
                    System.out.print("To play again, enter 'A'. To exit, enter 'E': ");
                    p_a_choice = scanner.next().toUpperCase();
                    if (p_a_choice.equals("A")) { run_again = true; }
                    else if (!p_a_choice.equals("E")) { System.out.println("Please enter a valid input."); }
                }

            } else {
                System.out.println("Please enter a valid input.");
                run_again = true;
            }
        }
        System.out.println("Thanks for playing !"); */

    public static void play(CntF game) {
        MyFrame frame = new MyFrame(game);
        int letter = 1;  // 1 is x, 0 is o.
        // do board visuals

        PrintWriter pw;
        BufferedReader br;

        frame.controlPanel.text_string = "Click on the board to start the game.";
        frame.controlPanel.repaint();

        try {
            game.initializationLatch.await();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // clear the move tracker file
        try {
            FileWriter fileW = new FileWriter("Used_move_list.txt");
            pw = new PrintWriter(fileW); pw.print(""); pw.close();
        } catch (IOException ignored) {  }

        int coordinates;

        if (!game.o_player.human)
            game.max = 0;

        game.create_game_end_latch();

        while (!game.available_moves.isEmpty()) {

            if (game.game_end.getCount() == 0) {
                game.create_game_end_latch(); }

            // Get the move.
            try { game.doingMove.await(); } catch (InterruptedException e) {e.printStackTrace();}
            game.create_doing_move();

            if (letter == 1) { coordinates = game.x_player.get_move(game, frame); }
            else { coordinates = game.o_player.get_move(game, frame); }

            // System.out.println("Got move");

            // Do the move. (or go back)
            if (coordinates == -1) {
                // takeback(coordinates);
                assert true; assert false;  // lmow this is java's 'pass'
            }
            else {
                game.implement_move(coordinates, letter);
                if (letter == 1)
                    game.x_moves.add(0, coordinates);
                else
                    game.o_moves.add(0, coordinates);
                game.board[coordinates/10][coordinates % 10] = letter;
                game.boardStates[game.board_state_num] = game.board;
                game.board_state_num += 1;
                frame.gamePanel.updateBoard(coordinates/10, coordinates%10);
                /* for (int i = 0; i < 6; i++) {
                    System.out.print("| ");
                    for (int j = 0; j < 7; j++) {
                        if (game.board[j][i] == -1)
                            System.out.print(" ");
                        else
                            System.out.print(game.board[j][i]);
                        if (j < 6)
                            System.out.print(" | ");
                        else
                            System.out.println(" |");
                    }
                }
                System.out.println("  1   2   3   4   5   6   7"); */
            }

            // System.out.println(game.trinarize_board());

            if (!game.x_player.human) {
                game.pos_eval(letter, 1, coordinates);
            } else if (!game.o_player.human) {
                game.pos_eval(letter, 0, coordinates);
            } else {  // if both human
                game.pos_eval(letter, 1, coordinates);
            }

            /* System.out.print("Input: ");
            Scanner scanner = new Scanner(System.in);
            scanner.next(); */

            // Update board states file. Doing it in a file so that the element doesn't change as game.board changes
            try {
                FileWriter fileW = new FileWriter("Used_move_list.txt");
                pw = new PrintWriter(fileW); pw.println(Arrays.toString(game.board)); pw.close();
            } catch (IOException ignored) {  }

            // check if someone won
            if (game.currentWinner) {
                // update display()
                game.available_moves.clear();
                try { game.doingMove.await(); } catch (InterruptedException e) {e.printStackTrace();}
                frame.gamePanel.winCoords.addAll(game.win_coords(coordinates, letter));
                frame.gamePanel.repaint();
            }

            letter = -1*letter + 1;

            if (game.available_moves.size() == 0) {
                try {
                    game.game_end.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
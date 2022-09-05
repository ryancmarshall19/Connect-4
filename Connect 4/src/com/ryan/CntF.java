package com.ryan;

import com.sun.jdi.ThreadReference;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class CntF {
    int[][] board;

    Player x_player;
    Player o_player;

    boolean initialized = false;

    CountDownLatch initializationLatch = new CountDownLatch(1);

    int[][][] boardStates;
    int board_state_num;

    int max = 1;

    boolean currentWinner;

    Set<Integer> available_moves;
    Set<Integer> possible_moves_set;

    Set<Integer> xPositions;
    Set<Integer> oPositions;

    LinkedList<Integer> x_moves;
    LinkedList<Integer> o_moves;

    Set<Integer> max_3Rfinishing_coords;
    Set<Integer> max_historic_3Rfinishing_coords;
    ArrayList<Set<Integer>> max_3R_nums;

    Set<Integer> other_3Rfinishing_coords;
    Set<Integer> other_historic_3Rfinishing_coords;
    ArrayList<Set<Integer>> other_3R_nums;

    int mouseX = -1;
    int mouseY = -1;

    CountDownLatch humanMoveReady;
    CountDownLatch doingMove;
    CountDownLatch game_end;

    final long starting_board_num = -2_934_293_422_202_152_995L;

    String null_pointer;


    public CntF() {
        this.board = new int[7][6];  // list of 7 columns each with 6 rows (slots) in them, from 0 (top) to 5 (bottom).
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                board[i][j] = -1;
            }
        } // -1 is empty, 0 is O, 1 is X

        this.currentWinner = false;

        this.boardStates = new int[42][7][6];
        this.board_state_num = 0;

        this.available_moves = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++)
                available_moves.add(10 * i + j);
        }

        this.possible_moves_set = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            possible_moves_set.add(10 * i + 5);
        }


        xPositions = new HashSet<>();
        oPositions = new HashSet<>();

        x_moves = new LinkedList<>();
        o_moves = new LinkedList<>();

        max_3Rfinishing_coords = new HashSet<>();
        max_historic_3Rfinishing_coords = new HashSet<>();
        max_3R_nums = new ArrayList<>();

        other_3Rfinishing_coords = new HashSet<>();
        other_historic_3Rfinishing_coords = new HashSet<>();
        other_3R_nums = new ArrayList<>();

        doingMove = new CountDownLatch(0);
        humanMoveReady = new CountDownLatch(1);
        game_end = new CountDownLatch(0);

        null_pointer = null;
    }

    public void set_players(Player x, Player o) {
        this.x_player = x;
        this.o_player = o;
        initializationLatch.countDown();
    }

    public void implement_move(int position, int letter) {
        //System.out.println("Implementing move ...");
        //System.out.println("Possible moves: " + possible_moves_set);
        //System.out.println("Available moves: " + available_moves);
        possible_moves_set.remove(position);
        //System.out.println("Removing " + position + " from possible moves (and available)");
        if (position % 10 > 0) {
            //System.out.println("Adding " + (position - 1) + "to possible moves");
            possible_moves_set.add(position - 1);
        }

        available_moves.remove(position);

        if (letter == 1)
            xPositions.add(position);
        else
            oPositions.add(position);

        boolean bmax = letter == max;

        currentWinner = check_winner(position, bmax);

        max_3Rfinishing_coords.remove(position);
        other_3Rfinishing_coords.remove(position);

        //System.out.println("After: ");
        //System.out.println("Possible moves: " + possible_moves_set);
        //System.out.println("Available moves: " + available_moves);
    }

    private boolean check_winner(int position, boolean max) {
        if (max) {
            return max_3Rfinishing_coords.contains(position);
        } else {
            return other_3Rfinishing_coords.contains(position);
        }
    }

    private boolean old_check_winner(int position, int letter) {

        int row_num = position % 10;

        Set<Integer> lPositions;
        if (letter == 1)
            lPositions = xPositions;
        else
            lPositions = oPositions;

        // checking if 4 in the column
        if (row_num <= 2) {  // can only have four up if you're in the fourth row or higher
            if (lPositions.contains(position + 1) && lPositions.contains(position + 2) && lPositions.contains(position + 3)) {
                return true;
            }
        }

        // checking if 4 in horizontal row
        for (int i = 0; i < 40; i += 10) {

            if (position + i >= 30) { // if in the third col or more
                if (lPositions.contains(position + i) && lPositions.contains(position + i - 10) &&
                lPositions.contains(position + i - 20) && lPositions.contains(position + i - 30)) {
                    return true;
                }
            }
        }

        // checking positive slope direction. The for loop moves the starting positions up and to the right 4 times,
        // and I check the four positions down and to the left of the starting position.
        int j;
        for (int i = 0; i < 4; i++) {
            j = 10 * i;
            if (position/10 + j >= 3 && row_num - i <= 2) { // if position is greater/eq to col 3 and less/eq row 2
                if (lPositions.contains(position + j - i) && lPositions.contains(position + j - 10 - i + 1) &&
                        lPositions.contains(position + j - 20 - i + 2) && lPositions.contains(position + j - 30 - i + 3)) {
                    return true;
                }
            }
            if (position + j == 6 || row_num - i == 0) // if you're gonna reach the end of the board next loop, break
                break;
        }

        // checking negative slope diagonal. Same premise as positive slope.
        // each loop, move starting values up and left. Then, check the four spots down and right.
        for (int i = 0; i < 4; i++) {
            j = 10 * i;  // i is row, j is col.
            if (position/10 - j <= 3 && row_num - i <= 2) {  // you need to be at/above 2 and to the left of col 3 to work
                if (lPositions.contains(position - j - i) && lPositions.contains(position - j + 10 - i + 1) &&
                lPositions.contains(position - j + 20 - i + 2) && lPositions.contains(position - j + 30 - i + 3)) {
                    return true;
                }
            }
            if (position - j == 0 || row_num - i == 0) // if you're at the top or the left, break
                break;
        }
        return false;
    }

    public void create_human_move_ready() {
        humanMoveReady = new CountDownLatch(1);
    }

    public void create_doing_move() {
        doingMove = new CountDownLatch(1);
    }

    public int pos_eval(int letter, int max_letter, int position) {
        Set<Integer> finishing_coords = new HashSet<>();

        int letterCounter = 0;

        Set<Integer> letter_3R_finishing_coords;
        Set<Integer> letter_historic_finishing_coords;

        if (letter == max_letter) {
            letter_3R_finishing_coords = max_3Rfinishing_coords;
            letter_historic_finishing_coords = max_historic_3Rfinishing_coords;
        } else {
            letter_3R_finishing_coords = other_3Rfinishing_coords;
            letter_historic_finishing_coords = other_historic_3Rfinishing_coords;
        }

        Set<Integer> lPositions;
        Set<Integer> nlPositions;
        if (letter == 1) {
            lPositions = xPositions;
            nlPositions = oPositions;
        } else {
            lPositions = oPositions;
            nlPositions = xPositions;
        }

        int finishing_coord = -1;

        // check down
        if (position % 10 <= 3 && position % 10 > 0) {
            finishing_coord = position - 1;
            for (int i = 0; i < 3; i++) {
                if (lPositions.contains(position + i)) {
                    letterCounter++;
                } else if (nlPositions.contains(position + i)) {
                    break;
                }
            }
            if (letterCounter == 3 && !letter_3R_finishing_coords.contains(finishing_coord)) {
                finishing_coords.add(finishing_coord);
            }
        }

        letterCounter = 0;

        // check across
        boolean space = false;

        for (int i = 0; i < 70; i += 10) {
            if (position - 30 + i >= 0 && position - 30 + i < 70) {
                if (lPositions.contains(position - 30 + i)) {
                    letterCounter++;
                } else if (nlPositions.contains(position - 30 + i)) {
                    space = false;
                    letterCounter = 0;
                } else {
                    finishing_coord = position - 30 + i;
                    if (space) {
                        letterCounter = 0;
                    } else {
                        space = true;
                    }
                }
                if (letterCounter == 3 && space && !letter_3R_finishing_coords.contains(finishing_coord)) {
                    finishing_coords.add(finishing_coord);
                    space = false;
                    letterCounter = (position + i - 30) / 10 - finishing_coord / 10;  // how many things have u found since the space
                }
            }
        }

        letterCounter = 0;
        space = false;

        // check negative slope. Moving down and to the right through the loop
        for (int i = 0; i < 7; i++) {
            if (position - 30 + 10 * i >= 0 && (position % 10) - 3 + i >= 0 &&
                    position - 30 + 10 * i < 70 && (position % 10) - 3 + i < 6) {
                if (lPositions.contains(position - 33 + 11 * i)) {
                    letterCounter++;
                } else if (nlPositions.contains(position - 33 + 11 * i)) {
                    space = false;
                    letterCounter = 0;
                } else {
                    finishing_coord = position - 33 + (11 * i);
                    if (space && !lPositions.contains(position - 33 + (11 * (i - 1)))) {
                        letterCounter = 0;
                        if (i > 3) {
                            break;
                        }
                    } else {
                        space = true;
                    }
                }
                if (letterCounter == 3 && space && !letter_3R_finishing_coords.contains(finishing_coord)) {
                    finishing_coords.add(finishing_coord);
                    space = false;
                    letterCounter = (position - 33 + 11 * i) / 10 - finishing_coord / 10; // how many columns have u gone across since the space
                }
            }
        }

        letterCounter = 0;
        space = false;

        // check positive slope. Moving down and to the left through the loop
        for (int i = 0; i < 7; i++) {
            int j = 10 * i;
            if (position + 30 - j < 70 && (position % 10) - 3 + i >= 0 &&
                    position + 30 - j >= 0 && (position % 10) - 3 + i < 6) {
                if (lPositions.contains(position + 30 - j - 3 + i)) {
                    letterCounter++;
                } else if (nlPositions.contains(position + 30 - j - 3 + i)) {
                    space = false;
                    letterCounter = 0;
                } else {
                    finishing_coord = position + 30 - j - 3 + i;
                    if (space && !lPositions.contains(position + 30 - (j - 10) - 3 + (i - 1))) {
                        letterCounter = 0;
                        if (i > 3)
                            break;
                    } else {
                        space = true;
                    }
                }
                if (letterCounter == 3 && space && !letter_3R_finishing_coords.contains(finishing_coord)) {
                    finishing_coords.add(finishing_coord);
                    space = false;
                    letterCounter = finishing_coord / 10 - (position + 30 - 3 - j + i) / 10;
                }
            }
        }

        letter_3R_finishing_coords.addAll(finishing_coords);
        letter_historic_finishing_coords.addAll(finishing_coords);


        if (letter == max_letter)
            max_3R_nums.add(0, finishing_coords);
        else
            other_3R_nums.add(0, finishing_coords);

        int max_auto_win = 0;
        int o_auto_win = 0;

        for (int coord : max_3Rfinishing_coords) {
            if (max_3Rfinishing_coords.contains(coord - 1))
                if (!(max_auto_win > 0 && max_3Rfinishing_coords.contains(coord + 1)))
                    max_auto_win++;
        }
        for (int coord : other_3Rfinishing_coords) {
            if (other_3Rfinishing_coords.contains(coord - 1))
                if (!(o_auto_win > 0 && other_3Rfinishing_coords.contains(coord + 1)))
                    o_auto_win++;
        }

        return (max_3Rfinishing_coords.size() - other_3Rfinishing_coords.size() + 10 * max_auto_win - 10 * o_auto_win);

    }

    public void reset_3Rs(int letter, int max_letter, int position) {

        /* System.out.println("Undoing move.");
        System.out.println("Max 3R finishing coords: " + max_3Rfinishing_coords);
        System.out.println("Max 3R nums: " + max_3R_nums);
        System.out.println("Max 3R historics coords: " + max_historic_3Rfinishing_coords); */

        if (letter == max_letter || letter == -1) {
            max_3Rfinishing_coords.removeAll(max_3R_nums.get(0));
            max_historic_3Rfinishing_coords.removeAll(max_3R_nums.get(0));
            max_3R_nums.remove(0);
        }
        if (letter != max_letter || letter == -1) {
            other_3Rfinishing_coords.removeAll(other_3R_nums.get(0));
            other_historic_3Rfinishing_coords.removeAll(other_3R_nums.get(0));
            other_3R_nums.remove(0);
        }

        if (max_historic_3Rfinishing_coords.contains(position))
            max_3Rfinishing_coords.add(position);
        if (other_historic_3Rfinishing_coords.contains(position))
            other_3Rfinishing_coords.add(position);
    }

    public long trinarize_board() {
        if (xPositions.contains(0) || oPositions.contains(0) || xPositions.contains(60) || oPositions.contains(60))
            return -1;

        long board_num = starting_board_num;
        long counter = 0;

        for (int c = 0; c < 70; c += 10) {
            for (int r = 0; r < 6; r++) {
                int pos = c + r;
                if (!(pos == 0 || pos == 60)) {
                    if (xPositions.contains(pos)) {
                        board_num += 2L * (long) Math.pow(3L, (int) (counter / 10) * 10) * (long) Math.pow(3L, (counter % 10));
                    } else if (oPositions.contains(pos)) {
                        board_num += (long) Math.pow(3L, (int) (counter / 10) * 10) * (long) Math.pow(3L, (counter % 10));
                    }
                    counter++;
                }
            }
        }

        // System.out.println("Board trinarized successfully. Board code : " + board_num);

        return board_num;
    }

    public void undo_move(GamePanel panel, boolean b) {
        if (board_state_num >= 2) {
            board_state_num -= 2;
            board = boardStates[board_state_num];
            xPositions.remove(x_moves.get(0));
            oPositions.remove(o_moves.get(0));

            currentWinner = false;

            reset_3Rs(1, max, x_moves.get(0));
            reset_3Rs(0, max, o_moves.get(0));

            possible_moves_set.add(x_moves.get(0));
            possible_moves_set.add(o_moves.get(0));
            possible_moves_set.remove(x_moves.get(0) - 1);
            possible_moves_set.remove(o_moves.get(0) - 1);
            available_moves.addAll(possible_moves_set);

            board[x_moves.get(0)/10][x_moves.get(0) % 10] = -1;
            board[o_moves.get(0)/10][o_moves.get(0)%10] = -1;

            x_moves.remove(0);
            o_moves.remove(0);

            if (b)
                game_end.countDown();

            panel.update_positions(xPositions, oPositions);

            for (int i = 0; i < 70; i+= 10) { for (int j = 0; j < 6; j++) {
                    if (!(xPositions.contains(i+j) || oPositions.contains(i+j)))
                        available_moves.add(i + j);
                }
            }

            panel.winCoords.clear();

            panel.repaint();
        }
    }

    public void new_game(MyFrame frame) {
        available_moves.clear();
        frame.dispose();
        humanMoveReady.countDown();
        game_end.countDown();
    }

    public void create_game_end_latch() {
        game_end = new CountDownLatch(1);
    }

    public Set<Integer> win_coords(int position, int letter) {

        Set<Integer> winSet = new HashSet<>();

        int row_num = position % 10;

        Set<Integer> lPositions;
        if (letter == 1)
            lPositions = xPositions;
        else
            lPositions = oPositions;

        // checking if 4 in the column
        if (row_num <= 2) {  // can only have four up if you're in the fourth row or higher
            if (lPositions.contains(position + 1) && lPositions.contains(position + 2) && lPositions.contains(position + 3)) {
                winSet.add(position);
                winSet.add(position + 1);
                winSet.add(position + 2);
                winSet.add(position + 3);
            }
        }

        // checking if 4 in horizontal row
        for (int i = 0; i < 40; i += 10) {

            if (position + i >= 30) { // if in the third col or more
                if (lPositions.contains(position + i) && lPositions.contains(position + i - 10) &&
                        lPositions.contains(position + i - 20) && lPositions.contains(position + i - 30)) {
                    winSet.add(position + i);
                    winSet.add(position + i - 10);
                    winSet.add(position + i - 20);
                    winSet.add(position + i - 30);
                }
            }
        }

        // checking positive slope direction. The for loop moves the starting positions up and to the right 4 times,
        // and I check the four positions down and to the left of the starting position.
        int j;
        for (int i = 0; i < 4; i++) {
            j = 10 * i;
            if (position/10 + j >= 3 && row_num - i <= 2) { // if position is greater/eq to col 3 and less/eq row 2
                if (lPositions.contains(position + j - i) && lPositions.contains(position + j - 10 - i + 1) &&
                        lPositions.contains(position + j - 20 - i + 2) && lPositions.contains(position + j - 30 - i + 3)) {
                    winSet.add(position + j - i);
                    winSet.add(position + j - 10 - i + 1);
                    winSet.add(position + j - 20 - i + 2);
                    winSet.add(position + j - 30 - i + 3);
                }
            }
            if (position + j == 6 || row_num - i == 0) // if you're gonna reach the end of the board next loop, break
                break;
        }

        // checking negative slope diagonal. Same premise as positive slope.
        // each loop, move starting values up and left. Then, check the four spots down and right.
        for (int i = 0; i < 4; i++) {
            j = 10 * i;  // i is row, j is col.
            if (position/10 - j <= 3 && row_num - i <= 2) {  // you need to be at/above 2 and to the left of col 3 to work
                if (lPositions.contains(position - j - i) && lPositions.contains(position - j + 10 - i + 1) &&
                        lPositions.contains(position - j + 20 - i + 2) && lPositions.contains(position - j + 30 - i + 3)) {
                    winSet.add(position - j - i);
                    winSet.add(position - j + 10 - i + 1);
                    winSet.add(position - j + 20 - i + 2);
                    winSet.add(position - j + 30 - i + 3);
                }
            }
            if (position - j == 0 || row_num - i == 0) // if you're at the top or the left, break
                break;
        }
        return winSet;
    }
}
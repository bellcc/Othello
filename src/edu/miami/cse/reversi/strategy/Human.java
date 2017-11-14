package edu.miami.cse.reversi.strategy;

import java.util.*;

import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class Human implements Strategy {

    @Override
    public Square chooseSquare(Board board) {
        return chooseOne(board.getCurrentPossibleSquares(), board);
    }

    public static boolean isEmpty(Board board, int row, int col) {
        return !board.getSquareOwners().containsKey(new Square(row, col));
    }

    public static int countFlips(Board board, Player current, Square move) {
        int flips = 0;

        Iterator it = board.getSquareOwners().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            int row = ((Square) pair.getKey()).getRow();
            int col = ((Square) pair.getKey()).getColumn();

            double slope = (double) (move.getColumn() - col) / (move.getRow() - row);

            if (move.getRow() - row == 0) {
                // Handles zero column
                slope = 0;
            }

            if (!move.equals(pair.getKey()) && !pair.getValue().equals(current) && (Double.isInfinite(slope) || slope == 0 || Math.abs(slope) == 1)) {
                int dirR = 0, dirC = 0;

                if (col > move.getColumn()) {
                    dirC = 1;
                } else if (col < move.getColumn()) {
                    dirC = -1;
                }

                if (row > move.getRow()) {
                    dirR = 1;
                } else if (row < move.getRow()) {
                    dirR = -1;
                }

                int r = row + dirR, c = col + dirC, count = 0;
                while (!isEmpty(board, r, c)) {
                    count ++;

                    if (!board.getSquareOwners().get(new Square(r, c)).opponent().equals(current)) {
                        flips += count;
                        break;
                    }

                    r += dirR;
                    c += dirC;
                }
            }
        }

        return flips;
    }

    public static int corners(Board board, Square move) {
        if ((move.getRow() == 0 || move.getRow() == 7) &&
                (move.getColumn() == 0 || move.getColumn() == 7)) {
            return 1000;
        }

        return 0;
    }

    public static int heuristic(Board board, Player current, Square move) {
        return countFlips(board, current, move) + corners(board, move);
    }

    public static <T> T chooseOne(Set<T> itemSet, Board board) {
        List<T> itemList = new ArrayList<>(itemSet);
        int picked = 0;

        int max = Integer.MIN_VALUE;
        for (int i = 0; i < itemList.size(); i++) {
            int value = heuristic(board, board.getCurrentPlayer(), (Square) itemList.get(i));

            if (value > max) {
                max = value;
                picked = i;
            }
        }

        return itemList.get(picked);
    }
}

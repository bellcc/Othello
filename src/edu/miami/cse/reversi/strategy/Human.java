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

    public static int countFlips(Board simulated, Board current) {
        Player type = current.getCurrentPlayer();
        return simulated.getPlayerSquareCounts().get(type) - current.getPlayerSquareCounts().get(type);
    }

    public static int corners(Square move) {
        if ((move.getRow() == 0 || move.getRow() == 7) &&
                (move.getColumn() == 0 || move.getColumn() == 7)) {
            return 1000;
        }

        return 0;
    }

    public static int heuristic(Board board, Board simulated, Square move) {
        return countFlips(board, simulated) + corners(move);
    }

    public static <T> T chooseOne(Set<T> itemSet, Board board) {
        List<T> itemList = new ArrayList<>(itemSet);
        int picked = 0;

        int max = Integer.MIN_VALUE;
        for (int i = 0; i < itemList.size(); i++) {
            Board simulated = board.play((Square) itemList.get(i));
            int value = heuristic(board, simulated, (Square) itemList.get(i));

            if (value > max) {
                max = value;
                picked = i;
            }
        }

        return itemList.get(picked);
    }
}

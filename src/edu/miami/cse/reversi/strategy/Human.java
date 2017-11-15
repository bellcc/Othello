package edu.miami.cse.reversi.strategy;

import java.util.*;

import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class Human implements Strategy {

    class Node {
        private Board board;
        private Square move;
        private int value;

        private ArrayList<Node> children;

        public Node(Board board, Square move, int value) {
            this.board = board;
            this.move = move;
            this.value = value;

            this.children = new ArrayList<>();
        }

        public Board getBoard() {
            return this.board;
        }

        public Square getMove() {
            return this.move;
        }

        public int getValue() {
            return this.value;
        }

        public void addChild(Node node) {
            children.add(node);
        }

        public ArrayList<Node> getChildren() {
            return this.children;
        }
    }

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

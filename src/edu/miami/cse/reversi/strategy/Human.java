package edu.miami.cse.reversi.strategy;

import java.util.*;

import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class Human implements Strategy {

    static class Node {
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
        Player player = current.getCurrentPlayer();
        return simulated.getPlayerSquareCounts().get(player) - current.getPlayerSquareCounts().get(player);
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

    public static ArrayList<Square> getMoves(Board board) {
        return new ArrayList<>(board.getCurrentPossibleSquares());
    }

    public static <T> Node getDecisionTree(Board board) {
        return createTree(new Node(board, null, 0), 0, 3);
    }

    public static <T> Node createTree(Node parent, int depth, int maxDepth) {
        if (depth == maxDepth) {
            return parent;
        }

        ArrayList<Square> moves = getMoves(parent.getBoard());
        for (Square move : moves) {
            Board board = parent.getBoard().play(move);
            Node child = new Node(board, move, heuristic(parent.getBoard(), board, move));

            parent.addChild(createTree(child, depth + 1, maxDepth));
        }

        return parent;
    }

    public static <T> T chooseOne(Set<T> itemSet, Board board) {
        Node tree = getDecisionTree(board);

        return new ArrayList<>(itemSet).get(0);

        /*
        int picked = 0;

        int max = Integer.MIN_VALUE;
        for (int i = 0; i < itemList.size(); i++) {
            Square move = (Square) itemList.get(i);
            Board simulated = board.play(move);

            int value = heuristic(board, simulated, move);

            // Node node = new Node(board, move, value);

            if (value > max) {
                max = value;
                picked = i;
            }
        }

        return itemList.get(picked);
        */
    }
}

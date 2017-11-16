package edu.miami.cse.reversi.strategy;

import java.util.*;

import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class Human implements Strategy {

    static Player player;

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

    public static Player getOpponent(Player player) {
        if (player.equals(Player.BLACK)) {
            return Player.WHITE;
        }

        return Player.BLACK;
    }

    public static int alphabeta(Node node, int depth, int alpha, int beta, boolean maxPlayer) {
        if (depth == 0 || node.getChildren().size() == 0) {
            return node.getValue();
        }

        if (node.getBoard().getCurrentPlayer().equals(player)) {
            int value = Integer.MIN_VALUE;

            for (Node child : node.getChildren()) {
                value = Math.max(value, alphabeta(child, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);

                if (beta <= alpha) {
                    break;
                }
            }

            return value;
        } else {
            int value = Integer.MAX_VALUE;

            for (Node child : node.getChildren()) {
                value = Math.min(value, alphabeta(child, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);

                if (beta <= alpha) {
                    break;
                }
            }

            return value;
        }
    }

    public static <T> T chooseOne(Set<T> itemSet, Board board) {
        long start = System.currentTimeMillis();


        player = board.getCurrentPlayer();

        List<T> moves = new ArrayList<>(itemSet);

        //*
        Node tree = getDecisionTree(board);



        int opt = alphabeta(tree, 2, 0, 0, true);

        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println(end - start);


        for (T move : moves) {
            for (Node child : tree.getChildren()) {
                if (move.equals(child.getMove()) && child.getValue() == opt) {
                    return move;
                }
            }
        }

        return null;
        //*/

        /*
        int picked = 0;

        int max = Integer.MIN_VALUE;
        for (int i = 0; i < moves.size(); i++) {
            Square move = (Square) moves.get(i);
            Board simulated = board.play(move);

            int value = heuristic(board, simulated, move);

            // Node node = new Node(board, move, value);

            if (value > max) {
                max = value;
                picked = i;
            }
        }

        return moves.get(picked);
        */
    }
}

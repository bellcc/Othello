package edu.miami.cse.reversi.strategy;

import java.util.*;

import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class GameProject5 implements Strategy {

    static class Node {
        private Board board;
        private Square move;
        private int value;

        private ArrayList<Node> children;

        private Square optimal;

        /*
            Internal class used to represent the decision tree structure.
         */
        public Node(Board board, Square move, int value) {
            this.board = board;
            this.move = move;
            this.value = value;

            this.children = new ArrayList<>();

            // Set default value
            this.optimal = new Square(-1, -1);
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

        public void setOptimal(Square square) {
            this.optimal = square;
        }

        public Square getOptimal() {
            return this.optimal;
        }
    }

    @Override
    public Square chooseSquare(Board board) {
        return chooseOne(board.getCurrentPossibleSquares(), board);
    }

    /*
        Counts the number of pieces that would be flipped between one game state to another.
     */
    public static int countFlips(Board simulated, Board current) {
        Player player = current.getCurrentPlayer();
        return simulated.getPlayerSquareCounts().get(player) - current.getPlayerSquareCounts().get(player);
    }

    /*
        Corners are a strategic move that should always be made if given the opportunity.
     */
    public static int corners(Square move) {
        if ((move.getRow() == 0 || move.getRow() == 7) &&
                (move.getColumn() == 0 || move.getColumn() == 7)) {
            return 1000;
        }

        return 0;
    }

    public static int countTotal(Board simulated, Board current) {
        Player player = current.getCurrentPlayer();
        return simulated.getPlayerSquareCounts().get(player) - (64 - simulated.getPlayerSquareCounts().get(player));
    }

    public static int countRealTotal(Board simulated, Board current) {
        Player player = current.getCurrentPlayer();
        int empty = 64 - simulated.getPlayerSquareCounts().get(Player.WHITE) - simulated.getPlayerSquareCounts().get(Player.BLACK);
        return simulated.getPlayerSquareCounts().get(player) - (64 - simulated.getPlayerSquareCounts().get(player) - empty);
    }

    public static int isBad(Square move){
        boolean notcorner = corners(move) == 0;
        if ((move.getRow() == 0 || move.getRow() == 7 || move.getRow() == 1 ||move.getRow() == 6)  &&
                (move.getColumn() == 0 || move.getColumn() == 7 || move.getColumn() == 1 || move.getColumn() == 6) &&
                notcorner) {
            return -1000;
        }
        return 0;
    }

    /*
        Determines if a given square is along the outermost border of the board.
     */
    public static int isEdge(Square move){
        if(move.getColumn() == 0 || move.getColumn() == 7 || move.getRow() == 0 || move.getRow() == 7){
            return 10;
        }
        return 0;
    }

    /*
        The heuristic used to evaluate a game state.
     */
    public static int heuristic(Board board, Board simulated, Square move) {
        return countFlips(board, simulated) + corners(move) + 15 * countRealTotal(board, simulated) + isEdge(move);// + isBad(move);
    }

    /*
        Retrieves all of the possible moves for a given game state.
     */
    public static ArrayList<Square> getMoves(Board board) {
        return new ArrayList<>(board.getCurrentPossibleSquares());
    }

    /*
        Outer function that creates a decision tree that has a depth of three.
     */
    public static <T> Node getDecisionTree(Board board) {
        return createTree(new Node(board, null, 0), 0, 3);
    }

    /*
        Creates the decision tree for with a given starting state.
     */
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

    /*
        Returns the other player given a player object.
     */
    public static Player getOpponent(Player player) {
        if (player.equals(Player.BLACK)) {
            return Player.WHITE;
        }

        return Player.BLACK;
    }

    /*
         Performs alpha beta pruning to determine the most optimal game state with our heuristic function.
     */
    public static int alphabeta(Node node, int depth, int alpha, int beta, boolean maxPlayer) {
        if (depth == 0 || node.getChildren().size() == 0) {
            return node.getValue();
        }

        if (maxPlayer) {
            int value = Integer.MIN_VALUE;

            for (Node child : node.getChildren()) {
                int tmp = alphabeta(child, depth - 1, alpha, beta, false);

                if (tmp > value) {
                    child.setOptimal(child.getMove());
                    value = tmp;
                }

                alpha = Math.max(alpha, value);

                if (beta <= alpha) {
                    break;
                }
            }

            return value;
        } else {
            int value = Integer.MAX_VALUE;

            for (Node child : node.getChildren()) {
                int tmp = alphabeta(child, depth - 1, alpha, beta, true);

                if (tmp < value) {
                    child.setOptimal(child.getMove());
                    value = tmp;
                }

                beta = Math.min(beta, value);

                if (beta <= alpha) {
                    break;
                }
            }

            return value;
        }
    }

    /*
        Determines what the optimal decision based on a tree that has been processed by alpha beta pruning.
     */
    public static Square getOptimal(Node tree) {
        for (Node child : tree.getChildren()) {
            if (!child.getOptimal().equals(new Square(-1, -1))) {
                return child.getOptimal();
            }
        }

        return null;
    }

    /*
        Chooses which possible move should be made with alpha beta pruning.
     */
    public static <T> T chooseOne(Set<T> itemSet, Board board) {
        List<T> moves = new ArrayList<>(itemSet);

        Node tree = getDecisionTree(board);

        int value = alphabeta(tree, 3, 0, 0, true);
        Square optimal = getOptimal(tree);

        for (T move : moves) {
            if (move.equals(optimal)) {
                return move;
            }
        }

        return null;
    }
}

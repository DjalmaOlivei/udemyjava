package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        currentPlayer = Color.WHITE;
        turn = 1;
        initialState();
    }

    public ChessPiece getPromoted() {
        return promoted;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public int getTurn() {
        return turn;
    }

    public boolean getCheck() {
        return check;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Color currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturePiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturePiece);
            throw new ChessException("You cant put yourself in check!");
        }

        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        // promotion special move
        promoted = null;
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0)
                    || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
                promoted = (ChessPiece) board.piece(target);
                //promoted = replacePromotedPiece("q");
            }
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }

        // en passant
        if (movedPiece instanceof Pawn
                && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2))
            enPassantVulnerable = movedPiece;
        else
            enPassantVulnerable = null;

        return (ChessPiece) capturePiece;
    }

    public ChessPiece replacePromotedPiece(String type) {
        if (promoted == null)
            throw new IllegalStateException("no piece to promote!");
        if (!type.equals("b") && !type.equals("n") && !type.equals("r") && !type.equals("q")) {
            type = "q";
            //throw new InvalidParameterException("Invalid promotion!");
        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);
        check = testCheck(currentPlayer);

        return newPiece;

    }

    private ChessPiece newPiece(String type, Color color) {

        if (type.equals("b"))
            return new Bishop(board, color);
        if (type.equals("n"))
            return new Knight(board, color);
        if (type.equals("r"))
            return new Rook(board, color);
        return new Queen(board, color);

    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturatedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if (capturatedPiece != null) {
            piecesOnTheBoard.remove(capturatedPiece);
            capturedPieces.add(capturatedPiece);
        }

        // rooks
        if (p instanceof King && p.getColor() == Color.WHITE) {
            // #specialmove castling kingside rook
            if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
                Position targetT = new Position(source.getRow(), source.getColumn() + 1);
                ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
                board.placePiece(rook, targetT);
                rook.increaseMoveCount();
            }

            // #specialmove castling queenside rook
            if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
                Position targetT = new Position(source.getRow(), source.getColumn() - 1);
                ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
                board.placePiece(rook, targetT);
                rook.increaseMoveCount();
            }
        } else {
            // #specialmove castling kingside rook
            if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() - 3);
                Position targetT = new Position(source.getRow(), source.getColumn() - 1);
                ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
                board.placePiece(rook, targetT);
                rook.increaseMoveCount();
            }

            // #specialmove castling queenside rook
            if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() + 4);
                Position targetT = new Position(source.getRow(), source.getColumn() + 1);
                ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
                board.placePiece(rook, targetT);
                rook.increaseMoveCount();
            }
        }

        // en passant special move
        if (p instanceof Pawn && source.getColumn() != target.getColumn() && capturatedPiece == null) {
            Position pawnPosition;
            if (p.getColor() == Color.WHITE)
                pawnPosition = new Position(target.getRow() + 1, target.getColumn());
            else
                pawnPosition = new Position(target.getRow() - 1, target.getColumn());
            capturatedPiece = board.removePiece(pawnPosition);
            capturedPieces.add(capturatedPiece);
            piecesOnTheBoard.remove(capturatedPiece);
        }

        return capturatedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturatedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece((target));
        p.decreaseMoveCout();
        board.placePiece(p, source);
        if (capturatedPiece != null) {
            board.placePiece(capturatedPiece, target);
            capturedPieces.remove(capturatedPiece);
            piecesOnTheBoard.add(capturatedPiece);

        }

        if (p instanceof King && p.getColor() == Color.WHITE) {
            // #specialmove castling kingside rook
            if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
                Position targetT = new Position(source.getRow(), source.getColumn() + 1);
                ChessPiece rook = (ChessPiece) board.removePiece(targetT);
                board.placePiece(rook, sourceT);
                rook.decreaseMoveCout();
            }

            // #specialmove castling queenside rook
            if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
                Position targetT = new Position(source.getRow(), source.getColumn() - 1);
                ChessPiece rook = (ChessPiece) board.removePiece(targetT);
                board.placePiece(rook, sourceT);
                rook.increaseMoveCount();
            }
        } else {
            // #specialmove castling kingside rook
            if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() - 3);
                Position targetT = new Position(source.getRow(), source.getColumn() - 1);
                ChessPiece rook = (ChessPiece) board.removePiece(targetT);
                board.placePiece(rook, sourceT);
                rook.decreaseMoveCout();
            }

            // #specialmove castling queenside rook
            if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
                Position sourceT = new Position(source.getRow(), source.getColumn() + 4);
                Position targetT = new Position(source.getRow(), source.getColumn() + 1);
                ChessPiece rook = (ChessPiece) board.removePiece(targetT);
                board.placePiece(rook, sourceT);
                rook.decreaseMoveCout();
            }
        }

        // en passant special move
        if (p instanceof Pawn && source.getColumn() != target.getColumn() && capturatedPiece == enPassantVulnerable) {
            ChessPiece pawn = (ChessPiece) board.removePiece(target);
            Position pawnPosition;
            if (p.getColor() == Color.WHITE)
                pawnPosition = new Position(3, target.getColumn());
            else
                pawnPosition = new Position(4, target.getColumn());
            board.placePiece(pawn, pawnPosition);
        }
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position))
            throw new ChessException("There is no piece there!");
        if (!board.piece(position).isThereAnyPossibleMove())
            throw new ChessException("No move for this piece");
        if (currentPlayer != ((ChessPiece) board.piece(position)).getColor())
            throw new ChessException("Not your piece!");
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target))
            throw new ChessException("The chosen piece can't move to target position");
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
                .collect(Collectors.toList());
        for (Piece p : list) {
            if (p instanceof King)
                return (ChessPiece) p;
        }
        throw new IllegalStateException("there is no king that on " + color.toString() + " color!");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()])
                return true;
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color))
            return false;
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
                .collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat.length; j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece) p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturPiece);
                        if (!testCheck)
                            return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialState() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK, this));
        placeNewPiece('e', 8, new Queen(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

    }

}

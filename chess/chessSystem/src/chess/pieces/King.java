package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    public King(Board board, Color color) {
        super(board, color);
       
    }

    private boolean canMove(Position position){
        ChessPiece p = (ChessPiece)getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }



    @Override
    public String toString(){
        return "K";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        
        Position p  = new Position(0, 0);
        //up
        p.setValues(position.getRow()-1,position.getColumn());
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        //down
        p.setValues(position.getRow()+1,position.getColumn());
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        //left
        p.setValues(position.getRow(),position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        //rigt
        p.setValues(position.getRow(),position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        
        //down left
        p.setValues(position.getRow()+1,position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        //down right
        p.setValues(position.getRow()+1,position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        //up left
        p.setValues(position.getRow()-1,position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        //up right
        p.setValues(position.getRow()-1,position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getColumn()] = true ;
        
        return mat;
    }
    
}

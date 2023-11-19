package boardgame;

public abstract class Piece {
    protected Position position;
    private Board board;

    public Piece(Board board) {
        position = null;
        this.board = board;
    }

    protected Board getBoard() {
        return board;
    }
    
    public abstract boolean[][] possibleMoves();

    public boolean possibleMove(Position possition) {
        return possibleMoves()[position.getRow()][possition.getColum()];
    }

    public boolean isThereAnyPossibleMove(){
        boolean[][] mat = possibleMoves();
        for ( int i = 0; i<mat.length; i++){
            for (int j = 0; j<mat[i].length; j++){
                if(mat[i][j]) return true;
            }
        }
        return false;
    }

}

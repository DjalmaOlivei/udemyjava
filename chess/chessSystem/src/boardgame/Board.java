package boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1) throw new BoardException("columns and rows cant be 0 or less!");
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColums() {
        return columns;
    }

    public void setColums(int columns) {
        this.columns = columns;
    }

    public Piece piece(int row, int column) {
        if(!positionExists(row, column)) throw new BoardException("Position does not exist!");
        return pieces[row][column];
    }

    public Piece piece(Position position) {
        if(!positionExists(position)) throw new BoardException("Position does not exist!");
        return pieces[position.getRow()][position.getColum()];
    }

    public void placePiece(Piece piece, Position position){
        if(thereIsAPiece(position)) throw new BoardException("There is already a piece on position "+ position +"!");
        pieces[position.getRow()][position.getColum()] = piece;
        piece.position = position;
    }

    public boolean positionExists(Position position){
        return positionExists(position.getRow(), position.getColum());
    }

    public boolean positionExists(int row, int cloum){
        return (row>=0 && row < rows) && (cloum >=0 && cloum < columns);
    }

    public boolean thereIsAPiece(Position position){
        if(!positionExists(position)) throw new BoardException("Position does not exist!");
        return piece(position) != null;
    }

}

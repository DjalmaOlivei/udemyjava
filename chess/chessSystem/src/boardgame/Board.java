package boardgame;

public class Board {

    private int rows;
    private int colums;
    private Piece[][] pieces;

    public Board(int rows, int colums) {
        if (rows < 1 || colums < 1) throw new BoardException("colums and rows cant be 0 or less!");
        this.rows = rows;
        this.colums = colums;
        pieces = new Piece[rows][colums];
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColums() {
        return colums;
    }

    public void setColums(int colums) {
        this.colums = colums;
    }

    public Piece piece(int row, int colum) {
        if(!positionExists(row, colum)) throw new BoardException("Position does not exist!");
        return pieces[row][colum];
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
        return (row>=0 && row < rows) && (cloum >=0 && cloum < colums);
    }

    public boolean thereIsAPiece(Position position){
        if(!positionExists(position)) throw new BoardException("Position does not exist!");
        return piece(position) != null;
    }

}

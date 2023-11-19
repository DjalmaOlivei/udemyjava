package application;


import chess.ChessPiece;

public class UI {

    public static void printBoard(ChessPiece[][] pieces){
        for(int i = 0; i<pieces.length; i++){
            System.out.print((i+1)+" ");
            for(int j = 0; j<pieces[i].length;j++){
                printPiece(pieces[i][j]);
            }
            System.out.println();
        }
        System.out.print("  ");
        for(int i = 97; i<pieces[0].length+97; i++){
            System.out.print((char) i + " ");
        }
        /*for(ChessPiece[] i: pieces){

            for(ChessPiece j : i){

            }

        }*/

    }

    private static void printPiece(ChessPiece piece){
        if (piece == null){
            System.out.print("-"+" ");
        }
        else{
            System.out.print(piece+" ");
        }
    }
    
}
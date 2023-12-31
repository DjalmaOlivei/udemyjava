package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String args[]) {

		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		Scanner sc = new Scanner(System.in);

		while (!chessMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);

				boolean[][] possibleMoves  = chessMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves);

				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);

				// moves and captures pieces if there´s any
				ChessPiece capturChessPiece = chessMatch.performChessMove(source, target);
				if (capturChessPiece != null) captured.add(capturChessPiece);

				if (chessMatch.getPromoted() != null) {
					System.out.print("Chose a promotion (b/n/r/q): ");
					String type = sc.nextLine().toLowerCase();
					while (!type.equals("b") && !type.equals("n") && !type.equals("r") && !type.equals("q")) {
						System.out.print("Chose a promotion (b/n/r/q): ");
						type = sc.nextLine().toLowerCase();
					}
					chessMatch.replacePromotedPiece(type);
				}

			 } catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);

	}

}

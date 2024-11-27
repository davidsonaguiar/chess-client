package com.fnrc;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnrc.chess.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Connection connection = new Connection();

        String message = "";
        while (message != null && !message.equals("macth")) message = connection.receiveMessage();;

        ObjectMapper mapper = new ObjectMapper();

        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();

        while(!chessMatch.getCheckMate()) {
            try {
                Color color = connection.getColor();

                System.out.println("color turn: " + chessMatch.getCurrentPlayer());
                System.out.println("color: " + color);

                UI.clearScreen();
                UI.printMatch(chessMatch, captured);

                if (chessMatch.getCurrentPlayer().getColor().equals(color)) {
                    System.out.println();
                    System.out.print("Selecione a peça que deseja mover: ");
                    ChessPosition source = UI.readChessPosition(connection);

                    boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                    UI.clearScreen();
                    UI.printBoard(chessMatch.getPieces(), possibleMoves);

                    System.out.println();
                    System.out.println("Informe a posição alvo: ");
                    UI.readChessPosition(connection);
                    ChessPosition target = UI.readChessPosition(connection);

                    ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                    if (capturedPiece != null)  captured.add(capturedPiece);

                    if (chessMatch.getPromoted() != null) {
                        System.out.print("Enter piece for promotion (B/N/R/Q): ");
                        String type = connection.receiveMessage().toUpperCase();
                        while (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
                            System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
                            type = connection.receiveMessage().toUpperCase();
                        }
                        chessMatch.replacePromotedPiece(type);
                    }

                    String sourceJson = mapper.writeValueAsString(source);
                    String targetJson = mapper.writeValueAsString(target);
                    connection.sendMessage(sourceJson);
                    connection.sendMessage(targetJson);
                }
                else {
                    String sourceJson = connection.receiveMessage();
                    String targetJson = connection.receiveMessage();

                    while(sourceJson == null || targetJson == null) {
                        sourceJson = connection.receiveMessage();
                        targetJson = connection.receiveMessage();
                    }

                    ChessPosition source = mapper.readValue(sourceJson, ChessPosition.class);
                    ChessPosition target = mapper.readValue(targetJson, ChessPosition.class);

                    ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                    if (capturedPiece != null)  captured.add(capturedPiece);

                    if (chessMatch.getPromoted() != null) {
                        System.out.print("Enter piece for promotion (B/N/R/Q): ");
                        String type = connection.receiveMessage().toUpperCase();
                        while (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
                            System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
                            type = connection.receiveMessage().toUpperCase();
                        }
                        chessMatch.replacePromotedPiece(type);
                    }
                }
            }
            catch (ChessException e) {
                System.out.println(e.getMessage());
            }
            catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            } catch (JsonProcessingException e) {
                System.out.println("Erro de serialização");
            }

            UI.clearScreen();
            UI.printMatch(chessMatch, captured);
        }
        connection.close();
    }
}

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
        Scanner scanner = new Scanner(System.in);
        Connection connection = new Connection();
        Object turnLock = new Object();

        String message = connection.receiveMessage();
        while (!message.equals("match")) {
            message = connection.receiveMessage();
        }

        System.out.println("Match started!");
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();

        while(!chessMatch.getCheckMate()) {
            try {
                Color color = connection.getColor();
                UI.clearScreen();
                UI.printMatch(chessMatch, captured, color);

                Thread sendMovimment = new Thread(() -> {
                   if(color.getColor().equals(chessMatch.getCurrentPlayer().getColor())) {
                        System.out.print("Informe a posição de origem: ");
                        ChessPosition source = UI.readChessPosition(scanner);

                        boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                        UI.clearScreen();
                        UI.printBoard(chessMatch.getPieces(), possibleMoves);

                        System.out.print("Informe a posição alvo: ");
                        ChessPosition target = UI.readChessPosition(scanner);

                        ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                        if(capturedPiece != null) captured.add(capturedPiece);

                        if(chessMatch.getPromoted() != null) {
                            System.out.print("Enter piece for promotion (B/N/R/Q): ");
                            String type = scanner.nextLine().toUpperCase();
                            while(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
                                System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
                                type = scanner.nextLine().toUpperCase();
                            }
                            chessMatch.replacePromotedPiece(type);
                        }

                        connection.sendMessage(source.toString() + " " + target.toString());
                   }
                });

                Thread receiveMovimment = new Thread(() -> {
                    if(!color.getColor().equals(chessMatch.getCurrentPlayer().getColor())) {
                        System.out.println("Aguardando a jogada do oponente...");

                        String[] movimment = connection.receiveMessage().split(" ");

                        String sourceString = movimment[0];
                        char sourceCol = sourceString.charAt(0);
                        int sourceRow = Integer.parseInt(sourceString.substring(1));

                        String targetString = movimment[1];
                        char targetCol = targetString.charAt(0);
                        int targetRow = Integer.parseInt(targetString.substring(1));

                        ChessPosition source = new ChessPosition(sourceCol, sourceRow);
                        ChessPosition target = new ChessPosition(targetCol, targetRow);

                        ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
                        if(capturedPiece != null) captured.add(capturedPiece);

                        if(chessMatch.getPromoted() != null) {
                            System.out.println("Oponente promoveu uma peça.");
                        }
                    }
                });

                sendMovimment.start();
                receiveMovimment.start();

                sendMovimment.join();
                receiveMovimment.join();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

//        while (!chessMatch.getCheckMate()) {
//            try {
//                Color color = connection.getColor();
//
//                    UI.clearScreen();
//                    UI.printMatch(chessMatch, captured, color);
//
//                    if (color.getColor().equals(chessMatch.getCurrentPlayer().getColor())) {
//                        System.out.print("Informe a posição de origem: ");
//                        ChessPosition source = UI.readChessPosition(scanner);
//
//                        boolean[][] possibleMoves = chessMatch.possibleMoves(source);
//                        UI.clearScreen();
//                        UI.printBoard(chessMatch.getPieces(), possibleMoves);
//
//                        System.out.print("Informe a posição alvo: ");
//                        ChessPosition target = UI.readChessPosition(scanner);
//
//
//                        ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
//                        if (capturedPiece != null) captured.add(capturedPiece);
//
//                        if (chessMatch.getPromoted() != null) {
//                            System.out.print("Enter piece for promotion (B/N/R/Q): ");
//                            String type = scanner.nextLine().toUpperCase();
//                            while (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
//                                System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
//                                type = scanner.nextLine().toUpperCase();
//                            }
//                            chessMatch.replacePromotedPiece(type);
//                        }
//
//
//                        ObjectMapper mapper = new ObjectMapper();
//                        connection.sendMessage(mapper.writeValueAsString(source));
//                        connection.sendMessage(mapper.writeValueAsString(target));
//
//
//                    } else {
//                        System.out.println("Aguardando a jogada do oponente...");
//                        ObjectMapper mapper = new ObjectMapper();
//
//                        System.out.println("chegou aqui");
//
//                        String sourceJson = connection.receiveMessage();
//                        String targetJson = connection.receiveMessage();
//
//                        while (sourceJson == null || targetJson == null) {
//                            sourceJson = connection.receiveMessage();
//                            targetJson = connection.receiveMessage();
//                        }
//
//                        ChessPosition source = mapper.readValue(sourceJson, ChessPosition.class);
//                        ChessPosition target = mapper.readValue(targetJson, ChessPosition.class);
//
//                        ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
//                        if (capturedPiece != null) captured.add(capturedPiece);
//
//                        if (chessMatch.getPromoted() != null) {
//                            System.out.println("Oponente promoveu uma peça.");
//                        }
//
//                        turnLock.notify();
//                        turnLock.wait();
//                    }
//                }
//            } catch (ChessException e) {
//                System.out.println(e.getMessage());
//            } catch (InputMismatchException e) {
//                System.out.println(e.getMessage());
//            } catch (InterruptedException | JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//
//            UI.clearScreen();
//            UI.printMatch(chessMatch, captured, connection.getColor());
//        }

        connection.close();
    }
}

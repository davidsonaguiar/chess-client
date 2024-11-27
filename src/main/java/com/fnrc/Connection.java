package com.fnrc;

import com.fnrc.chess.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
    private final String HOST = "localhost";
    private final int PORT = 8080;
    private final Socket client;
    private final BufferedReader serverInput;
    private final PrintWriter serverOutput;
    private final Color color;

    public Connection() {
        try {
            this.client = new Socket(HOST, PORT);
            this.serverInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.serverOutput = new PrintWriter(client.getOutputStream(), true);

            String color = this.receiveMessage();
            this.color = Color.getColor(color);

            System.out.println("Você jogará do lado " + this.color.getColor() + ".");
            if(this.color.equals(Color.WHITE)) System.out.println("Aguardando um oponente.");

            String match = this.receiveMessage();
            if(match.equals("match")) System.out.println("Partida iniciada!");
        }
        catch (IOException e) {
            throw new RuntimeException("Erro ao conectar com o servidor: " + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Cor inválida: " + e.getMessage());
        }
    }

    public Socket getClient() {
        return this.client;
    }

    public BufferedReader getServerInput() {
        return this.serverInput;
    }

    public PrintWriter getServerOutput() {
        return this.serverOutput;
    }

    public Color getColor() {
        return this.color;
    }

    public void close() {
        try {
            this.client.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        this.serverOutput.println(message);
    }

    public String receiveMessage() {
        try {
            return this.serverInput.readLine();
        } catch (IOException e) {
            System.out.println("Erro ao receber mensagem: " + e.getMessage());
        }
        return null;
    }


}

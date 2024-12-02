package com.fnrc;

import com.fnrc.chess.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
    private final Socket client;
    private final BufferedReader serverInput;
    private final PrintWriter serverOutput;
    private final Color color;

    public Connection(String host, Integer port) {
        try {
            this.client = new Socket(host, port);
            this.serverInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.serverOutput = new PrintWriter(client.getOutputStream(), true);

            String color = this.receiveMessage();
            System.out.println(color);
            this.color = Color.getColor(color);

            System.out.println("Você jogará do lado " + this.color.getColor() + ".");
            if(this.color.equals(Color.WHITE)) System.out.println("Aguardando um oponente.");
            else System.out.println("Oponente conectado.");
        }
        catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
            throw new RuntimeException("Erro ao conectar ao servidor: " + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Cor inválida: " + e.getMessage());
        }
    }

    public Color getColor() {
        return this.color;
    }

    public Boolean isConnected() {
        return this.client.isConnected();
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

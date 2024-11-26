package com.fnrc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Main {
    public static void main(String[] args) {
        Socket server = null;
        try {
            String HOST = "localhost";
            int PORT = 8080;

            server = new Socket(HOST, PORT);
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(server.getInputStream()));
            PrintWriter serverOutput = new PrintWriter(server.getOutputStream(), true);

            Thread output = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverInput.readLine()) != null) {
                        System.out.println("Servidor: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Conexão com o servidor foi encerrada.");
                }
            });

            output.start();

            Thread input = new Thread(() -> {
                try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
                    String userMessage;
                    while ((userMessage = userInput.readLine()) != null) {
                        serverOutput.println(userMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao enviar mensagem: " + e.getMessage());
                }
            });

            input.start();

            try {
                output.join();
                input.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Error 3: " + e.getMessage());
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
                System.out.println("Closing connection");
            } catch (IOException e) {
                System.out.println("Error 4: " + e.getMessage());
            }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.iesharia.ipcalcserver;

/**
 *
 * @author navarro
 */
import java.io.*;
import java.net.*;

public class IPCalcClient {

    private final String host;
    private final int port;

    public IPCalcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (
                Socket socket = new Socket(host, port); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Conectado al servidor. Ingrese las IP a calcular (ejemplo: 192.168.1.10/24:192.168.1.11/24):");
            System.out.println("Escriba la IP '0.0.0.0/0' para salir");
            while (true) {
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    System.out.println(userInput);
                    out.println(userInput);
                    String response;
                    if ("EXIT".equalsIgnoreCase(in.readLine())) {
                        break;
                    }
                    while ((response = in.readLine()) != null) {
                        if ("END_OF_RESPONSE".equals(response)) {
                            break;
                        }
                        System.out.println(response);
                    }
                    System.out.println();
                }
                System.out.println("Ingrese las IP a calcular (ejemplo: 192.168.1.10/24:192.168.1.11/24):");
                System.out.println("Escriba la IP '0.0.0.0/0' para salir");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        IPCalcClient client = new IPCalcClient("localhost", 5000);
        client.start();
    }
}

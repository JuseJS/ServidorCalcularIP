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

public class IPCalcServer {

    private ServerSocket serverSocket;
    private final int port;

    public IPCalcServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor de calculo de IP, iniciado en el puerto: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                IPCalcHandler mathHandler = new IPCalcHandler(clientSocket);
                new Thread(mathHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class IPCalcHandler implements Runnable {

        private final Socket clientSocket;

        public IPCalcHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // Paramos el servidor al recibir la IP: 0.0.0.0/0
                    if ("0.0.0.0/0".equalsIgnoreCase(inputLine)) {
                        try (clientSocket) {
                            System.out.println("Se recibio la IP: 0.0.0.0/0, cerrando conexión con cliente: " + clientSocket.getInetAddress());
                            out.println("EXIT");
                        }
                        break;
                    }

                    System.out.println("IPs recibidas: " + inputLine);
                    try {
                        String result = evaluateIP(inputLine);
                        out.println(result);
                    } catch (Exception e) {
                        String errorMessage;
                        errorMessage = """
                           Formato incorrecto.
                           Debes enviar las IP usando el siguiente formato:
                           192.168.1.20/24:192.168.1.30/24""";
                        out.println(errorMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String evaluateIP(String clientMSG) {
            clientMSG = clientMSG.replaceAll("\\s+", "");
            String[] ips;

            if (clientMSG.contains(":")) {
                ips = clientMSG.split("\\:");
                if (ips.length == 2) {
                    // Iniciamos las variables necesarias
                    String[] firstIPSplit;
                    String[] secondIPSplit;
                    String firstIP;
                    int firstIPMask;
                    String secondIP;
                    int secondIPMask;
                    String firstIPNetworkIP;
                    String secondIPNetworkIP;

                    // Separamos correctamente las IP de sus mascaras
                    firstIPSplit = ips[0].split("\\/");
                    secondIPSplit = ips[1].split("\\/");

                    // Convertimos las variables para un mejor uso
                    firstIP = firstIPSplit[0];
                    firstIPMask = Integer.parseInt(firstIPSplit[1]);

                    secondIP = secondIPSplit[0];
                    secondIPMask = Integer.parseInt(secondIPSplit[1]);

                    // Llamamos a la clase IPv4Address con cada IP
                    IPv4Address firstAddress = new IPv4Address(firstIP, firstIPMask);
                    IPv4Address secondAddress = new IPv4Address(secondIP, secondIPMask);

                    // Obtenemos las direcciones de red de ambas IP
                    firstIPNetworkIP = firstAddress.getDecimalNetwork();
                    secondIPNetworkIP = secondAddress.getDecimalNetwork();

                    if (firstIPNetworkIP.equals(secondIPNetworkIP)) {
                        return "Las IP que introdujiste:\n"
                                + "Primera IP: " + firstIP
                                + "\nSegunda IP: " + secondIP
                                + "\nPertenecen ambas a la red: " + firstIPNetworkIP;
                    }
                    return "Las IP que introdujiste, pertenecen a redes distintas:\n"
                            + "Primera IP: " + firstIP + " - Dirección de red: " + firstIPNetworkIP
                            + "\nSegunda IP: " + secondIP + " - Dirección de red: " + secondIPNetworkIP;
                }
            }

            String errorMessage;
            errorMessage = """
                           Formato incorrecto.
                           Debes enviar las IP usando el siguiente formato:
                           192.168.1.20/24:192.168.1.30/24""";

            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void main(String[] args) {
        IPCalcServer server = new IPCalcServer(5000);
        server.start();
    }
}

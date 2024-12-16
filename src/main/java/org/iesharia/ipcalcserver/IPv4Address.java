package org.iesharia.ipcalcserver;

/**
 * Clase que representa una dirección IPv4 con funcionalidad para cálculos de
 * red.
 */
public class IPv4Address {

    private String IPAddress;
    private int mask;

    private String binaryAddress;
    private String binaryMask;
    private String decimalMask;

    private String binaryNetwork;
    private String decimalNetwork;

    private String binaryBroadcast;
    private String decimalBroadcast;

    private String binaryFirstHost;
    private String decimalFirstHost;

    private String binaryLastHost;
    private String decimalLastHost;

    private double maxHosts;

    private String type;

    // Constructor
    public IPv4Address(String IPAddress, int mask) {
        this.IPAddress = IPAddress;
        this.mask = mask;

        // Convertir la IP a binario
        this.binaryAddress = IPAddressToBinary();

        // Convertir la máscara a binario
        this.binaryMask = "1".repeat(mask) + "0".repeat(32 - mask);
        this.decimalMask = binaryToDecimal(binaryMask);

        // Calcular la dirección de red
        this.binaryNetwork = binaryAddress.substring(0, mask) + "0".repeat(32 - mask);
        this.decimalNetwork = binaryToDecimal(binaryNetwork);

        // Calcular la dirección de broadcast
        this.binaryBroadcast = binaryAddress.substring(0, mask) + "1".repeat(32 - mask);
        this.decimalBroadcast = binaryToDecimal(binaryBroadcast);

        // Calcular el primer host
        this.binaryFirstHost = binaryNetwork.substring(0, 31) + "1";
        this.decimalFirstHost = binaryToDecimal(binaryFirstHost);

        // Calcular el último host
        this.binaryLastHost = binaryBroadcast.substring(0, 31) + "0";
        this.decimalLastHost = binaryToDecimal(binaryLastHost);

        // Calcular el máximo número de hosts
        this.maxHosts = Math.pow(2, 32 - mask) - 2;

        // Determinar si la IP es privada o pública
        this.type = addressType();
    }

    // Métodos getter
    public String getIPAddress() {
        return IPAddress;
    }

    public String getBinaryAddress() {
        return binaryNotation(binaryAddress);
    }

    public String getBinaryMask() {
        return binaryNotation(binaryMask);
    }

    public String getDecimalMask() {
        return decimalMask;
    }

    public String getDecimalNetwork() {
        return decimalNetwork;
    }

    public String getDecimalBroadcast() {
        return decimalBroadcast;
    }

    public String getDecimalFirstHost() {
        return decimalFirstHost;
    }

    public String getDecimalLastHost() {
        return decimalLastHost;
    }

    public double getMaxHosts() {
        return maxHosts;
    }

    public String getType() {
        return type;
    }

    private String IPAddressToBinary() {
        StringBuilder result = new StringBuilder();
        String[] octets = IPAddress.split("\\.");
        for (String octet : octets) {
            result.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(octet))).replace(" ", "0"));
        }
        return result.toString();
    }

    private String binaryNotation(String binary) {
        return binary.substring(0, 8) + "."
                + binary.substring(8, 16) + "."
                + binary.substring(16, 24) + "."
                + binary.substring(24, 32);
    }

    private String binaryToDecimal(String binary) {
        return Integer.parseInt(binary.substring(0, 8), 2) + "."
                + Integer.parseInt(binary.substring(8, 16), 2) + "."
                + Integer.parseInt(binary.substring(16, 24), 2) + "."
                + Integer.parseInt(binary.substring(24, 32), 2);
    }

    private String addressType() {
        String[] octets = IPAddress.split("\\.");
        int firstOctet = Integer.parseInt(octets[0]);
        if ((firstOctet == 10)
                || (firstOctet == 172 && Integer.parseInt(octets[1]) >= 16 && Integer.parseInt(octets[1]) <= 31)
                || (firstOctet == 192 && Integer.parseInt(octets[1]) == 168)) {
            return "Privada";
        } else {
            return "Pública";
        }
    }
}

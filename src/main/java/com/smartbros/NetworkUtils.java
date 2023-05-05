package com.smartbros;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    public static int getAvailablePort() throws Exception {
        for (int port = 9000; port < 65535; port++) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("0.0.0.0", port), 1);
            } catch (IOException e) {
                return port;
            }
        }
        throw new Exception("No available port found");
    }

    public static List<String> getExternalIps() throws SocketException {
        List<String> externalIPs = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                String ipAddress = inetAddress.getHostAddress();

                if (ipAddress.contains(".") && !ipAddress.startsWith("127.") && !ipAddress.startsWith("0.")) {
                    externalIPs.add(ipAddress);
                }
            }
        }
        return externalIPs;
    }

    public static void main(String[] args) {
        try {
            int availablePort = getAvailablePort();
            System.out.println("Available port: " + availablePort);

            List<String> externalIPs = getExternalIps();
            System.out.println("External IPs: " + externalIPs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

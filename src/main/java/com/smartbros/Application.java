package com.smartbros;

import io.micronaut.runtime.Micronaut;

import java.io.IOException;
import java.util.Set;

public class Application {

    public static void main(String[] args) throws IOException {
        int port = NetworkUtils.getAvailablePort();
        Set<String> externalIps = NetworkUtils.getExternalIps();

        System.out.println("Server is listening on the following IP addresses:");
        for (String ip : externalIps) {
            System.out.println("- " + ip + ":" + port);
        }

        Micronaut.run(Application.class, new String[]{"--server.port=" + port});

    }
}
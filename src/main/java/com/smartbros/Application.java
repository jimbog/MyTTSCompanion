package com.smartbros;

import io.micronaut.runtime.Micronaut;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Application {

    public static void main(String[] args) throws Exception {
        int port = NetworkUtils.getAvailablePort();
        List<String> externalIps = NetworkUtils.getExternalIps();

        System.out.println("Server is listening on the following IP addresses:");
        for (String ip : externalIps) {
            System.out.println("- " + ip + ":" + port);
        }

        Micronaut.run(Application.class, new String[]{"-Dmicronaut.server.port=" + port});

    }
}

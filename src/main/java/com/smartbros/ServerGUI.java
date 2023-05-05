package com.smartbros;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.micronaut.runtime.Micronaut;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;

public class ServerGUI extends Application {
    public static void main(String[] args) throws Exception {
        int port = NetworkUtils.getAvailablePort();
        List<String> externalIps = NetworkUtils.getExternalIps();

        System.out.println("Server is listening on the following IP addresses:");
        for (String ip : externalIps) {
            System.out.println("- " + ip + ":" + port);
        }

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        redirectSystemOutputTo(textArea);

        int port = NetworkUtils.getAvailablePort();
        List<String> externalIps = NetworkUtils.getExternalIps();

        ObjectMapper objectMapper = new ObjectMapper();
        String ipAddressesStr = "";
        try {
            ipAddressesStr = objectMapper.writeValueAsString(externalIps);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println(ipAddressesStr);
        BufferedImage qrCodeImage = generateQRCode(ipAddressesStr, 250, 250);
        ImageView qrCodeImageView = new ImageView(SwingFXUtils.toFXImage(qrCodeImage, null));

        Button stopButton = new Button("Stop Server");
        stopButton.setOnAction(event -> {
//            EchoServer.stopServer();
            System.exit(0);
        });

        VBox root = new VBox(10, textArea, qrCodeImageView, stopButton);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 500, 500);

        primaryStage.setOnCloseRequest(event -> {
//            EchoServer.stopServer();
            System.exit(0);
        });
        primaryStage.setTitle("MyTTS Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            System.out.println(port);
            Micronaut.run(Application.class, new String[]{"-Dmicronaut.server.port=" + port});
        }).start();
    }

    private void redirectSystemOutputTo(TextArea textArea) {
        PrintStream printStream = new PrintStream(new TextAreaOutputStream(textArea));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private static class TextAreaOutputStream extends OutputStream {
        private TextArea textArea;

        public TextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            textArea.appendText(String.valueOf((char) b));
        }
    }
    private static BufferedImage generateQRCode(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            throw new RuntimeException("Error generating QR code: " + e.getMessage(), e);
        }

        BufferedImage qrCodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return qrCodeImage;
    }

}


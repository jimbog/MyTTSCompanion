package com.smartbros;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Optional;

@Controller("/")
public class KeyboardController {

    @Get(value = "/typeit", produces = MediaType.APPLICATION_JSON)
    public String typeIt(@QueryValue("text") Optional<String> text) {
        System.out.println("From micronaut, printing now");
        if (text.isPresent()) {
            try {
                Robot robot = new Robot();
                String inputText = text.get();

                for (char c : inputText.toCharArray()) {
                    int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                    robot.keyPress(keyCode);
                    robot.keyRelease(keyCode);
                }

                robot.keyPress(KeyEvent.VK_SPACE);
                robot.keyRelease(KeyEvent.VK_SPACE);

                return "{\"status\": \"success\"}";
            } catch (AWTException e) {
                e.printStackTrace();
                return "{\"status\": \"error\", \"message\": \"Failed to create Robot\"}";
            }
        } else {
            return "{\"status\": \"error\", \"message\": \"Invalid JSON\"}";
        }
    }

    @Get(value = "/test", produces = MediaType.APPLICATION_JSON)
    public String testIt() {
        return "{\"status\": \"success\", \"message\": \"connected\"}";
    }
}

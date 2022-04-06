/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers;

import com.raxa.bot.utilities.OverlayWSClass;
import java.io.StringReader;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Raxa
 */
public class OverlayHandler {

    OverlayWSClass WS;

    public OverlayHandler() {

    }

    public void start() {
        try {
            WS = new OverlayWSClass(new URI("wss://localhost:8081"));
            WS.addMessageHandler(new OverlayWSClass.MessageHandler() {
                public void handleMessage(String message) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        JsonObjectBuilder strBuilder = Json.createObjectBuilder();
        strBuilder.add("update", message);
        JsonObject strJsonObject = strBuilder.build();
        WS.sendJSON(strJsonObject);
    }

}

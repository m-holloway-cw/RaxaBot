/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.utilities;

import java.net.URI;
import javax.json.JsonObject;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 *
 * @author Raxa
 */
@ClientEndpoint
public class OverlayWSClass {


    private final URI uri;


    Session userSession = null;
    private MessageHandler messageHandler;

    
    public OverlayWSClass(URI uri) {

        this.uri = uri;
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
           
        } catch (Exception e) {
            e.printStackTrace();
            
            throw new RuntimeException(e);
        }
    }


    /**
     * Callback that is called when we open a connect to Twitch.
     *
     * @param {ServerHandshake} handshakedata
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback that is called when the connection with Twitch is lost.
     *
     * @param {int} code
     * @param {String} reason
     * @param {boolean} remote
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("On Close: code: " + reason + " remote: " + userSession.getId());
        //reconnect here
        //reconnect();
    }


    /**
     * Callback that is called when we get a message from Twitch.
     *
     * @param {String} message
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        if(message.equals("success")){
            
        }
    }

    void parse(String msg) {
        System.out.println("PARSE INCOMING: " + msg);
        if (msg.contains("USERNOTICE #")) {
            if (msg.contains("Prime")) {
                System.out.println("PRIME Message:" + msg);
            } else {
                System.out.println("Notification Message:" + msg);
            }
        }
    }

    /*
     * Method that handles reconnecting with Twitch.
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void reconnect() {

    }

    public void addMessageHandler(MessageHandler handler){
        this.messageHandler = handler;
    }
    
    public void sendJSON(JsonObject json){
        this.userSession.getAsyncRemote().sendObject(json);
    }
    
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public static interface MessageHandler {

        public void handleMessage(String message);
    }

}

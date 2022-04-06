/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.rpg;

import com.raxa.bot.Start;
import com.raxa.gui.handlers.DashboardController;
import com.raxa.store.Datastore;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import javafx.application.Platform;

/**
 *
 * @author Raxa
 */
public class rpgHandler {

    private final Datastore store;
    private HashMap<Integer, Player> MAP;
    private boolean found;
    private Player p;
    // possible use with UI to write names to fxml list faster
    //private final List<Player> currPlayers = new java.util.ArrayList<>();;

    public rpgHandler(Datastore store) {
        this.store = store;
    }

    public void start() {
        this.MAP = getMapFromFile();

        //dummy test values
        //addUser(0, new Player("Raxa", "God"));
        //addUser(1, new Player("RaxaBot", "Slave"));
    }

    public synchronized HashMap<Integer, Player> getMap() {
        return this.MAP;
    }

    public Player getPlayer(int userID) {
        p = null;
        MAP.entrySet().forEach((i) -> {
            if (i.getKey() == userID) {
                p = i.getValue();
            }
        });
        return p;
    }

    public synchronized void writeMap(HashMap<Integer, Player> map) {
        try {
            FileOutputStream fout = new FileOutputStream("RPG.map");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(map);
        } catch (IOException ie) {
            sendEvent("Error occurred trying to write rpg map to file");
        }
    }

    public synchronized HashMap<Integer, Player> getMapFromFile() {
        try {
            Path location = Paths.get("");
            Path lResolved = location.resolve("RPG.map");
            FileInputStream fin = new FileInputStream(lResolved.toString());
            ObjectInputStream ois = new ObjectInputStream(fin);
            HashMap<Integer, Player> m1 = (HashMap<Integer, Player>) ois.readObject();
            m1.entrySet().forEach((m) -> {
                //currPlayers.add(m.getValue());
                System.out.println("Player added id: " + m.getKey() + ", name: " + m.getValue().getName() + ", type: " + m.getValue().getType());
            });
            System.out.println("Successfully set rpg map from file");
            sendEvent("Successfully set rpg map from file");
            return m1;
        } catch (IOException ie) {
            sendEvent("RPG file not found");
            ie.printStackTrace();
        } catch (Exception e) {
            sendEvent("Error occurred trying to get existing RPG file");
        }
        return null;
    }

    public boolean userExists(int userID) {
        found = false;
        MAP.entrySet().forEach((i) -> {
            if (i.getKey() == userID) {
                found = true;
            }
        });
        return found;
    }

    public boolean checkType(String type) {
        //TODO add enum or similar to check for valid types
        return false;
    }

    public boolean addUser(int id, Player player) {
        try {
            MAP.put(id, player);
            writeMap(MAP);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String handleNewUser(int userID, String username) {
        //Create the player object to store for use with other commands
        addUser(userID, new Player(username, "empty"));
        //build return info message
        //TODO remove brackets on some commands
        String reply = "@" + username + ", to begin your adventure, type !setType [type]."
                + "Use !type to see a list of available types."
                + " You will then be automatically sent on quests to gain EXP and items!";
        return reply;
    }

    private void sendMessage(final String msg) {
        DashboardController.wIRC.sendMessage(msg, true);
    }

    private void sendEvent(final String msg) {
        String event = msg;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Start.bot.getStore().getEventList().addList(event);
            }
        });
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.gui.handlers;

import com.raxa.bot.Start;
import com.raxa.bot.rpg.Player;
import com.raxa.bot.rpg.rpgHandler;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Raxa
 */
public class RpgController implements Initializable {

    @FXML
    TextField nameText;

    @FXML
    TextField typeText;

    @FXML
    TextField idText;

    ScreensController myController = new ScreensController();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void goDash() {
        //setDimensions();
        myController.loadScreen(Start.dashboardID, Start.dashboardFile);
        myController.setScreen(Start.dashboardID);
        myController.setId("dashboard");
        myController.show(myController);
    }

    @FXML
    public void addPlayer() {
        String name;
        String type;
        int id;
        nameText.selectAll();
        nameText.copy();
        name = nameText.getText();
        typeText.selectAll();
        typeText.copy();
        type = typeText.getText();
        Player player = new Player(name, type);
        idText.selectAll();
        idText.copy();
        id = Integer.parseInt(idText.getText());
        player.rollStats(type);
        if (Start.rpg.addUser(id, player)) {
            sendEvent("Added " + name + " to rpg");
            System.out.println("Added " + name + " to rpg");
        } else {
            sendEvent("Failed to add " + name + " to rpg");
            System.out.println("Failed to add " + name + " to rpg");
        }
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

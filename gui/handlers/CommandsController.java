/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.gui.handlers;

import com.raxa.bot.Start;
import com.raxa.bot.utilities.ConfigParameters;
import com.raxa.store.Datastore;
import com.raxa.store.XmlDatastore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * FXML Controller class
 *
 * @author Raxa
 */
public class CommandsController implements ControlledScreen {

    private Datastore store;
    final ConfigParameters configuration = new ConfigParameters();

    ScreensController myController = new ScreensController();

    private String commandNameTemp;

    @FXML
    private Button commands;

    @FXML
    private Button filter;

    @FXML
    ScrollPane commandList;

    @FXML
    Pane commandInfo;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        try {
            try {
                final ConfigParameters.Elements elements = configuration.parseConfiguration("./raxabot.xml");
                //final ConfigParameters.Elements elements = configuration.parseConfiguration("./jyvbot.xml");
                store = new XmlDatastore(elements);
            } catch (Exception e) {
                e.printStackTrace();
            }

            VBox vB = new VBox();
            vB.setAlignment(Pos.CENTER_LEFT);
            vB.setPrefWidth(197);
            vB.setPrefHeight(323);
            String[] commandName = new String[this.store.getCommands().size()];
            for (int i = 0; i < this.store.getCommands().size(); i++) {
                final ConfigParameters.Command command = this.store.getCommands().get(i);
                commandName[i] = command.name;
            }
            for (int i = 0; i < commandName.length; i++) {
                String cI = commandName[i];
                Button cN = new Button(cI);
                cN.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        showCommandInfo(cI);
                    }
                });
                vB.getChildren().add(cN);

            }
            commandList.setContent(vB);
        } catch (Exception ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    @FXML
    private void dash(ActionEvent event) {
        //setDimensions();
        myController.loadScreen(Start.dashboardID, Start.dashboardFile);
        myController.setScreen(Start.dashboardID);
        myController.setId("dashboard");
        myController.show(myController);
    }

    @FXML
    private void editCommands(ActionEvent event) {
        //setDimensions();
        myController.loadScreen(Start.commandEditorID, Start.commandEditorFile);
        myController.setScreen(Start.commandEditorID);
        myController.setId("commandEditor");
        myController.show(myController);
    }

    private void showCommandInfo(String command) {
        VBox vB = new VBox();
        vB.setAlignment(Pos.CENTER);
        vB.setPrefWidth(255);
        vB.setPrefHeight(323);
        for (int i = 0; i < this.store.getCommands().size(); i++) {
            final ConfigParameters.Command commandConfig = this.store.getCommands().get(i);
            if (commandConfig.name.equals(command)) {
                Label name = new Label("Name:");
                Label nameD = new Label(commandConfig.name);
                String authLevel = commandConfig.auth;
                if (authLevel.contains("+a")) {
                    authLevel = "All users (+a)";
                } else if (authLevel.contains("+s")) {
                    authLevel = "Sub only (+s)";
                } else if (authLevel.contains("+m")) {
                    authLevel = "Mod only (+m)";
                } else if (authLevel.equals(" ") || authLevel.equals("")) {
                    authLevel = "Broadcaster only";
                } else {
                    authLevel = "User only: " + commandConfig.auth;
                    authLevel = authLevel.replace("+", "");
                }
                Label auth = new Label("Authorization:");
                Label authD = new Label(authLevel);
                Label cooldown = new Label("Cooldown: " + commandConfig.cooldown + " seconds");

                Label disabled = new Label("Disabled: " + commandConfig.disabled);

                Label repeating = new Label("Repeating: " + commandConfig.repeating);
                Label interval = new Label("Interval: " + commandConfig.interval);
                Label initialDelay = new Label("Initial delay: " + commandConfig.initialDelay);
                Label sound = new Label("Sound: " + commandConfig.sound);
                Label message = new Label("Message:");
                Label messageD = new Label(commandConfig.text);
                messageD.setPrefHeight(200);
                messageD.setWrapText(true);
                messageD.setTextAlignment(TextAlignment.CENTER);
                messageD.setFont(Font.font("", FontWeight.BOLD, 12));
                nameD.setFont(Font.font("", FontWeight.BOLD, 12));
                authD.setFont(Font.font("", FontWeight.BOLD, 12));
                vB.getChildren().addAll(name, nameD, auth, authD, disabled, repeating, interval, initialDelay, cooldown, sound, message, messageD);
                commandNameTemp = commandConfig.name;
            };
        }
        commandInfo.getChildren().clear();
        commandInfo.getChildren().add(vB);
        // myController.setScreen(Start.dashboardID);
    }

    /*Start.dimensions dm = ScreensController.dm;

    private void setDimensions() {
        int h = (int) Start.stage.getHeight();
        int w = (int) Start.stage.getWidth();
        dm.setHeight(h);
        dm.setWidth(w);
    }*/
}

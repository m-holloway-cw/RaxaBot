/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.gui.handlers;

import com.raxa.bot.Start;
import com.raxa.store.Datastore;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Raxa
 */
public class ConfigurationController implements Initializable {

    Datastore store;
    ScreensController myController = new ScreensController();

    @FXML
    Label saveText;

    @FXML
    TextField testMessageText;

    @FXML
    TextField soundTestText;

    @FXML
    RadioButton rpgEnabled;

    @FXML
    RadioButton rpgDisabled;

    @FXML
    RadioButton spotifyEnabled;

    @FXML
    RadioButton spotifyDisabled;

    @FXML
    RadioButton lottoEnabled;

    @FXML
    RadioButton lottoDisabled;

    @FXML
    ToggleGroup RPG;

    @FXML
    ToggleGroup Spotify;

    @FXML
    ToggleGroup lotto;

    @FXML
    AnchorPane background;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        store = Start.bot.getStore();
        if (store.getConfiguration().lottoStatus.equals("on")) {
            lottoEnabled.setSelected(true);
        } else {
            lottoDisabled.setSelected(true);
        }
        if (store.getConfiguration().rpgStatus.equals("on")) {
            rpgEnabled.setSelected(true);
        } else {
            rpgDisabled.setSelected(true);
        }
        if (store.getConfiguration().spotifyStatus.equals("on")) {
            spotifyEnabled.setSelected(true);
        } else {
            spotifyDisabled.setSelected(true);
        }
        saveText.setVisible(false);
    }

    @FXML
    public void goDash() {
        //setDimensions();
        myController.loadScreen(Start.dashboardID, Start.dashboardFile);
        myController.setScreen(Start.dashboardID);
        myController.setId("dashboard");
        myController.show(myController);
    }

    // Takes user input filename and attempts to play
    // must be mp3 or wav
    @FXML
    private void playTestSound() {
        try {
            soundTestText.selectAll();
            soundTestText.copy();
            Path xmlFile = Paths.get("");
            Path xmlResolved = xmlFile.resolve(soundTestText.getText());
            Media hit = new Media(xmlResolved.toUri().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Start.bot.getStore().getEventList().addList("Successful sound test");
                }
            });
        } catch (Exception e) {
            soundTestText.setText("Error playing song");
        }
    }

    // Takes user input message to send to chat
    @FXML
    private void sendTestMessage() {
        testMessageText.selectAll();
        testMessageText.copy();
        String message = testMessageText.getText();
        try {
            DashboardController.wIRC.sendMessage(message, false);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Start.bot.getStore().getEventList().addList("Attempted to send: \'" + message + "\' to chat, reconnect if failed");
                }
            });
        } catch (Exception e) {
            System.out.println("error occured sending test");
            e.printStackTrace();
        }
    }

    @FXML
    public void saveSettings() {
        //grab radiobuttons, etc and save all content
        RadioButton toggle = (RadioButton) RPG.getSelectedToggle();
        String choice = toggle.getText();
        System.out.println(choice);
        String enabled;
        if (choice.equals("Enabled")) {
            enabled = "on";
        } else {
            enabled = "off";
        }
        //send enabled to spoopathon here
        store.modifyConfiguration("rpgStatus", enabled);
        System.out.println("RPG:" + enabled);

        toggle = (RadioButton) Spotify.getSelectedToggle();
        if (toggle.getText().equals("Enabled")) {
            enabled = "on";
        } else {
            enabled = "off";
        }
        //send enabled to spotify here
        store.modifyConfiguration("spotifyStatus", enabled);
        System.out.println("spotify:" + enabled);

        toggle = (RadioButton) lotto.getSelectedToggle();
        if (toggle.getText().equals("Enabled")) {
            enabled = "on";
            //CommandParser.lotto.lottoEnable();
        } else {
            enabled = "off";
            //CommandParser.lotto.lottoDisable();
        }
        //send enabled to lotto here
        store.modifyConfiguration("lottoStatus", enabled);
        System.out.println("lotto:" + enabled);

        //print confirmation text
        saveText.setVisible(true);
    }

    private int i = 0;
    
    @FXML
    public void OBS() {
        try {   
            i++;
            String message = "Configuration Test Message" + i;
            Start.ov.sendMessage(message);
            /*Robot sim = new Robot();
            sim.keyPress(KeyEvent.VK_CONTROL);
            sim.keyPress(KeyEvent.VK_ALT);
            sim.keyPress(KeyEvent.VK_SHIFT);
            sim.keyPress(KeyEvent.VK_B);
            sim.delay(100);
            sim.keyRelease(KeyEvent.VK_B);
            sim.keyRelease(KeyEvent.VK_ALT);
            sim.keyRelease(KeyEvent.VK_SHIFT);
            sim.keyRelease(KeyEvent.VK_CONTROL);*/
        } catch (Exception ex) {
            Logger.getLogger(ConfigurationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.gui.handlers;

import com.raxa.bot.Start;
import com.raxa.bot.RaxaBot;
import com.raxa.bot.utilities.CommonUtility;
import com.raxa.bot.utilities.WSClass;
import com.raxa.store.Datastore;
import com.raxa.store.XmlDatastore;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Raxa
 */
public class DashboardController implements Initializable, ControlledScreen {

    ScreensController myController = new ScreensController();

    //TODO implement synchronized/threadsafe dimension settings
    //Start.dimensions dm = ScreensController.dm;
    RaxaBot bot;
    Datastore store;
    
    public static WSClass wIRC;

    static int visitCount = 0;

    public static XmlDatastore.eventObList eventObL = new XmlDatastore.eventObList();

    @FXML
    private ListView<String> eventList;


    private static JFXPanel dashContainer;
    private static final int JFXPANEL_WIDTH_INT = 600;
    private static final int JFXPANEL_HEIGHT_INT = 400;


    @FXML
    private void commands(ActionEvent event) {
        //setDimensions();
        myController.loadScreen(Start.commandsID, Start.commandsFile);
        myController.setScreen(Start.commandsID);
        myController.setId("commands");
        myController.show(myController);
    }

    @FXML
    private void configuration(ActionEvent event) {
        // setDimensions();
        myController.loadScreen(Start.configurationID, Start.configurationFile);
        myController.setScreen(Start.configurationID);
        myController.setId("configuration");
        myController.show(myController);
    }

    @FXML
    private void moderation(ActionEvent event) {
        //setDimensions();
        myController.loadScreen(Start.moderationID, Start.moderationFile);
        myController.setScreen(Start.moderationID);
        myController.setId("moderation");
        myController.show(myController);
    }

    @FXML
    private void rpg(ActionEvent event) {
        ///setDimensions();
        myController.loadScreen(Start.rpgID, Start.rpgFile);
        myController.setScreen(Start.rpgID);
        myController.setId("rpg");
        myController.show(myController);
    }

    @FXML
    private void chat(ActionEvent event) {
        /* myController.loadScreen(Start.chatID, Start.chatFile);
        myController.setScreen(Start.chatID);
        myController.setId("chat");
        myController.show(myController);
         */
        Parent root;
        try {
            Path chat = Paths.get(Start.chatFile);
            FXMLLoader chatLoad = new FXMLLoader();
            chatLoad.setLocation(getClass().getClassLoader().getResource(chat.toString()));
            root = chatLoad.load();
            Stage stage = new Stage();
            stage.setTitle("Twitch Chat");
            stage.setScene(new Scene(root, 450, 891));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void startBot(ActionEvent event) {
        Thread botT;
        try {
            eventObL.addList("Starting bot");
            botT = new Thread() {
                @Override
                public void run() {
                    //open websocket connection
                    try {
                        wIRC = new WSClass(
                                new URI("wss://irc-ws-edge.chat.twitch.tv"),
                                store.getConfiguration().joinedChannel,
                                store.getConfiguration().account,
                                store.getConfiguration().password,
                                store);

                        if (!wIRC.connectWSS(false)) {
                            throw new Exception("Error when connecting to Twitch.");
                        } else {
                            Start.bot.start(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            botT.start();
            eventObL.addList("Bot connected to chat");
        } catch (NullPointerException e) {
            CommonUtility.ERRORLOGGER.severe(e.toString());
            eventObL.addList("No instance to cancel, restart the application");
        } catch (Exception e) {
            CommonUtility.ERRORLOGGER.severe(e.toString());
            e.printStackTrace();
            eventObL.addList("General error occured creating the bot, restart the application");
        }
    }

    @FXML
    private void restartBot(ActionEvent event) {
      wIRC.close();
    }

    public Scene getScene() throws IOException {
        Parent dash = FXMLLoader.load(getClass().getResource("./Dashboard.fxml"));
        Scene dashBoard = new Scene(dash);
        return dashBoard;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        store = Start.bot.getStore();
        store.setLV(eventList);
        store.getLV().setItems(eventObL.getList());
        store.setEvent(eventObL);
        ChatController.getChat(store.getConfiguration().account + " connected to #" + store.getConfiguration().joinedChannel);
        if (visitCount == 0) {
            eventObL.addList("Bot ready to connect");
        }
        visitCount++;
    }

    public void eventObLAdd(String msg) {
        eventObL.addList(msg);
        eventList.setItems(eventObL.getList());
        eventList.scrollTo(eventObL.getList().size() - 1);
    }

    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }
//TODO
    /*
    private void setDimensions() {
       int h = (int) Start.stage.getHeight();
       int w = (int) Start.stage.getWidth();
       dm.setHeight(h);
       dm.setWidth(w);
    }*/

}

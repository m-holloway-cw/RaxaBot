/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot;

import com.raxa.bot.handlers.DiscordBot;
import com.raxa.bot.handlers.OverlayHandler;
import com.raxa.bot.handlers.SpotifyReader;
import com.raxa.bot.rpg.Player;
import com.raxa.bot.rpg.rpgHandler;
import com.raxa.gui.handlers.ScreensController;
import com.raxa.bot.utilities.ConfigParameters;
import com.raxa.bot.utilities.ConfigParameters.Elements;
import com.raxa.store.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.annotation.concurrent.GuardedBy;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

/**
 *
 * @author Raxa
 */
public class Start extends Application {

    //TODO
    private final ConfigParameters configParser = new ConfigParameters();
    public static Stage stage = new Stage();
    public static RaxaBot bot;
    public static Datastore store;
    public static rpgHandler rpg;
    public static OverlayHandler ov;

    //refine event viewer mod actions, command updates, etc
    public static String dashboardID = "dashboard";
    public static String dashboardFile = "Dashboard.fxml";

    //add/edit/delete/cooldown/auth etc view all scrollable
    public static String commandsID = "commands";
    public static String commandsFile = "Commands.fxml";
    public static String commandEditorID = "commandEditor";
    public static String commandEditorFile = "CommandEditor.fxml";

    //configuration -> account settings, joined channel
    public static String configurationID = "configuration";
    public static String configurationFile = "Configuration.fxml";

    //moderation -> filters, phrases, toggles, spam
    public static String moderationID = "moderation";
    public static String moderationFile = "Moderation.fxml";
    public static String regexID = "regex";
    public static String regexFile = "Regex.fxml";

    //queue, lottery, raffle display users + order, scrollable
    public static String lotteryID = "lottery";
    public static String lotteryFile = "Lottery.fxml";

    //rpg game system & settings
    public static String rpgID = "rpg";
    public static String rpgFile = "rpg.fxml";

    //twitch chat interface
    public static String chatID = "chat";
    public static String chatFile = "Chat.fxml";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //begin bot setup
        try {
            Path xmlFile = Paths.get("");
            Path xmlResolved = xmlFile.resolve("raxabot.xml");
            final Elements elements = configParser.parseConfiguration(xmlResolved.toString());
            store = new XmlDatastore(elements);
            bot = new RaxaBot(store);
            store.setBot(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create and start discord bot setup todo run this from another page/start button
        DiscordBot db = new DiscordBot();
        //db.start();

        // create and start instance to handle spotify now playing
        // resets access values every 2,000 seconds and polls API every 5 seconds
        if (store.getConfiguration().spotifyStatus.equals("on")) {
            SpotifyReader sr = new SpotifyReader();
            ScheduledExecutorService get = Executors.newScheduledThreadPool(1);
            get.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    sr.getAccess();
                }
            }, 0, 2000, TimeUnit.SECONDS);
            ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
            ses.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    sr.start();
                }
            }, 2, 5, TimeUnit.SECONDS);
        }
        try {
            // create/start rpg instance thread and deserialize the rpg hashmap
            rpg = new rpgHandler(bot.getStore());
            //uncomment next 2 lines to create new RPG.map file
            HashMap<Integer, Player> MAP = new HashMap<Integer, Player>();
            rpg.writeMap(MAP);
            rpg.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // start up node processes for SSE event system
        // create associate websocket
        try {
            //Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("node EventServer/server.js");

            /*BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            String response = in.readLine();
            String error = err.readLine();

            System.out.println(response);

            if (!error.equals("null")) {
                System.out.println("Error occured starting Node system: " + error);
            } else {
                System.out.println("Response from node: " + response);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        ov = new OverlayHandler();
        ov.start();

        ScreensController container = new ScreensController();
        container.loadScreen(dashboardID, dashboardFile);
        container.setScreen(dashboardID);

        Group root = new Group();
        root.getChildren().addAll(container);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        String botName = store.getConfiguration().account;
        stage.setTitle(botName);

        stage.getIcons().add(new Image("https://www.raxastudios.com/images/raxaHeart.png"));

        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    //storage for width/height variables to allow scalable content
    public static class dimensions {

        @GuardedBy("this")
        private int width = 600;
        @GuardedBy("this")
        private int height = 400;

        public synchronized int getWidth() {
            return this.width;
        }

        public synchronized void setWidth(int w) {
            this.width = w;
        }

        public synchronized int getHeight() {
            return this.height;
        }

        public synchronized void setHeight(int h) {
            this.height = h;
        }
    }

}

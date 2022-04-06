/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot;


import com.raxa.bot.handlers.WhisperHandler;
import com.raxa.bot.utilities.CommonUtility;
import com.raxa.bot.utilities.TimerManagement;
import com.raxa.store.Datastore;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the application for a Twitch Bot.
 */
public final class RaxaBot {

    private static Logger LOGGER;

    private final Datastore store;

    private int reconCount = 0;

    private ScheduledExecutorService sPP;
    public TimerManagement timers;
    public static TimerManagement.pongHandler pH = new TimerManagement.pongHandler();

    // construct bot 
    public RaxaBot(Datastore getStore) {
        this.store = getStore;
        LOGGER = CommonUtility.ERRORLOGGER;
    }

    public Datastore getStore() {
        return this.store;
    }

    public void start(boolean reconnect) {
        try {

            LOGGER.info("Bot is now ready for service.");
            
            //open pubsub connection listen to channel point events
            WhisperHandler.connect(store);
            //clear out old timers before
            // start all periodic timers for broadcasting events
            TimerManagement.LHM.clear();
            startTimers(store);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error caught at bot start up: {0}");
            e.printStackTrace();
        }
    }


    /*
    ** This will start the repeating commands based on what is found in XML
    **
     */
    public void startTimers(final Datastore store) {
        timers = new TimerManagement();
        timers.setupPeriodicBroadcast();
    }

}

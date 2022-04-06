/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.utilities;

import com.raxa.bot.utilities.ConfigParameters;
import com.raxa.bot.RaxaBot;
import com.raxa.bot.Start;
import com.raxa.gui.handlers.DashboardController;
import com.raxa.store.Datastore;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import org.w3c.dom.Element;

/*
** Roughly 15 minute intervals between command sendMessage, 
** OR based on number of messages between sendMessage
**
** RE-ADD online check timer foro online-only timer functionality 
** as well as for future discord stream is live function
 */

/**
 * This class is responsible for timer management.
 */
public final class TimerManagement {

    public static final LinkedHashMap<String, Task> LHM = new LinkedHashMap<>();
    private final Datastore store;

    /*
** This takes the information parsed in the start of the program under 
** elements "repeating" and "interval"
** "repeating" = True commands need to start when the bot starts
** all commands set to repeat need to start (including created/edited commands)
     */
    public TimerManagement() {
        this.store = Start.bot.getStore();
        //this.outstream = stream;
    }

    public synchronized LinkedHashMap<String, Task> getLHM() {
        return LHM;
    }

    
    public volatile boolean repeat;

    public void setupPeriodicBroadcast() {
        for (int i = 0; i < store.getCommands().size(); i++) {
            final ConfigParameters.Command command = Start.bot.getStore().getCommands().get(i);
            if (Boolean.parseBoolean(command.repeating)) {
                int interval = Integer.parseInt(command.interval);
                if (interval < 600) {
                    String event = "Repeating interval too short for command " + command.name;
                    Platform.runLater(() -> {
                        store.getEventList().addList(event);
                    });
                } else {
                    if (schedule(command.name)) {
                        String event = "Started repeating command: " + command.name;
                        Platform.runLater(() -> {
                            store.getEventList().addList(event);
                        });
                    } else {
                        String event = "Failed to start repeating command: " + command.name;
                        Platform.runLater(() -> {
                            store.getEventList().addList(event);
                        });
                    };
                }
            }
        }
    }

    public boolean schedule(String commandToSchedule) {
        for (int i = 0; i < store.getCommands().size(); i++) {
            final ConfigParameters.Command command = Start.bot.getStore().getCommands().get(i);
            if (command.name.equals(commandToSchedule)) {
                int iDelay = Integer.parseInt(command.initialDelay);
                int interval = Integer.parseInt(command.interval);
                try {
                    if (getLHM().containsKey(command.name)) {
                        System.out.println("Duplicate found");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Start.bot.getStore().getEventList().addList("Repeating command " + command.name + " already in progress");
                            }
                        });
                        return false;
                    } else {
                        Task task = new Task(command.name, command.text, iDelay, interval);
                        getLHM().put(command.name, task);
                        task.start();
                        return true;
                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
                return false;
            }
        }
        return false;
    }

    public boolean stop(String commandToStop) {
        try {
            getLHM().get(commandToStop).shutdown();
            getLHM().remove(commandToStop);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Start.bot.getStore().getEventList().addList("Repeating command " + commandToStop + " not found to stop");
                }
            });
            return false;
        }
    }

    public static class Task {

        private final ConnectionServices tH = new ConnectionServices();
        private volatile boolean dead;
        private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        private Future f;
        private final String commandName;
        private final String commandText;
        private final int initialDelay;
        private final int commandInterval;

        public Task(String name, String content, int initD, int interval) {
            commandName = name;
            commandText = content;
            initialDelay = initD;
            commandInterval = interval;
            dead = false;
        }

        public void start() {
            Runnable runnable = () -> {
                
                if (!getDead()) {
                    // check for stream online/offline
                    if (tH.uptime("").equals("Stream is not currently live.")) {
                        System.out.println("Stream offline while attemping to print: " + commandName);
                    } else {
                        System.out.println("Text of " + commandName + ": " + commandText);
                        DashboardController.wIRC.sendMessage(commandText, true);
                    }
                } else {
                    this.shutdown();
                }
                
            };
            f = ses.scheduleWithFixedDelay(runnable, initialDelay, commandInterval, TimeUnit.SECONDS);
        }

        private boolean getDead() {
            return dead;
        }

        public void shutdown() {
            System.out.println("Current shutdown thread: " + Thread.currentThread().getName());
            System.out.println("Shutting down command: " + commandName);
            ses.shutdown();
            f.cancel(true);
            try {
                if (!ses.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    ses.shutdownNow();
                    Thread.currentThread().join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                ses.shutdownNow();
            }
            dead = true;
        }
    }


    @ThreadSafe
    public static class pongHandler {

        @GuardedBy("this")
        private int pongCounter = 1;

        public synchronized int getPong() {
            System.out.println("Pongs sent: " + pongCounter);
            return pongCounter;
        }

        public synchronized void addPong() {
            pongCounter++;
            System.out.println("Pongs +1= " + pongCounter);
        }

        public synchronized void resetPong() {
            System.out.println("Pongs reset");
            pongCounter = 0;
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers;

import com.raxa.bot.utilities.CommandParser;
import com.raxa.store.Datastore;
//import com.twitchbotx.gui.LotteryController;
import com.raxa.bot.Start;
import com.raxa.gui.handlers.DashboardController;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
//import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.annotation.concurrent.GuardedBy;

/**
 *
 * @author Raxa
 */
public class LotteryHandler {

    //lottery system, broadcaster starts a lottery with parameter word to enter in chat to enter the lottery
    //word set every new lottery, !draw # to choose # of winners, clear from array after picked
    //reset array every new lottery
    private static final Logger LOGGER = Logger.getLogger(LotteryHandler.class.getSimpleName());

    private Datastore store = Start.bot.getStore();

    public LotteryHandler() {

    }

    //Begin lottery with ticket class
    //caster syntax !lottery-open [auth] [keyword] !lottery-close !draw
    //viewer syntax keyword in chat
    public static class Lotto {

        @GuardedBy("this")
        private final Random RNG = new Random();
        @GuardedBy("this")
        private final LinkedHashMap<String, Entrant<Integer, String>> MAP = new LinkedHashMap<>();
        @GuardedBy("this")
        private int size = MAP.size();
        @GuardedBy("this")
        private final List<String> prevWinner = new java.util.ArrayList<>();
        @GuardedBy("this")
        private final List<String> currPool = new java.util.ArrayList<>();
        @GuardedBy("this")
        private boolean lottoOn = true;
        @GuardedBy("this")
        private String winner;
        @GuardedBy("this")
        private String keyword;
        @GuardedBy("this")
        private boolean subOnly = false;

        public synchronized LinkedHashMap<String, Entrant<Integer, String>> getMap() {
            return this.MAP;
        }

        public synchronized List<String> getCurr() {
            return currPool;
        }

        public synchronized List<String> getPrev() {
            return prevWinner;
        }

        public synchronized void addUser(String user, boolean sub) {
            if (subOnly && sub || !subOnly) {
                String displayName = CommandParser.displayName;
                boolean nameCheck = user.equalsIgnoreCase(displayName);
                if (!nameCheck) {
                    displayName = user;
                }
                int tempCountFix = 0;
                for (String check : currPool) {
                    if (check.equals(user)) {
                        if (tempCountFix < 1) {
                            tempCountFix++;
                            sendMessage("User " + displayName + " already entered");
                            return;
                        } else {
                            return;
                        }
                    }
                }
                currPool.add(user);
                int ticketValue = 2;
                tempCountFix = 0;
                boolean p = true;
                for (String u : prevWinner) {
                    if (u.equals(user)) {
                        if (tempCountFix < 1) {
                            p = false;
                            sendMessage("User " + displayName + " re-added");
                            ticketValue = 1;
                            tempCountFix++;
                        }
                    }
                }
                if (p && tempCountFix == 0) {
                    sendMessage(displayName + " added");
                }
                MAP.put(user, new Entrant());
                MAP.get(user).addTicket(ticketValue);
            }
        }

        public synchronized void leaveLotto(String user) {
            MAP.remove(user);
            prevWinner.add(user);
            currPool.remove(user);
            sendMessage(user + " has been removed from the lottery");
        }

        public synchronized String drawLotto() {
            int r = 0;
            size = MAP.size();
            List<String> sA = new java.util.ArrayList<>();
            for (int i = 0; i < size; i++) {
                MAP.entrySet().forEach((m) -> {
                    int tempTickets = m.getValue().getTicket();
                    while (tempTickets != 0) {
                        sA.add(m.getKey());
                        tempTickets--;
                    }
                });
            }
            try {
                Collections.shuffle(sA);
                r = RNG.nextInt(sA.size());
                winner = sA.get(r);
                sendMessage("Winner: " + winner + " kffcCheer");
                prevWinner.add(winner);
                MAP.remove(winner);
                currPool.remove(winner);
                MAP.entrySet().forEach((m) -> {
                    int ticketValue = 2;
                    for (String c : prevWinner) {
                        if (c.equals(m.getKey())) {
                            ticketValue = 1;
                        }
                    }
                    m.getValue().addTicket(ticketValue);
                    //System.out.println(m.getKey() + " tickets: " + m.getValue().getTicket());
                });
            } catch (NullPointerException | IllegalArgumentException ne) {
                sendMessage("Lottery is empty!");
            }
            return (winner);
        }

        //boolean value lottoOn will not clear MAP
        //value meant to prevent additional entries
        public synchronized void lottoOpen(String trailing) {
            lottoOn = true;
            Start.bot.getStore().modifyConfiguration("lottoStatus", "on");
            subOnly = false;
            String auth;
            try {
                auth = trailing.substring(trailing.indexOf(" ") + 1, trailing.indexOf(" ") + 3);
                if (!auth.equalsIgnoreCase("+a") && !auth.equalsIgnoreCase("+s")) {
                    auth = "+a";
                    keyword = trailing.substring(trailing.indexOf(" "), trailing.length());
                } else {
                    keyword = trailing.substring(trailing.indexOf(auth) + 3, trailing.length());
                }
            } catch (StringIndexOutOfBoundsException e) {
                auth = "+a";
                keyword = trailing.substring(trailing.indexOf(" "), trailing.length());
            }
            //System.out.println("auth found: " + auth + " keyword: " + keyword);
            if (auth.equals("+s")) {
                subOnly = true;
                sendMessage("A sub-only lottery has started! Subs can type " + keyword + " to enter!");
            } else {
                subOnly = false;
                sendMessage("Lottery has started! Type " + keyword + " to enter!");
            }
        }

        public synchronized void lottoClear() {
            MAP.clear();
            currPool.clear();
            sendMessage("Lottery pool has been cleared");
        }

        public synchronized void lottoClose() {
            lottoOn = false;
            Start.bot.getStore().modifyConfiguration("lottoStatus", "off");
            sendMessage("Lottery has been closed");
        }

        public synchronized boolean getLottoStatus() {
            return lottoOn;
        }

        public synchronized String getLottoName() {
            return keyword;
        }

        private void sendMessage(final String msg) {
            DashboardController.wIRC.sendMessage(msg, true);
        }

    }

    public static class Entrant<Integer, String> implements Serializable {

        private String content;
        public int ticket;

        public Entrant(String content) {
            this.content = content;
            this.ticket = 0;
        }

        public Entrant() {
            this.ticket = 0;
        }

        public void addTicket(int pastWinner) {
            this.ticket += pastWinner;
        }

        public String getContent() {
            return this.content;
        }

        public int getTicket() {
            return this.ticket;
        }
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

    /**
     * This command will send a message out to a specific Twitch channel.
     *
     * It will also wrap the message in pretty text (> /me) before sending it
     * out.
     *
     * @param msg The message to be sent out to the channel
     */
    private void sendMessage(final String msg) {
        DashboardController.wIRC.sendMessage(msg, true);
    }

}

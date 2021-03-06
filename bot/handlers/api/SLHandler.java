/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxa.store.Datastore;
import com.raxa.bot.Start;
import com.raxa.bot.handlers.sqlHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import javafx.application.Platform;

/**
 *
 * @author Raxa
 */
public class SLHandler implements Runnable {

    private static String accessToken;
    private final Datastore store;
    private String temp = "";
    private int tempCount = 0;

    public SLHandler(final Datastore store) {
        this.store = store;
        this.accessToken = store.getConfiguration().streamlabsToken;
    }

    public void run() {
        try {
            String limit = "1";
            URL url = new URL("https://streamlabs.com/api/v1.0/donations?access_token=" + accessToken + "&limit=" + limit + "&currency=USD");
            HttpURLConnection t = (HttpURLConnection) url.openConnection();
            t.setRequestMethod("GET");
            t.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.85 Safari/537.36");
            BufferedReader read = new BufferedReader(new InputStreamReader(t.getInputStream()));
            JsonNode data = new ObjectMapper().readTree(read.readLine());

            String donation = data.get("data").toString();
            //System.out.println("donation:  " + donation);
            donation = donation.replace("[", "");
            donation = donation.replace("]", "");
            JsonNode low = new ObjectMapper().readTree(donation);
            if (tempCount == 0) {
                temp = low.get("donation_id").asText();
                tempCount++;
                //for testing 
                temp = low.get("donation_id").asText();
                double amount = low.get("amount").asDouble();
                DecimalFormat f = new DecimalFormat("##.00");

                String outAmount = (String) f.format(amount);
                System.out.println("amount: " + amount);
                int am = low.get("amount").asInt();
                outAmount = outAmount.replaceAll("[^0-9]", "");
                System.out.println("DONATIONID:" + temp);
                System.out.println("Donation amount: $" + am + " formatted for marathon:" + outAmount);
                String username = low.get("name").asText();
                System.out.println("From: " + username);
                String msg = low.get("message").asText();
                System.out.println("Message: " + msg);
            }

            if (temp.equals("") || !temp.equals(low.get("donation_id").asText())) {
                //convert dollar amount to point value
                //1 cent = 1 point i.e. $9.50 donation = 950 points
                temp = low.get("donation_id").asText();
                double amount = low.get("amount").asDouble();
                DecimalFormat f = new DecimalFormat("##.00");

                String outAmount = (String) f.format(amount);
                int am = low.get("amount").asInt();
                outAmount = outAmount.replaceAll("[^0-9]", "");
                System.out.println("DONATIONID:" + temp);
                System.out.println("Donation amount: $" + am + " formatted for marathon:" + outAmount);
                String username = low.get("name").asText();
                System.out.println("From: " + username);
                String msg = low.get("message").asText();
                System.out.println("Message: " + msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addSubSQLPoints(String msg, int points) {
        //parse for #game, send with points to sqlHandler.java
        sqlHandler sql = new sqlHandler(store);
        sql.gameSearch(msg, points);
    }

    private void sendEvent(String user, String msg, int amount) {
        String eventMsg = "Donation Event: " + user + " donated $" + amount + " with message: " + msg;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Start.bot.getStore().getEventList().addList(eventMsg);
            }
        });
    }
}

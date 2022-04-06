/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers;

import com.raxa.bot.utilities.ConfigParameters;
import com.raxa.store.Datastore;
import com.raxa.bot.utilities.TwitchMessenger;
import com.raxa.gui.handlers.DashboardController;
import com.raxa.bot.Start;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.w3c.dom.DOMException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Raxa
 *
 * use this over FTP handler with php on website see SpotifyReader ->
 * ServerHandler.java for current version
 *
 */
public class sqlHandler {

    //SQL database
    String SQLURL;
    String USER;
    String PASS;
    Connection con = null;
    Statement stmt = null;
    String sqlStatement = "";

    private static final Logger LOGGER = Logger.getLogger(sqlHandler.class.getSimpleName());

    private Datastore store;

    private String gameName;
    private String gameID;
    private String gamePoints;

    public sqlHandler(final Datastore store) {
        this.store = store;
        this.SQLURL = store.getConfiguration().sqlURL;
        this.USER = store.getConfiguration().sqlUser;
        this.PASS = store.getConfiguration().sqlPass;
        this.gameID = "";
        this.gameName = "";
        this.gamePoints = "";
    }

    //open the connection
    //static login and table info
    //returns null if connection fails
    Statement connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(SQLURL, USER, PASS);
            stmt = con.createStatement();
            return stmt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void closeConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(sqlHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addGame(String msg) {
        
    }

    public void deleteGame(String msg) {
        
    }

    public void setName(String msg) {
        
    }

    public void setPoints(String msg) {
        
    }

    /*
    ** get methods for points, name, id
     */
    public void getData(int index) {
        
    }

    public void getSize() {
       
    }

    public void addPoints(String msg) {
        
    }

    public void getPoints(String msg, String username) {

    }

    public void gameSearch(String msg, int points) {
       
    }

    /*
    ** This method will enable or disable the addPoints method
    ** meaning no bot messages in chat, no points added to system
    ** quick method to disable system for user side
     */
    public void sStatus(String msg) {

    }


    private void sendMessage(final String msg, boolean action) {
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

    public void download() {
        try {
            FileUtils.copyURLToFile(new URL("url to jar here"), new File("KFbot-1.1.jar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

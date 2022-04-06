/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers;

import com.raxa.bot.Start;
import com.raxa.store.Datastore;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;

/**
 *
 * @author Raxa
 */
public class SpotifyReader {

    public static String REFRESH_TOKEN = "";
    int m = 0;
    int statusCount = 0;
    String newCode = "";
    private Datastore store = Start.bot.getStore();
    
    public void start() {

        String sAPI = "https://api.spotify.com/v1/me/player/currently-playing";

        try {
            URL url = new URL(sAPI);
            HttpURLConnection t = (HttpURLConnection) url.openConnection();
            t.setRequestMethod("GET");
            //System.out.println(newCode);
            t.setRequestProperty("Authorization", "Bearer " + newCode);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(t.getInputStream()));
            StringBuilder info = new StringBuilder();
            int i = 0;
            String line = "";
            try {
                while (i < 3) {
                    //info.append(bufReader.readLine());
                    line = bufReader.readLine();
                    //System.out.println(line);
                    info.append(line);
                    if (line.contains("null")) {
                        i++;
                    }
                }
            } catch (NullPointerException ne) {
                //System.out.println("End of response");
            }
            String status = t.getResponseMessage();
            if (!status.equals("OK")) {
                //System.out.println("Get status code: " + t.getResponseMessage());
            } else if (statusCount < 1) {
                //System.out.println("Get status code: " + t.getResponseMessage());
                statusCount++;
            }
            /* 
            **  Search for and set the Title
             */
            //System.out.println(info.toString());
            String title = "";
            int beginTitleIndex = info.indexOf("name", info.indexOf(".com/track")) + 9;
            int endTitleIndex = info.indexOf("\",", beginTitleIndex);
            title = info.substring(beginTitleIndex, endTitleIndex);

            /* 
            **  Search for and set the Artist
             */
            int artistStart = info.toString().indexOf("\"name\"", info.toString().indexOf("artists")) + 10;
            int artistEnd = info.toString().indexOf(",", artistStart) - 1;
            String artist = info.toString().substring(artistStart, artistEnd);
            String songInfo = (title + " - " + artist + " - ");
            writeInfo(songInfo);

        } catch (IOException e) {
            //System.out.println("HTTP error: " + e);
            getAccess();
        } catch (StringIndexOutOfBoundsException ie) {
            if (m < 1) {
                //System.out.println("player closed");
                m++;
            } else {

            }
        } catch (Exception e) {
            //System.out.println("error: " + e);
            e.printStackTrace();
        }
    }

    public void getAccess() {
        String urlRequest = store.getConfiguration().spotifyUrl;
        String clientId = store.getConfiguration().spotifyId;
        String clientSecret = store.getConfiguration().spotifySecret;
        try {

            Path configFile = Paths.get("config.ini");
            Scanner scan = new Scanner(configFile);
            while (scan.hasNext()) {
                REFRESH_TOKEN = scan.next();
            }
            //System.out.println("Found configuration, config:" + REFRESH_TOKEN);
            HttpClient httpC = new HttpClient();
            PostMethod postMethod = new PostMethod(urlRequest);
            NameValuePair[] req = {
                new NameValuePair("grant_type", "refresh_token"),
                new NameValuePair("refresh_token", REFRESH_TOKEN),
                new NameValuePair("client_id", clientId),
                new NameValuePair("client_secret", clientSecret),};
            postMethod.setRequestBody(req);
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            try {
                httpC.executeMethod(postMethod);
                newCode = postMethod.getResponseBodyAsString();
                //System.out.println("Post reply: " + postMethod.getResponseBodyAsString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            int statusPost = postMethod.getStatusCode();
            //System.out.println("Post status code: " + postMethod.getStatusCode());
            if (statusPost == 400) {
                System.out.println("Failed to authenticate, restart the app.");
                System.exit(1);
            } else {
                int startAccess = newCode.indexOf("access_token") + 15;
                int endAcess = newCode.indexOf("token_type") - 3;
                newCode = newCode.substring(startAccess, endAcess);
                //System.out.println("New access_token found: " + newCode);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    static String temp = "";

    public static void writeInfo(String songInfo) {
        if (temp.equals(songInfo)) {
            //do nothing as this is a duplicate entry
        } else {
            try {
                FileWriter fw = new FileWriter("./songInfo.txt");
                fw.write(songInfo);
                fw.close();
                //System.out.println("Wrote [" + songInfo + "] to songsInfo.txt file");
                temp = songInfo;
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}

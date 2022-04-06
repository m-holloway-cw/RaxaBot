/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.utilities;

import com.raxa.bot.Start;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Raxa
 * 
 * TODO
 * 
 * change ftp system to SQL -> website PHP system
 */
public class FTPHandler implements Runnable {

    @Override
    public void run() {
        System.out.println("Checking for changes to default.html");
        handleFTP();
    }

    public static void handleFTP() {
        String server = Start.store.getConfiguration().ftpURL;
        int port = 21;
        String username = Start.store.getConfiguration().ftpUser;
        String password = Start.store.getConfiguration().ftpPass;

        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        try {

            ftpClient.connect(server, port);
            if (ftpClient.login(username, password)) {
                System.out.println("Start uploading first file");
                String filename = "default.html";
                fis = new FileInputStream(new File(filename));
                boolean result = ftpClient.storeFile("/public_html/kf/" + filename, fis);
                fis.close();
                if (result) {
                    System.out.println("No issues");
                }
            } else {
                System.out.println("LOGIN FAILED");
            }

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    System.out.println("Disconnecting FTP");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

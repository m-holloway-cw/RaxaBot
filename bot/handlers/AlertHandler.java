/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers;

import com.raxa.store.Datastore;

/**
 *
 * @author Raxa
 * Use streamlabs socket api to obtain alert information
 * subscriptions, follows, donations, bits, raids, hosts
 */
public class AlertHandler implements Runnable{
    private final String accessCode;
    private final Datastore store;
    public AlertHandler(final Datastore store){
        this.store = store;
        this.accessCode = this.store.getConfiguration().streamlabsToken;
    }
    
    public void run(){
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.handlers;

import java.util.Random;

/**
 *
 * @author Raxa
 */
public class ChatGamesHandler {

    
    
    // TODO update/change odds?
    public boolean roulette() {
        Random RNG = new Random();
        int fate = RNG.nextInt(1000);
        return (fate < 500);
    }

    // do like a slot machine with ffz emotes for currency
    // ie
    /*
    ** raxaGoalb raxaW raxaGoalg
    ** raxaDucklyn raxaW raxaTride
    ** raxaHeart raxaW raxaOH
    */
    public String slots(){
        
        return "";
    }
    
    // betting with odds wager currency
    public String bet(String options){
        
        return "";
    }
    
}

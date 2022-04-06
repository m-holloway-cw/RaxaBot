/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.bot.rpg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Raxa
 */
public class Player implements Serializable {

    private String name;
    private String type;
    private String weaponName;
    private String armorName;
    private int currHP;
    private int maxHP;
    private int armorStat;
    private int luck;
    private int strength;
    private int speed;
    private int intellect;
    private int charisma;
    private int constitution;
    private int gold;

    public Player(String name, String type) {
        // new characters roll for stats 
        // type influences bonuses
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public boolean setName(String name) {
        // allow for full name i.e. use [raxa the terrible] syntax for chat command
        try {
            this.name = name;
            return true;
        } catch (NullPointerException ne) {
            return false;
        }
    }

    public Player rollStats(String type) {
        this.luck = rollBase();
        this.strength = rollBase();
        this.intellect = rollBase();
        this.charisma = rollBase();
        this.constitution = rollBase();
        Random rng = new Random();
        // add bonuses based on type
        switch (this.type) {
            case "barbarian":
                this.maxHP = rng.nextInt(12) + 1 + this.constitution;
                this.currHP = this.maxHP;
                //TODO armorstat, any modifiers for luck, stregth, intellect, charisma, constitution
                //base gold and equipment and possible story path
                break;
            case "fighter":
                this.maxHP = rng.nextInt(10) + 1 + this.constitution;
                this.currHP = this.maxHP;
                break;
            case "ranger":
                this.maxHP = rng.nextInt(10) + 1 + this.constitution;
                this.currHP = this.maxHP;
                break;
            case "wizard":
                this.maxHP = rng.nextInt(6) + 1 + this.constitution;
                this.currHP = this.maxHP;
                break;
            case "cleric":
                this.maxHP = rng.nextInt(8) + 1 + this.constitution;
                this.currHP = this.maxHP;
                break;
        }
        return this;
    }

    public int rollBase() {
        Random rng = new Random();
        //roll 4 d6 take sum of highest 3 rolls
        int[] rolls = new int[4];
        int total = 0;
        for (int i = 0; i < rolls.length; i++) {
            rolls[i] = rng.nextInt(6) + 1; // num is between 0 and 5, add 1 to match dice roll
        }
        System.out.println(Arrays.toString(rolls));
        Arrays.sort(rolls);
        System.out.println(Arrays.toString(rolls));
        for (int j = 1; j < 4; j++) {
            // take highest 3 and add
            total += rolls[j];
        }
        System.out.println(total);
        return total;
    }
}

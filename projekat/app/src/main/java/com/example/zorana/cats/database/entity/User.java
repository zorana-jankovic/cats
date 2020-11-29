package com.example.zorana.cats.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int numOfWins;
    private int numOfDefeats;
    private int numOfUnsolved;
    private int musicOn;
    private int manualFight;
    private int numOfBoxes;

    public User(String name){
        this.name = name;
        numOfWins = 0;
        numOfDefeats = 0;
        numOfUnsolved = 0;
        musicOn = 0;
        manualFight = 0;
        numOfBoxes = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfWins() {
        return numOfWins;
    }

    public void setNumOfWins(int numOfWins) {
        this.numOfWins = numOfWins;
    }

    public int getNumOfDefeats() {
        return numOfDefeats;
    }

    public void setNumOfDefeats(int numOfDefeats) {
        this.numOfDefeats = numOfDefeats;
    }

    public int getNumOfUnsolved() {
        return numOfUnsolved;
    }

    public void setNumOfUnsolved(int numOfUnsolved) {
        this.numOfUnsolved = numOfUnsolved;
    }

    public int getMusicOn() {
        return musicOn;
    }

    public void setMusicOn(int musicOn) {
        this.musicOn = musicOn;
    }

    public int getManualFight() {
        return manualFight;
    }

    public void setManualFight(int manualFight) {
        this.manualFight = manualFight;
    }

    public int getNumOfBoxes() {
        return numOfBoxes;
    }

    public void setNumOfBoxes(int numOfBoxes) {
        this.numOfBoxes = numOfBoxes;
    }
}

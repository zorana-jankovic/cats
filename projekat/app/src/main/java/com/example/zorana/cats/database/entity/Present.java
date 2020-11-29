package com.example.zorana.cats.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Present {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long idUser;
    private long time;

    public Present(long idUser, long time) {
        this.idUser = idUser;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

package com.example.zorana.cats.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CarParts {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long idPart;
    private long idUser;
    private int onCar;

    public CarParts(long idPart, long idUser,int onCar){
        this.idPart = idPart;
        this.idUser = idUser;
        this.onCar = onCar;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdPart() {
        return idPart;
    }

    public void setIdPart(long idPart) {
        this.idPart = idPart;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public int getOnCar() {
        return onCar;
    }

    public void setOnCar(int onCar) {
        this.onCar = onCar;
    }
}

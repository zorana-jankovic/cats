package com.example.zorana.cats.database.dao;

import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.User;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class CarPartsDao {

    @Insert
    public abstract long insert(CarParts carParts);

    @Query("SELECT * FROM CarParts WHERE idUser=:idUser AND onCar=:onCar")
    public abstract List<CarParts> getAllCarParts(long idUser, int onCar);

    @Query("DELETE FROM CarParts WHERE id=:id")
    public abstract void deleteCarPart(long id);

    @Query("DELETE FROM CarParts WHERE idUser=:idUser")
    public abstract void deleteCarPartForUser(long idUser);

    @Query("UPDATE CarParts SET onCar=:onCar , idPart=:idPart WHERE id=:id")
    public abstract  void updateCarPart(long id, int onCar, long idPart);

}

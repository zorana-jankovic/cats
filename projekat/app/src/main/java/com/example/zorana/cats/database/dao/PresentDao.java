package com.example.zorana.cats.database.dao;


import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.Present;
import com.example.zorana.cats.database.entity.User;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class PresentDao {

    @Insert
    public abstract long insert(Present present);

    @Query("SELECT * FROM Present WHERE idUser=:idUser")
    public abstract List<Present> getAllPresents(long idUser);

    @Query("DELETE FROM Present WHERE id=:id")
    public abstract void deletePresent(long id);

    @Query("DELETE FROM Present WHERE idUser=:idUser")
    public abstract void deletePresentForUser(long idUser);

}

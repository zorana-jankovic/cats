package com.example.zorana.cats.database.dao;

import com.example.zorana.cats.database.entity.User;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class UserDao {

    @Insert
    public abstract long insert(User user);

    @Query("SELECT * FROM User")
    public abstract List<User> getAllUsers();

    @Query("SELECT * FROM User")
    public abstract List<User> getAllUsersThread();

    @Query("SELECT * FROM User WHERE name=:s")
    public abstract User getUser(String s);

    @Query("DELETE FROM User WHERE name=:i")
    public abstract void deleteOneUser(String i);

    @Query("DELETE  FROM User")
    public abstract void deleteAllUsers();

    @Query("UPDATE User SET numOfWins=numOfWins + :win  WHERE id=:id1")
    public abstract  void updateNumOfWins(int win, long id1);

    @Query("UPDATE User SET numOfDefeats=numOfDefeats + :defeats  WHERE id=:id1")
    public abstract  void updateNumOfDefeats(int defeats, long id1);

    @Query("UPDATE User SET numOfUnsolved=numOfUnsolved + :unsolved  WHERE id=:id1")
    public abstract  void updateNumOfUnsolved(int unsolved, long id1);

    @Query("UPDATE User SET musicOn=:mus  WHERE id=:id1")
    public abstract  void updateMusicStatus(int mus, long id1);

    @Query("UPDATE User SET manualFight=:manFight  WHERE id=:id1")
    public abstract  void updateManualFightStatus(int manFight, long id1);

    @Query("UPDATE User SET numOfBoxes=numOfBoxes + :boxes  WHERE id=:id1")
    public abstract  void updateNumOfBoxes(int boxes, long id1);

}

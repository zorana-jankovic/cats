package com.example.zorana.cats.viewmodels;

import com.example.zorana.cats.database.entity.User;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    MutableLiveData<List<User>> users=new MutableLiveData<>();

    public MyViewModel(){
        users =new MutableLiveData<>();
        users.setValue(new ArrayList<User>());
    }

    public LiveData<List<User>> getUsersNames(){
        return users;
    }

    public void setUsersNames(List<User> names)
    {
        List<User> lista=users.getValue();
        lista.clear();
        lista.addAll(names);
        users.setValue(lista);

    }

    public void addUserName(String name){
        List<User> lista=users.getValue();
        lista.add(new User(name));
        users.setValue(lista);
    }
}

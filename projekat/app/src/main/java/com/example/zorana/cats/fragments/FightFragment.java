package com.example.zorana.cats.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zorana.cats.R;
import com.example.zorana.cats.game.GameController;
import com.example.zorana.cats.game.MyCustomImageView;


public class FightFragment extends Fragment {

    private static View view;
    private MyCustomImageView myCustomImageView;
    private GameController gameController;

    private  NavController navController;

    public FightFragment() {
        // Required empty public constructor
    }



       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_fight, container, false);
        myCustomImageView = view.findViewById(R.id.custom_image_view);

        gameController = new GameController(myCustomImageView, this);

        System.out.println("USAO U FIGHT FRAGMENBT");



        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GameController.nit.interrupt();

        //System.out.println("DESTROY U FIGHT FRAGMENBT");
    }



}

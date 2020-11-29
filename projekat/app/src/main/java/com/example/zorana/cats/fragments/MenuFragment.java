package com.example.zorana.cats.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.RoomDatabase;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zorana.cats.MainActivity;
import com.example.zorana.cats.R;
import com.example.zorana.cats.database.MyRoomDatabase;
import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.User;
import com.example.zorana.cats.dialogs.SettingsDialog;
import com.example.zorana.cats.dialogs.StatsDialog;

import java.util.ArrayList;


public class MenuFragment extends Fragment {

    private ImageView stats;
    private ImageView settings;
    private ImageView car;
    private ImageView boxes;
    private ImageView fight;

    private View view;

    //private static int music,manualFight;
    private static User actualUser;
    private static MyRoomDatabase myRoomDatabase;


    private static ArrayList<CarParts> deloviNaAutu;
    private static ArrayList<CarParts> rezervniDelovi;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        myRoomDatabase = MyRoomDatabase.getSingleton(getActivity());


        stats = view.findViewById(R.id.stats_icon);


        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PROVERI ZA DIALOG!!!
                StatsDialog stats = new StatsDialog(actualUser);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                stats.show(fragmentManager, "Stats");
            }
        });


        settings = view.findViewById(R.id.settings_icon);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PROVERI ZA DIALOG!!!
                SettingsDialog settings = new SettingsDialog();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                settings.show(fragmentManager, "Settings");
            }
        });


        car = view.findViewById(R.id.car_icon);
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_menuFragment_to_garageFragment);
            }
        });

        boxes = view.findViewById(R.id.boxes_icon);
        boxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_menuFragment_to_boxesFragment);
            }
        });

        fight = view.findViewById(R.id.fight_icon);
        fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deloviNaAutu.size() == 0)
                    return;
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_menuFragment_to_fightFragment);
            }
        });


        GarageFragment.setMenuFragment(this);
        BoxesFragment.setMenuFragment(this);

        updateDelove();



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.mediaPlayer != null && !MainActivity.mediaPlayer.isPlaying() && getMusic() == 1) {
                    MainActivity.mediaPlayer.start();
                }
            }
        }).start();


        return view;
    }

    public static int getMusic() {
        if (actualUser != null)
            return actualUser.getMusicOn();
        else
            return 0;

    }

    public static void setMusic(final int val) {
        actualUser.setMusicOn(val);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("UPDATE " + actualUser.getName());
                myRoomDatabase.userDao().updateMusicStatus(val, actualUser.getId());
            }
        }).start();

        //pusti ili ugasi

        if (val == 0) {
            if (MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying()) {
                MainActivity.mediaPlayer.pause();
            }
        } else {
            if (MainActivity.mediaPlayer != null && !MainActivity.mediaPlayer.isPlaying()) {
                MainActivity.mediaPlayer.start();
            }
        }
    }

    public static void setManualFight(final int val) {
        actualUser.setManualFight(val);

        new Thread(new Runnable() {
            @Override
            public void run() {
                myRoomDatabase.userDao().updateManualFightStatus(val, actualUser.getId());
            }
        }).start();
    }


    public static int getManualFight() {
        if (actualUser != null)
            return actualUser.getManualFight();
        else
            return 0;
    }




    @Override
    public void onStop() {
        super.onStop();
//        if(MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying()){
//            MainActivity.mediaPlayer.pause();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.pause();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        if(MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying()){
//            MainActivity.mediaPlayer.pause();
//        }
    }

    public static void setActualUser(User u) {
        actualUser = u;
        System.out.println("ACTUAL" + actualUser);
        BoxesFragment.setActualUser(u);
        if (actualUser != null && actualUser.getMusicOn() == 1) {
            MainActivity.mediaPlayer.seekTo(0);
            MainActivity.mediaPlayer.start();
        }

    }

    public static ArrayList<CarParts> getDeloviNaAutu() {
        return deloviNaAutu;
    }


    public static ArrayList<CarParts> getRezervniDelovi() {
        return rezervniDelovi;
    }

    public  void updateDelove() {
        if (actualUser != null){
            //System.out.println("ZVAO UPDATE");

            final Context context = getContext();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    deloviNaAutu = (ArrayList<CarParts>) myRoomDatabase.carPartsDao().getAllCarParts(actualUser.getId(), 1);
                    rezervniDelovi = (ArrayList<CarParts>) myRoomDatabase.carPartsDao().getAllCarParts(actualUser.getId(), 0);

                }
            }).start();

            // iscrtaj novo auto

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            iscrtajAuto(context);

        }

    }


    private  void iscrtajAuto(Context context) {

        if (deloviNaAutu.size() != 0) {

            // DODAJ DA UVEK PRVO SASIJU ISCRTAS

            float diffx = -0.18f;
            float diffy = -0.006f;

            for (int i = 0; i < deloviNaAutu.size(); i++) {
                float hB, vB;
                int idPart = (int) deloviNaAutu.get(i).getIdPart();

                if (idPart == 0) {
                    continue;
                } else {
                    hB = 0.514f + GarageFragment.hBias[idPart]  + 0.0353f;
                    vB = 0.747f + GarageFragment.vBias[idPart];
                }

                final ImageView imageView = makeNewImageView(view, idPart, 0, 0,
                        0, GarageFragment.width[idPart], GarageFragment.height[idPart], hB, vB, context);
            }
        }
    }

    private  ImageView makeNewImageView(View view, int id, int px, int py, int flag, float percentX, float percentY, float hBias, float vBias, Context context) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.constraint_layout_fragment_menu);
        ConstraintSet constraints = new ConstraintSet();

        constraints.clone(constraintLayout);
        // Define our ImageView and add it to layout
        ImageView imageView = new ImageView(context);
        imageView.setId(View.generateViewId());
        imageView.setTag(id);
        imageView.setImageResource(GarageFragment.mappingImagesIds[id]);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(px, py);
        imageView.setLayoutParams(layoutParams);

        constraintLayout.addView(imageView);
        // Now constrain the ImageView so it is centered on the screen.
        // There is also a "center" method that can be used here.
        if (flag == 1) {
            constraints.constrainWidth(imageView.getId(), px);
            constraints.constrainHeight(imageView.getId(), py);
        } else {
            constraints.constrainWidth(imageView.getId(), 0);
            constraints.constrainHeight(imageView.getId(), 0);
            constraints.constrainPercentWidth(imageView.getId(), percentY);
            constraints.constrainPercentHeight(imageView.getId(), percentX);
        }


        constraints.center(imageView.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                0, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, hBias);
        constraints.center(imageView.getId(), ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                0, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0, vBias);
        constraints.applyTo(constraintLayout);

        return imageView;
    }

    public static User getActualUser() {
        return actualUser;
    }

    public static MyRoomDatabase getMyRoomDatabase() {
        return myRoomDatabase;
    }
}

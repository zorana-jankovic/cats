package com.example.zorana.cats.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zorana.cats.R;
import com.example.zorana.cats.database.MyRoomDatabase;
import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.User;

import java.util.ArrayList;


public class GarageFragment extends Fragment {
    public static Integer[] mappingImagesIds = {R.drawable.sasija3, R.drawable.blade, R.drawable.chainsaw, R.drawable.forklift,
            R.drawable.rocket1, R.drawable.stinger, R.drawable.wheel, R.drawable.wheel};

    public static Float[] width = {0.28f, 0.15f, 0.15f, 0.15f, 0.09f, 0.18f, 0.09f, 0.09f};
    public static Float[] height = {0.28f, 0.13f, 0.13f, 0.1f, 0.09f, 0.15f, 0.09f, 0.09f};
    public static Float[] hBias = {0.694f, 0.008f, 0.126f, 0.133f, 0.048f, 0.144f, -0.113f, 0.048f};
    public static Float[] vBias = {0.753f, -0.04f, -0.043f, 0.025f, -0.066f, -0.038f, 0.062f, 0.062f};

    public static Integer[] attackMap = {0, 100, 110, 0, 60, 170, 0, 0};
    public static Integer[] healthMap = {700, 0, 0, 20, 0, 0 , 100, 100};
    public static Integer[] energyMap = {50, 20, 25, 20, 30, 35, 0, 0};

    /* MAPIRANJE:

        sasija = 0
        blade = 1
        sekira = 2
        forklift = 3
        raketa = 4
        stinger = 5
        tocakLevi = 6
        tocakDesni = 7

        */

    private User actualUser;
    private static ArrayList<CarParts> deloviNaAutu;
    private static ArrayList<CarParts> rezervniDelovi;

    private static int attack, health, energy;


    private float dx, dy; // za rectangle oko sasije


    private ImageView mapIcon;

    private static ImageView movingImage = null;
    private static ImageView clickedImageView;
    private static int clickedImageViewIndex;

    private TextView finish;
    private static MyRoomDatabase myRoomDatabase;

    private static MenuFragment menuFragment;

    private static TextView macMapa, srceMapa, munjaMapa;
    private static TextView macVozilo, srceVozilo, munjaVozilo;

    public GarageFragment() {
        // Required empty public constructor
    }


    private int windowwidth, windowheight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_garage, container, false);

        myRoomDatabase = MyRoomDatabase.getSingleton(getActivity());

        actualUser = MainFragment.dohvUsera();
        mapIcon = view.findViewById(R.id.map_icon);

        macMapa = view.findViewById(R.id.mapa_napad_broj);
        srceMapa = view.findViewById(R.id.mapa_zivoti_broj);
        munjaMapa = view.findViewById(R.id.mapa_energija_broj);

        macVozilo = view.findViewById(R.id.napad_trenutno_count);
        srceVozilo = view.findViewById(R.id.zivoti_trenutno_broj);
        munjaVozilo = view.findViewById(R.id.energija_trenutno_broj);

        deloviNaAutu = MenuFragment.getDeloviNaAutu();
        //sortirajDeloveNaAutu();
        rezervniDelovi = MenuFragment.getRezervniDelovi();

        attack = 0;
        health = 0;
        energy = 0;

        for (int i = 0 ; i < deloviNaAutu.size(); i++){
            int index = (int) deloviNaAutu.get(i).getIdPart();
            attack = attack + attackMap[index];
            health = health + healthMap[index];

            if (index == 0){
                energy = energy + energyMap[index];
            }else{
                energy = energy - energyMap[index];
            }
        }


        macMapa.setText("" + 0);
        srceMapa.setText("" + 0);
        munjaMapa.setText("" + 0);

        macVozilo.setText(""  + attack);
        srceVozilo.setText(""  + health);
        munjaVozilo.setText(""  + energy + "/" + energyMap[0]);


        final ConstraintLayout constraintLayout = view.findViewById(R.id.constraint_layout);
        windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        windowheight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        dx = (float) ((0.28 / windowwidth) / 2);
        dy = (float) ((0.28 / windowheight) / 2);


        final LinearLayout linearLayout = view.findViewById(R.id.layout_hor_scroll);

        iscrtajAuto(view,linearLayout,constraintLayout);

        float dp = 60;
        final int px = dpTopx(dp);

        HorizontalScrollView horizontalScrollView = view.findViewById(R.id.horizontal_scroll);
        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view1, MotionEvent motionEvent) {
                float x_cord = motionEvent.getRawX();
                float y_cord = motionEvent.getRawY();
                if (y_cord / windowheight < 0.3) {
                    return false;
                }
                if (movingImage == null && clickedImageView == null) {
                    return false;
                }
                switch (motionEvent.getAction()) {
                    //case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:

                        if (movingImage == null && clickedImageView != null) {
                            linearLayout.removeView(clickedImageView);
                            movingImage = makeNewImageView(view, (Integer) clickedImageView.getTag(), px, px,
                                    1, 0, 0, x_cord / windowwidth, y_cord / windowheight);

                        }
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) movingImage.getLayoutParams();
                        if (x_cord > windowwidth) {
                            x_cord = windowwidth;
                        }
                        if (y_cord > windowheight) {
                            y_cord = windowheight;
                        }

                        layoutParams.horizontalBias = x_cord / windowwidth;
                        layoutParams.verticalBias = y_cord / windowheight;

                        movingImage.setLayoutParams(layoutParams);
                        break;

                    case MotionEvent.ACTION_UP:
                        proveriDaLiSeSpusta(x_cord, y_cord, movingImage, linearLayout, constraintLayout, view);
//                        linearLayout.addView(clickedImageView);
//                        constraintLayout.removeView(movingImage);
                        movingImage = null;
                        clickedImageView = null;
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        for (int i = 0; i < rezervniDelovi.size(); i++) {
            System.out.println("rezervni deo: " + (int)rezervniDelovi.get(i).getIdPart());
            makeNewComponentInHorizontalView((int)rezervniDelovi.get(i).getIdPart(), px, linearLayout, i);
        }

        finish = view.findViewById(R.id.text_finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDatabase();
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_garageFragment_to_menuFragment);
            }
        });


        return view;
    }

    private void iscrtajAuto(View view,final LinearLayout linearLayout,final ConstraintLayout constraintLayout) {

        if (deloviNaAutu.size() != 0) {

            // DODAJ DA UVEK PRVO SASIJU ISCRTAS

            sortirajDeloveNaAutu();

            for (int i = 0; i < deloviNaAutu.size(); i++) {
                float hB, vB;
                final int idPart = (int) deloviNaAutu.get(i).getIdPart();

                if (idPart == 0) {
                    hB = hBias[idPart];
                    vB = vBias[idPart];
                } else {
                    hB = hBias[0] + hBias[idPart];
                    vB = vBias[0] + vBias[idPart];
                }

                final ImageView imageView = makeNewImageView(view, idPart, 0, 0,
                        0, width[idPart], height[idPart], hB, vB);

                final int index = i;

                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        float x_cord = motionEvent.getRawX();
                        float y_cord = motionEvent.getRawY();

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mapIcon.setImageDrawable(imageView.getDrawable());
                                macMapa.setText("" + attackMap[idPart]);
                                srceMapa.setText("" + healthMap[idPart]);
                                munjaMapa.setText("" + energyMap[idPart]);
                                break;

                            case MotionEvent.ACTION_MOVE:
                                if ((Integer) imageView.getTag() != 0) {
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                                    if (x_cord > windowwidth) {
                                        x_cord = windowwidth;
                                    }
                                    if (y_cord > windowheight) {
                                        y_cord = windowheight;
                                    }

                                    layoutParams.horizontalBias = x_cord / windowwidth;
                                    layoutParams.verticalBias = y_cord / windowheight;
                                    imageView.setLayoutParams(layoutParams);
                                    break;

                                }

                            case MotionEvent.ACTION_UP:
                                if (y_cord / windowheight < 0.38) {
                                    //izbaci
                                    if ((Integer) imageView.getTag() != 0) {
                                        constraintLayout.removeView(imageView);
                                        makeNewComponentInHorizontalView((Integer) imageView.getTag(),
                                                dpTopx(60), linearLayout, rezervniDelovi.size());
                                        deloviNaAutu.get(index).setOnCar(0);
                                        rezervniDelovi.add(deloviNaAutu.remove(index));
                                        deloviNaAutu.add(index, null);

                                       // sortirajDeloveNaAutu();

                                        attack = attack - attackMap[idPart];
                                        health = health - healthMap[idPart];
                                        energy = energy + energyMap[idPart];

                                        macVozilo.setText("" + attack);
                                        srceVozilo.setText("" + health);
                                        munjaVozilo.setText(""  + energy + "/" + energyMap[0]);
                                    }
                                } else {
                                    if ((Integer) imageView.getTag() != 0) {
                                        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                                        layoutParams1.horizontalBias = hBias[0] + hBias[(Integer) imageView.getTag()];
                                        layoutParams1.verticalBias = vBias[0] + vBias[(Integer) imageView.getTag()];
                                        imageView.setLayoutParams(layoutParams1);
                                    }
                                }

                                break;
                        }
                        return true;
                    }
                });

                //constraintLayout.removeView(movingImage);
            }
        }
    }

    private void sortirajDeloveNaAutu() {
        ArrayList<CarParts> pomocniArr = new ArrayList<>();

        for(int i = 0 ; i < deloviNaAutu.size(); i++){
            if (deloviNaAutu.get(i) != null) {
                if (deloviNaAutu.get(i).getIdPart() == 0) {
                    pomocniArr.add(deloviNaAutu.get(i));
                }
            }
        }

        for(int i = 0 ; i < deloviNaAutu.size(); i++){
            if (deloviNaAutu.get(i) != null) {
                if (deloviNaAutu.get(i).getIdPart() >= 6) {
                    pomocniArr.add(deloviNaAutu.get(i));
                }
            }
        }

        for(int i = 0 ; i < deloviNaAutu.size(); i++){
            if (deloviNaAutu.get(i) != null) {
                if (deloviNaAutu.get(i).getIdPart() != 0 && deloviNaAutu.get(i).getIdPart() < 6) {
                    pomocniArr.add(deloviNaAutu.get(i));
                }
            }
        }

        deloviNaAutu = new ArrayList<>();

        for(int i = 0 ; i < pomocniArr.size(); i++){
            deloviNaAutu.add(pomocniArr.get(i));
        }
    }

    private void updateDatabase() {

        for (int i = 0; i < deloviNaAutu.size(); i++) {
            if (deloviNaAutu.get(i) == null)
                continue;
            final int ind = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myRoomDatabase.carPartsDao().updateCarPart(deloviNaAutu.get(ind).getId(), 1, deloviNaAutu.get(ind).getIdPart());
                }
            }).start();

            System.out.println("Deo na autu rbr:" + i + "id dela:" + deloviNaAutu.get(i).getIdPart());
        }

        for (int i = 0; i < rezervniDelovi.size(); i++) {
            if (rezervniDelovi.get(i) == null)
                continue;
            final int ind = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myRoomDatabase.carPartsDao().updateCarPart(rezervniDelovi.get(ind).getId(), 0, rezervniDelovi.get(ind).getIdPart());
                }
            }).start();
            //System.out.println("Rezervni Deo za auto rbr:" + i + "id dela:" + rezervniDelovi.get(i).getIdPart());
        }

        if (menuFragment == null)
            System.out.println("MENU FRAG JE NULL PROBLEM");
        menuFragment.updateDelove();
    }

    private void makeNewComponentInHorizontalView(int i, int px, LinearLayout linearLayout, final int ind) {

        final ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(mappingImagesIds[i]);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(px, px);
        layoutParams.setMargins(20, 3, 20, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setTag(i);
        imageView.setId(View.generateViewId());
        linearLayout.addView(imageView);

        final int indexPart = i;

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("down");
                        clickedImageView = imageView;
                        clickedImageViewIndex = ind;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        System.out.println("up");
                        mapIcon.setImageDrawable(imageView.getDrawable());

                        macMapa.setText("" + attackMap[indexPart]);
                        srceMapa.setText("" + healthMap[indexPart]);
                        munjaMapa.setText("" + energyMap[indexPart]);

                        clickedImageView = null;
                        clickedImageViewIndex = -1;
                        break;
                }

                return true;
            }
        });

    }

    private void proveriDaLiSeSpusta(float x_cord, float y_cord, ImageView movingImage, final LinearLayout linearLayout, final ConstraintLayout constraintLayout, View view) {

        dx = (float) ((0.28 * windowwidth) / 2);
        dy = (float) ((0.28 * windowheight) / 2);


        if (x_cord >= (hBias[0] * windowwidth - dx) && x_cord <= (hBias[0] * windowwidth + dx)
                && y_cord >= (vBias[0] * windowheight - dy) && y_cord <= (vBias[0] * windowheight + dy)) {

            int idImageView = (Integer) movingImage.getTag();
            int i = 0;
            int br = 0;

            if (idImageView >= 6) {
                float sredina = hBias[0] * windowwidth;

                System.out.println("Sredina: " + sredina);
                System.out.println("x coord :" + x_cord);

                if (x_cord < sredina) { // levi tocak
                    idImageView = 6;
                } else { // desni tocak
                    idImageView = 7;
                }
            }


            for (i = 0; i < deloviNaAutu.size(); i++) {
                if (deloviNaAutu.get(i) == null)
                    continue;
                if (deloviNaAutu.get(i).getIdPart() == idImageView) {
                    br++;
                }

                if (idImageView == 1 && (deloviNaAutu.get(i).getIdPart() == 2
                        || deloviNaAutu.get(i).getIdPart() == 4 || deloviNaAutu.get(i).getIdPart() == 5)) {
                    br++;
                }

                if (idImageView == 2 && (deloviNaAutu.get(i).getIdPart() == 1
                        || deloviNaAutu.get(i).getIdPart() == 4 || deloviNaAutu.get(i).getIdPart() == 5)) {
                    br++;
                }

                if (idImageView == 4 && (deloviNaAutu.get(i).getIdPart() == 1
                        || deloviNaAutu.get(i).getIdPart() == 2 || deloviNaAutu.get(i).getIdPart() == 5)) {
                    br++;
                }

                if (idImageView == 5 && (deloviNaAutu.get(i).getIdPart() == 1
                        || deloviNaAutu.get(i).getIdPart() == 2 || deloviNaAutu.get(i).getIdPart() == 4)) {
                    br++;
                }
            }

            int ok = 0;

            if (br > 0) {
                ok = 0;
            } else {
                ok = 1;
            }

            if (idImageView > 0) {
                if (deloviNaAutu.size() == 0) {
                    ok = 0;
                }

                if (energy - energyMap[idImageView] < 0){
                    System.out.println("NEMA DOVOLJNO ENERGIJE DA BI SPUSTIO DEO");
                    ok = 0;
                }
            }

            final int index = deloviNaAutu.size();



            if (ok == 1) { // nema ga jos uvek, stavi ga
                rezervniDelovi.get(clickedImageViewIndex).setIdPart(idImageView);
                rezervniDelovi.get(clickedImageViewIndex).setOnCar(1);
                deloviNaAutu.add(rezervniDelovi.remove(clickedImageViewIndex));
                rezervniDelovi.add(clickedImageViewIndex, null);

                //sortirajDeloveNaAutu();


                attack = attack + attackMap[idImageView];
                health = health + healthMap[idImageView];
                if (idImageView == 0){
                    energy = energy + energyMap[idImageView];
                }else{
                    energy = energy - energyMap[idImageView];
                }


                macVozilo.setText("" + attack);
                srceVozilo.setText("" + health);
                munjaVozilo.setText(""  + energy + "/" + energyMap[0]);



                // da li ovde da update bazu ili bolje  na back button


                final ImageView noviDeo;

                if (idImageView == 0) {
                    noviDeo = makeNewImageView(view, idImageView, 0, 0, 0,
                            width[idImageView], height[idImageView], hBias[idImageView], vBias[idImageView]);
                } else {

                    noviDeo = makeNewImageView(view, idImageView, 0, 0, 0,
                            width[idImageView], height[idImageView], hBias[0] + hBias[idImageView], vBias[0] + vBias[idImageView]);
                }


                final int indexPart = idImageView;

                noviDeo.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        float x_cord = motionEvent.getRawX();
                        float y_cord = motionEvent.getRawY();

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mapIcon.setImageDrawable(noviDeo.getDrawable());

                                macMapa.setText("" + attackMap[indexPart]);
                                srceMapa.setText("" + healthMap[indexPart]);
                                munjaMapa.setText("" + energyMap[indexPart]);

                                break;

                            case MotionEvent.ACTION_MOVE:
                                if ((Integer) noviDeo.getTag() != 0) {
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) noviDeo.getLayoutParams();
                                    if (x_cord > windowwidth) {
                                        x_cord = windowwidth;
                                    }
                                    if (y_cord > windowheight) {
                                        y_cord = windowheight;
                                    }

                                    layoutParams.horizontalBias = x_cord / windowwidth;
                                    layoutParams.verticalBias = y_cord / windowheight;
                                    noviDeo.setLayoutParams(layoutParams);
                                    break;

                                }

                            case MotionEvent.ACTION_UP:
                                if (y_cord / windowheight < 0.38) {
                                    //izbaci
                                    if ((Integer) noviDeo.getTag() != 0) {
                                        constraintLayout.removeView(noviDeo);
                                        makeNewComponentInHorizontalView((Integer) noviDeo.getTag(), dpTopx(60), linearLayout, rezervniDelovi.size());
                                        deloviNaAutu.get(index).setOnCar(0);
                                        rezervniDelovi.add(deloviNaAutu.remove(index));
                                        deloviNaAutu.add(index, null);

                                       // sortirajDeloveNaAutu();

                                        attack = attack - attackMap[indexPart];
                                        health = health - healthMap[indexPart];
                                        energy = energy + energyMap[indexPart];

                                        macVozilo.setText("" + attack);
                                        srceVozilo.setText("" + health);
                                        munjaVozilo.setText(""  + energy + "/" + energyMap[0]);

                                    }
                                } else {
                                    if ((Integer) noviDeo.getTag() != 0) {
                                        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) noviDeo.getLayoutParams();
                                        layoutParams1.horizontalBias = hBias[0] + hBias[(Integer) noviDeo.getTag()];
                                        layoutParams1.verticalBias = vBias[0] + vBias[(Integer) noviDeo.getTag()];
                                        noviDeo.setLayoutParams(layoutParams1);
                                    }
                                }

                                break;
                        }
                        return true;
                    }
                });

                constraintLayout.removeView(movingImage);


                // constraintLayout.addView(noviDeo);
//                movingImage = null;
//                clickedImageView = null;

            } else {
                if (clickedImageView.getParent() != null) {
                    ((ViewGroup) clickedImageView.getParent()).removeView(clickedImageView);
                }
                linearLayout.addView(clickedImageView);
                constraintLayout.removeView(movingImage);
//                movingImage = null;
//                clickedImageView = null;
            }
        } else {
            if (clickedImageView.getParent() != null) {
                ((ViewGroup) clickedImageView.getParent()).removeView(clickedImageView);
            }
            linearLayout.addView(clickedImageView);
            constraintLayout.removeView(movingImage);
        }
    }


    private int dpTopx(float dip) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return px;
    }

    private ImageView makeNewImageView(View view, int id, int px, int py, int flag, float percentX, float percentY, float hBias, float vBias) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.constraint_layout);
        ConstraintSet constraints = new ConstraintSet();

        constraints.clone(constraintLayout);
        // Define our ImageView and add it to layout
        ImageView imageView = new ImageView(getContext());
        imageView.setId(View.generateViewId());
        imageView.setTag(id);
        imageView.setImageResource(mappingImagesIds[id]);
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

    public static MenuFragment getMenuFragment() {
        return menuFragment;
    }

    public static void setMenuFragment(MenuFragment men) {
        System.out.println("POSTAVLJAM MENU FRAGGG");
        menuFragment = men;
    }

    public static int getAttack() {
        return attack;
    }

    public static void setAttack(int attack) {
        GarageFragment.attack = attack;
    }

    public static int getHealth() {
        return health;
    }

    public static void setHealth(int health) {
        GarageFragment.health = health;
    }

    public static int getEnergy() {
        return energy;
    }

    public static void setEnergy(int energy) {
        GarageFragment.energy = energy;
    }
}

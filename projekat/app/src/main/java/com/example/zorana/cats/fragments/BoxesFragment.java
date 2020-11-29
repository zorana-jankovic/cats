package com.example.zorana.cats.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zorana.cats.R;
import com.example.zorana.cats.database.MyRoomDatabase;
import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.Present;
import com.example.zorana.cats.database.entity.User;

import java.util.ArrayList;


public class BoxesFragment extends Fragment {

    private static View view;
    private static ImageView box1, box2, box3;
    private static TextView textBox1, textBox2, textBox3;


    private static ImageView[] boxes = new ImageView[3];
    private static TextView[] texts = new TextView[3];
    private static ImageView[] pokloniSlike = new ImageView[3];

    public static User actualUser;
    private static MyRoomDatabase myRoomDatabase;

    private static ArrayList<CarParts> deloviNaAutu;
    private static ArrayList<CarParts> rezervniDelovi;

    private static ArrayList<Present> pokloni;

    private static MenuFragment menuFragment;

    private Thread nit;

    private TextView progresZaKutije;

    public BoxesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_boxes, container, false);
        myRoomDatabase = MyRoomDatabase.getSingleton(getActivity());

        actualUser = MainFragment.dohvUsera();

        deloviNaAutu = MenuFragment.getDeloviNaAutu();
        rezervniDelovi = MenuFragment.getRezervniDelovi();


        box1 = view.findViewById(R.id.box1);
        box2 = view.findViewById(R.id.box2);
        box3 = view.findViewById(R.id.box3);

        textBox1 = view.findViewById(R.id.box1_time);
        textBox2 = view.findViewById(R.id.box2_time);
        textBox3 = view.findViewById(R.id.box3_time);

        pokloniSlike[0] = view.findViewById(R.id.poklon1);
        pokloniSlike[1] = view.findViewById(R.id.poklon2);
        pokloniSlike[2] = view.findViewById(R.id.poklon3);

        for(int i = 0 ; i < pokloniSlike.length; i++){
            pokloniSlike[i].setAlpha(0f);
        }

        boxes[0] = box1;
        boxes[1] = box2;
        boxes[2] = box3;

        texts[0] = textBox1;
        texts[1] = textBox2;
        texts[2] = textBox3;

        progresZaKutije = view.findViewById(R.id.zaKutije);
        int num = 0;
        int pom = actualUser.getNumOfWins();

        if (pom % 3 == 0) {
            num = 0;
        }
        if (pom % 3 == 1) {
            num = 1;
        }
        if (pom % 3 == 2) {
            num = 2;
        }


        progresZaKutije.setText("Wins for new box: " + num + "/" + 3);

        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pokloni.size() < 1 ){
                    return;
                }

                if (!textBox1.getText().equals("OPEN")){
                    return;
                }
                otvoriKutiju();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myRoomDatabase.presentDao().deletePresent(pokloni.get(0).getId());
                        myRoomDatabase.userDao().updateNumOfBoxes(-1, actualUser.getId());
                        actualUser.setNumOfBoxes(actualUser.getNumOfBoxes()-1);
                        // PROVERI OVO !!!
                    }
                }).start();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                dohvatiPoklone();
                box1.setClickable(false);
            }
        });

        box2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pokloni.size() < 2 ){
                    return;
                }
                if (!textBox2.getText().equals("OPEN")){
                    return;
                }

                otvoriKutiju();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // puca ovde proveri
                        myRoomDatabase.presentDao().deletePresent(pokloni.get(1).getId());
                        myRoomDatabase.userDao().updateNumOfBoxes(-1, actualUser.getId());
                        actualUser.setNumOfBoxes(actualUser.getNumOfBoxes()-1);
                        // PROVERI OVO !!!
                    }
                }).start();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                dohvatiPoklone();
                box2.setClickable(false);
            }
        });

        box3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pokloni.size() < 3 ){
                    return;
                }
                if (!textBox3.getText().equals("OPEN")){
                    return;
                }

                otvoriKutiju();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myRoomDatabase.presentDao().deletePresent(pokloni.get(2).getId());
                        myRoomDatabase.userDao().updateNumOfBoxes(-1, actualUser.getId());
                        actualUser.setNumOfBoxes(actualUser.getNumOfBoxes()-1);
                        // PROVERI OVO !!!
                    }
                }).start();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                dohvatiPoklone();
                box3.setClickable(false);
            }
        });

        dohvatiPoklone();

       // box1.setClickable(true);

        nit = new Thread(new Runnable() {
            long start = System.currentTimeMillis();

            @Override
            public void run() {
                while (!nit.isInterrupted()) {
                    long end = System.currentTimeMillis();
                    if (end - start >= 1000) {
                        start = end;

                        long now = System.currentTimeMillis();
                        synchronized (BoxesFragment.class) {
                            for (int i = 0; i < pokloni.size(); i++) {
                                long targetTime = pokloni.get(i).getTime();


                                if (now - targetTime >= 0) { // isteklo vreme!
                                    final int index = i;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            texts[index].setText("OPEN");
                                            boxes[index].setClickable(true);
                                        }
                                    });
                                } else {
                                    long count = targetTime - now;

                                    long sec = count / 1000;
                                    long min = sec / 60;
                                    long h = min / 60;

                                    final int hh = (int) h;
                                    final int mm = (int) (min - h * 60);
                                    final int ss = (int) (sec - (hh * 60 + mm) * 60);

                                    final int index = i;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            texts[index].setText(hh + ":" + mm + ":" + ss);
                                        }
                                    });
                                }
                            }
                            for (int i = pokloni.size(); i < boxes.length; i++) {
                                final int index = i;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        texts[index].setText("");
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        nit.start();

        return view;
    }

    private static void dohvatiPoklone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pokloni = (ArrayList) myRoomDatabase.presentDao().getAllPresents(actualUser.getId());
            }
        }).start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // iscrtaj ih

        iscrtajPoklone();
    }

    private static long getTimeInMils(int mins) {
        long now = System.currentTimeMillis();
        now = (now + mins * 60  * 1000);

        return now;
    }

    private static void iscrtajPoklone() {

        synchronized (BoxesFragment.class) {
            System.out.println("POKLONI " + pokloni.size());
            for (int i = 0; i < pokloni.size(); i++) {
                boxes[i].setAlpha(1f);

                long targetTime = pokloni.get(i).getTime();
                long now = System.currentTimeMillis();

                if (now - targetTime >= 0) { // isteklo vreme!
                    texts[i].setText("OPEN");
                    boxes[i].setClickable(true);
                } else {
                    long count = targetTime - now;

                    long sec = count / 1000;
                    long min = sec / 60;
                    long h = min / 60;

                    int hh = (int) h;
                    int mm = (int) (min - h * 60);
                    int ss = (int) (sec - (hh * 60 + mm) * 60);


                    texts[i].setText("" + hh + ':' + mm + ':' + ss);
                    //texts[i].setText(String.format("%d:%d:%d", hh, mm, ss));

                    boxes[i].setClickable(false);
                }

            }

            for (int i = pokloni.size(); i < boxes.length; i++) {
                System.out.println(" setujem " + i);
                boxes[i].setAlpha(0.3f);
                texts[i].setText("");
                boxes[i].setClickable(false);
            }
        }
    }

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
    private void otvoriKutiju() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                int idPart1 = 0;
                int idPart2 = 0;
                int idPart3 = 0;

                if (rezervniDelovi.size() > 0 || deloviNaAutu.size() > 0) {
                    idPart1 = (int) (Math.random() * 8);
                    idPart2 = (int) (Math.random() * 8);
                    idPart3 = (int) (Math.random() * 8);
                }else {
                    idPart1 = (int) (0); //sasija
                    idPart2 = (int) (Math.random() * 8); //oruzije
                    while (idPart2 != 1 && idPart2 != 2 && idPart2 != 4 && idPart2 != 5){
                        idPart2 = (int) (Math.random() * 8);
                    }
                    idPart3 = (int) (6); //tocak
                }

                CarParts part1 = new CarParts(idPart1, actualUser.getId(), 0);
                long id1 = myRoomDatabase.carPartsDao().insert(part1);
                part1.setId(id1);

                CarParts part2 = new CarParts(idPart2, actualUser.getId(), 0);
                long id2 = myRoomDatabase.carPartsDao().insert(part2);
                part1.setId(id2);

                CarParts part3 = new CarParts(idPart3, actualUser.getId(), 0);
                long id3 = myRoomDatabase.carPartsDao().insert(part3);
                part1.setId(id3);



                rezervniDelovi.add(part1);
                rezervniDelovi.add(part2);
                rezervniDelovi.add(part3);

                final int ind1 = idPart1;
                final int ind2 = idPart2;
                final int ind3 = idPart3;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pokloniSlike[0].setImageResource(GarageFragment.mappingImagesIds[ind1]);
                        pokloniSlike[1].setImageResource(GarageFragment.mappingImagesIds[ind2]);
                        pokloniSlike[2].setImageResource(GarageFragment.mappingImagesIds[ind3]);

                        pokloniSlike[0].setAlpha(1f);
                        pokloniSlike[1].setAlpha(1f);
                        pokloniSlike[2].setAlpha(1f);
                    }
                });


            }
        }).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // trebalo bi da se iz onCreate zove update u MenuFragmentu

        menuFragment.updateDelove();
    }



    public static void napraviPoklon(final int time) {
        if (actualUser.getNumOfBoxes() >= 3) {
            return;
        }

        actualUser.setNumOfBoxes(actualUser.getNumOfBoxes() + 1);

        if (myRoomDatabase == null)
            myRoomDatabase = MainFragment.myRoomDatabase;

        new Thread(new Runnable() {
            @Override
            public void run() {
                myRoomDatabase.userDao().updateNumOfBoxes(1, actualUser.getId());

                Present present = new Present(actualUser.getId(), getTimeInMils(time));
                long prId = myRoomDatabase.presentDao().insert(present);
                present.setId(prId);
                System.out.println("POKLON VREME:" + present.getTime() );
            }
        }).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (time == 0)
            return;

        //dohvatiPoklone();

    }


    public static void setMenuFragment(MenuFragment m) {
        menuFragment = m;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nit.interrupt();
    }

    public static void setActualUser(User actualUser) {
        BoxesFragment.actualUser = actualUser;
    }

    public static User getActualUser() {
        return actualUser;
    }
}

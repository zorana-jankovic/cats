package com.example.zorana.cats.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zorana.cats.MainActivity;
import com.example.zorana.cats.R;
import com.example.zorana.cats.database.MyRoomDatabase;
import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.User;
import com.example.zorana.cats.recyclerview.ExampleAdapter;
import com.example.zorana.cats.viewmodels.MyViewModel;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private View view;
    private Button buttonStart;
    private LinearLayoutManager layoutManager;
    private MyViewModel myViewModel;
    public static  MyRoomDatabase myRoomDatabase;
    private EditText editText;
    private static User user;
    private ImageView deleteIcon;


    //  private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    private static int proveraNullUser = 0;

    public static int getProveraNullUser() {
        return proveraNullUser;
    }

    public static void setProveraNullUser(int proveraNullUser) {
        MainFragment.proveraNullUser = proveraNullUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        myViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        myRoomDatabase = MyRoomDatabase.getSingleton(getActivity());

        editText = view.findViewById(R.id.player_name);

        deleteIcon = view.findViewById(R.id.delete_user_icon);



//        myRoomDatabase.userDao().getAllUsers().observe(this, new Observer<List<User>>() {
//            @Override
//            public void onChanged(List<User> users) {
//                myViewModel.setUsersNames(users);
//            }
//        });


        //editText.setText("");

        recyclerView = view.findViewById(R.id.moj_recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);



        List<User> usersNames = new ArrayList<>();
        final ExampleAdapter exampleAdapter = new ExampleAdapter(usersNames, this, editText);
        recyclerView.setAdapter(exampleAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final List<User> usersNames2 = myRoomDatabase.userDao().getAllUsers();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exampleAdapter.addList(usersNames2);
                        exampleAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


//        usersNames.observe(this, new Observer<List<User>>() {
//            @Override
//            public void onChanged(List<User> korisnici) {
//                for (int i = 0; i < korisnici.size(); i++) {
//                    System.out.println(korisnici.get(i).getName());
//                }
//                exampleAdapter.addList(korisnici);
//                exampleAdapter.notifyDataSetChanged();
//            }
//        });

        buttonStart = view.findViewById(R.id.button_start);


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ok = 1;
                final String ime = editText.getText().toString();

                if (ime.equals(""))
                    return;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final User user1 = myRoomDatabase.userDao().getUser(ime);
                        System.out.println("USAO U userget!!!" + user1);
                        if (user1 == null) {
                            setProveraNullUser(1);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    user = new User(ime);
                                    MenuFragment.setActualUser(user);
                                    long id = myRoomDatabase.userDao().insert(user);
                                    user.setId(id);

                                    // pravi delove

//                                    for (int i = 0; i < 14; i++) {
//                                        myRoomDatabase.carPartsDao().insert(new CarParts(i % 7, id, 0));
//                                    }
                                    BoxesFragment.napraviPoklon(0);

                                }
                            }).start();

                        } else {
                            setProveraNullUser(0);
                            user = user1;
                            MenuFragment.setActualUser(user);
                        }
                        }
                }).start();



                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_mainFragment_to_menuFragment);

                editText.setText("");
            }
        });




        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("delete listener");
                final String ime = editText.getText().toString();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("delete thread");
                        if (!ime.equals("")) {
                            User user = myRoomDatabase.userDao().getUser(ime);
                            // sta ako nema usera
                            if (user != null) {
                                System.out.println("BRISE USERA :" + user.getName());
                                myRoomDatabase.presentDao().deletePresentForUser(user.getId());
                                // jel ce ovo obrisati sve za tog usera?
                                myRoomDatabase.carPartsDao().deleteCarPartForUser(user.getId());
                                myRoomDatabase.userDao().deleteOneUser(user.getName());
                            }else{
                                System.out.println("USER MI JE NULLLLLLLLLL");
                            }

                            final List<User> usersNames1 = myRoomDatabase.userDao().getAllUsers();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    exampleAdapter.addList(usersNames1);
                                    exampleAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                        //editText.setText("");
                    }
                }).start();

                if (!editText.getText().toString().equals("")) {
                    editText.setText("");
                }
            }
        });


        return view;
    }


    public static User dohvUsera() {
        return user;
    }


}

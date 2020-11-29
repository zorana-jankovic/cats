package com.example.zorana.cats.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.zorana.cats.R;
import com.example.zorana.cats.database.MyRoomDatabase;
import com.example.zorana.cats.database.entity.Present;
import com.example.zorana.cats.database.entity.User;
import com.example.zorana.cats.fragments.BoxesFragment;
import com.example.zorana.cats.fragments.FightFragment;
import com.example.zorana.cats.fragments.MenuFragment;
import com.example.zorana.cats.game.MyCustomImageView;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class FinishGameDialog extends DialogFragment {
    private User user;
    private int winner; // 0 - unsolved, 1- win, -1 - defeat
    private MyRoomDatabase myRoomDatabase;
    private FightFragment fightFragment;
    private Thread nit;
    private MyCustomImageView myCustomImageView;

    public FinishGameDialog(User user, int winner, FightFragment fightFragment, Thread nit, MyCustomImageView myCustomImageView){

//        this.user = user;
//        this.winner = winner;

        this.user = MenuFragment.getActualUser();
        this.winner = winner;
        myRoomDatabase = MenuFragment.getMyRoomDatabase();
        this.fightFragment = fightFragment;
        this.nit = nit;
        this.myCustomImageView = myCustomImageView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (winner == 0){
                    myRoomDatabase.userDao().updateNumOfUnsolved(1,user.getId());
                }

                if (winner == -1){
                    myRoomDatabase.userDao().updateNumOfDefeats(1,user.getId());
                }

                if (winner == 1){
                    myRoomDatabase.userDao().updateNumOfWins(1,user.getId());
                }

                if (user.getNumOfWins() % 3 == 0 && winner == 1){ //NAPRAVI NOVI POKLON
                    BoxesFragment.napraviPoklon(60);
                }

            }
        }).start();

        if (winner == 0){
            user.setNumOfUnsolved(user.getNumOfUnsolved() + 1);
        }

        if (winner == -1){
            user.setNumOfDefeats(user.getNumOfDefeats() + 1);
        }

        if (winner == 1){
            user.setNumOfWins(user.getNumOfWins() + 1);
        }


        CharSequence wins = "WINS: " + user.getNumOfWins();
        CharSequence defeats = "DEFEATS: " + user.getNumOfDefeats();
        CharSequence unsolved = "UNSOLVED: " + user.getNumOfUnsolved();

        CharSequence[] items = {wins,defeats,unsolved};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //novo
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View inflate = inflater.inflate(R.layout.stats_layout, null);

        TextView winsText = inflate.findViewById(R.id.stats_wins);
        TextView losesText = inflate.findViewById(R.id.stats_loses);
        TextView unsolvedText = inflate.findViewById(R.id.stats_unsolved);

        winsText.setText(wins);
        losesText.setText(defeats);
        unsolvedText.setText(unsolved);



        builder.setView(inflate)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        fightFragment.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NavController navController = Navigation.findNavController(myCustomImageView);
                                navController.navigate(R.id.action_fightFragment_to_menuFragment);
                            }
                        });
                    }
                });

//        builder.setTitle("STATS").setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // samo da nestane
//                    }
//                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

package com.example.zorana.cats.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.zorana.cats.R;
import com.example.zorana.cats.database.entity.User;

import org.w3c.dom.Text;

import androidx.fragment.app.DialogFragment;

public class StatsDialog extends DialogFragment {
    private User user;

    public StatsDialog(User user){
        this.user = user;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
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

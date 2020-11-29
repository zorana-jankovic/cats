package com.example.zorana.cats.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.zorana.cats.R;
import com.example.zorana.cats.fragments.MainFragment;
import com.example.zorana.cats.fragments.MenuFragment;

import java.util.ArrayList;

import androidx.fragment.app.DialogFragment;

public class SettingsDialog extends DialogFragment{

    private int music = 0, manualFight = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //music = manualFight = 0;  // Where we track the selected items
        music = MenuFragment.getMusic();
        manualFight = MenuFragment.getManualFight();
        boolean[] values = new boolean[2];
        if(music == 1){
            values[0] = true;
        }else{
            values[0] = false;
        }
        if(manualFight == 1){
            values[1] = true;
        }else{
            values[1] = false;
        }
        CharSequence[] items = {"Music", "Manual Fight"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("SETTINGS")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(items, values,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                //if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    System.out.println("WHICH " + which);
                                    if (which == 0){
                                        if (music == 0) {
                                            music = 1;
                                        }else{
                                            music = 0;
                                        }
                                    }
                                    if (which == 1){
                                        if(manualFight == 0) {
                                            manualFight = 1;
                                        }else{
                                            manualFight = 0;
                                        }
                                    }

                                }
                           // }
                        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        MenuFragment.setMusic(music);
                        MenuFragment.setManualFight(manualFight);

                        System.out.println("MUSIC VALUE + " +music);
                        System.out.println("MANUAL FIGHT VALUE + " +manualFight);
                    }
                });

        AlertDialog alertDialog = builder.create();

        return alertDialog;
    }
}

package com.example.zorana.cats.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zorana.cats.R;
import com.example.zorana.cats.database.entity.User;
import com.example.zorana.cats.fragments.MainFragment;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.MyViewHolder> {
    private List<User> users;
    private  static MainFragment mainFragment;
    private static EditText playerName;

    public ExampleAdapter(List<User> users, MainFragment mainFragment, EditText playerName){
        this.users=users;
        this.mainFragment=mainFragment;
        this.playerName = playerName;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public MyViewHolder(final View v) {
            super(v);
            cardView =(CardView) v;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playerName.setText(((TextView)cardView.findViewById(R.id.ime_igraca)).getText());
                    System.out.println(playerName.getText().toString());
                    System.out.println(playerName);
                }
            });
        }

        public CardView getCardView(){
            return cardView;
        }
    }



    // Create new views (invoked by the layout manager)
    @Override
    public ExampleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CardView cardView=holder.getCardView();
        TextView ime=cardView.findViewById(R.id.ime_igraca);

        ime.setText(users.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(users!=null)
            return users.size();
        return 0;
    }

    public void addList(List<User> lista){
        users=lista;
        notifyDataSetChanged();
    }

    public static EditText getPlayerName() {
        return playerName;
    }
}

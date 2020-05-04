package com.example.proximitysocialnetwork.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proximitysocialnetwork.MainActivity;
import com.example.proximitysocialnetwork.Profil;
import com.example.proximitysocialnetwork.R;

import java.util.ArrayList;

public class AdapterNotif extends RecyclerView.Adapter<AdapterNotif.MyViewHolder1>{

    public ArrayList<Profil> profils;
    private OnItemClickListener Listener;

    public AdapterNotif(ArrayList<Profil> profils) {
        this.profils = profils;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setonItemClickListener(AdapterNotif.OnItemClickListener listener)
    {
        Listener = listener;
    }


    @NonNull
    @Override
    public AdapterNotif.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.layout_recycler_view_notif, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotif.MyViewHolder1 holder, int position) {
        Profil profil = profils.get(position);
        holder.display(profil);
    }

    @Override
    public int getItemCount() {
        return profils.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView subtitle;


        public MyViewHolder1(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titre_notif);
            subtitle = itemView.findViewById(R.id.sous_titre_notif);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            Listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void display(Profil profil) {

            title.setText(profil.getName() + " était a proximité");
            subtitle.setText("Cliquer ici pour le découvrir");

        }
    }

}


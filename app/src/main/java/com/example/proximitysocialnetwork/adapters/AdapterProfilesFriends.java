package com.example.proximitysocialnetwork.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.proximitysocialnetwork.Profil;
import com.example.proximitysocialnetwork.R;

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class AdapterProfilesFriends extends RecyclerView.Adapter<AdapterProfilesFriends.MyViewHolder1>{

    public ArrayList<Profil> profils;
    private OnItemClickListener Listener;
    private Context context;

    public AdapterProfilesFriends(ArrayList<Profil> profils, Context context) {
        this.profils = profils;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setonItemClickListener(AdapterProfilesFriends.OnItemClickListener listener)
    {
        Listener = listener;
    }


    @NonNull
    @Override
    public AdapterProfilesFriends.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.layout_rv_friends, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProfilesFriends.MyViewHolder1 holder, int position) {
        Profil profil = profils.get(position);
        holder.display(profil);
    }

    @Override
    public int getItemCount() {
        return profils.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView profilePic;
        private String urlDownload;


        public MyViewHolder1(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.name_profile);
            profilePic = itemView.findViewById(R.id.profilepicnotif);



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

            title.setText(profil.getName());
            urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/" + profil.getProfileImage() +".jpg";
            downloadProfileImage();


        }


        public void downloadProfileImage(){
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    profilePic.setImageBitmap(response);
                    profilePic.setVisibility(View.VISIBLE);
                }
            }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(PersonDiscoveredActivity.this, "Error while downloading image", Toast.LENGTH_SHORT).show();
                }
            }
            );
            requestQueue.add(request);
        }
    }

}


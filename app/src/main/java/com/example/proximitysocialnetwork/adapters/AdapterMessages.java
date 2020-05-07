package com.example.proximitysocialnetwork.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proximitysocialnetwork.Message;
import com.example.proximitysocialnetwork.R;
import java.util.ArrayList;




/**
 * Created by Belal on 5/29/2016.
 */
//Class extending RecyclerviewAdapter
public class AdapterMessages extends RecyclerView.Adapter<AdapterMessages.ViewHolder> {

    private Context context;
    private int SELF = -1;
    private ArrayList<Message> messages;

    //Constructor
    public AdapterMessages(Context context, ArrayList<Message> messages){
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.isSelf()) {
            return SELF;
        }
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_from_self, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_from_friend, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Adding messages to the views
        Message message = messages.get(position);
        holder.textViewMessage.setText(message.getText());
        holder.textViewTime.setText(message.getTime());
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.text_message);
            textViewTime = (TextView) itemView.findViewById(R.id.time_message);
        }
    }
}

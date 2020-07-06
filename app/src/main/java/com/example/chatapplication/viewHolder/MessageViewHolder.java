package com.example.chatapplication.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView single_name;

    public MessageViewHolder(View itemView) {
        super(itemView);

        single_name = (TextView) itemView.findViewById(R.id.single_name);
    }
}
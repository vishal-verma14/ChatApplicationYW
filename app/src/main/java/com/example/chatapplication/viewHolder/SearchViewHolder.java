package com.example.chatapplication.viewHolder;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplication.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewSearch;

    public SearchViewHolder(View itemView) {
        super(itemView);
        textViewSearch = (TextView) itemView.findViewById(R.id.search_name);
    }
}
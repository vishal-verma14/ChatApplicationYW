package com.example.chatapplication.viewHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplication.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView lvEmail, lvMessage,rvEmail,rvMessage;
    public LinearLayout rightLayout,leftLayout;

    public ChatViewHolder(View itemView) {
        super(itemView);
        leftLayout = (LinearLayout) itemView.findViewById(R.id.leftLayout);
        rightLayout = (LinearLayout) itemView.findViewById(R.id.rightLayout);

        lvEmail = (TextView) itemView.findViewById(R.id.lt_sender);
        lvMessage = (TextView) itemView.findViewById(R.id.lt_message);
        rvEmail = (TextView) itemView.findViewById(R.id.rt_sender);
        rvMessage = (TextView) itemView.findViewById(R.id.rt_message);
    }
}
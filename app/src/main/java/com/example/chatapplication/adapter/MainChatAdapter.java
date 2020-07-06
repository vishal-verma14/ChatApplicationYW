package com.example.chatapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;
import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.helper.Constants;
import com.example.chatapplication.model.Message;
import com.example.chatapplication.model.User;
import com.example.chatapplication.screens.MainActivity;
import com.example.chatapplication.screens.SingleChatActivity;
import com.example.chatapplication.viewHolder.SearchViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchChatAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<User> user;
    private AppPreference mAppPreference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mqueryReference;
    public SearchChatAdapter(ArrayList<User> user) {
        this.user = user;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_row_chat, parent, false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        holder.textViewSearch.setText(user.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNewChat(v,user.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public void filterList(ArrayList<User> filterdNames) {
        this.user = filterdNames;
        notifyDataSetChanged();
    }

    private void checkNewChat(final View v, final User uid) {

        mAppPreference = new AppPreference(v.getContext());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mqueryReference = mFirebaseDatabase.getReference();

        final int[] count = new int[1];
        mqueryReference.child(Constants.KEY_GROUP).child(mAppPreference.getKEY_UID()).child(Constants.KEY_SINGLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count[0] = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Message data = dataSnapshot1.getValue(Message.class);
                    if (uid.getUid().equals(data.getUid())){
                        Intent i = new Intent(v.getContext(),MainActivity.class);
                        i.putExtra(Constants.GROUPID,data.getChatId());
                        v.getContext().startActivity(i);
                        break;
                    }
                    //SHOWS ALL ITERATION ARE DONE AND LIST ROW ID DIDN'T MATCHED WITH USER ID
                    count[0]--;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (count[0] == 0){
            Intent i = new Intent(v.getContext(),MainActivity.class);
            i.putExtra(Constants.GROUPID,Constants.NEWUSER);
            i.putExtra(Constants.NEWUSERNAME,uid.getName());
            i.putExtra(Constants.NEWUSERUID,uid.getUid());
            v.getContext().startActivity(i);
        }
    }

}


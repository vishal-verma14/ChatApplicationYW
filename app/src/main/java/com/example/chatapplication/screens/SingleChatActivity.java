package com.example.chatapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;
import com.example.chatapplication.adapter.MainChatAdapter;
import com.example.chatapplication.adapter.SearchChatAdapter;
import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.helper.Constants;
import com.example.chatapplication.model.Chat;
import com.example.chatapplication.model.Message;
import com.example.chatapplication.model.User;
import com.example.chatapplication.viewHolder.ChatViewHolder;
import com.example.chatapplication.viewHolder.MessageViewHolder;
import com.example.chatapplication.viewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SingleChatActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FloatingActionButton a_c_createGroup;
    private DatabaseReference mUserReference;
    private TextView editTextSearch;
    private RecyclerView rvUser;
    private AppPreference mAppPreference;
    private ArrayList<Message> userArrayList;
    private MainChatAdapter ChatAdapter;
    private ProgressDialog mProgressDialog;


    void definition(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(Constants.CHATSCREENLOADING);
        mProgressDialog.show();

        rvUser = (RecyclerView) findViewById(R.id.rv_user);
        rvUser.setHasFixedSize(true);
        rvUser.setLayoutManager(new LinearLayoutManager(this));
        editTextSearch = (TextView) findViewById(R.id.editTextSearch);
        mAppPreference = new AppPreference(this);
        a_c_createGroup = (FloatingActionButton) findViewById(R.id.a_c_createGroup);
        userArrayList = new ArrayList<Message>();
        ChatAdapter = new MainChatAdapter(userArrayList);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserReference = mFirebaseDatabase.getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_single_chat);
            //Load All Static Definition
            definition();
        //Handle Floating Action Bar
            handleGroupActivityPress();
            //Handle Search Activity
            handleSearchActivityPress();
             //Load Group Data
            handleGroupData();
            //Load One to One Data
            handleSingleData();


    }

    private void  handleGroupData(){
        mDatabaseReference = mFirebaseDatabase.getReference().child(Constants.KEY_GROUP).child(mAppPreference.getKEY_UID()).child(Constants.KEY_SINGLE);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    Message data = Snapshot.getValue(Message.class);
                    userArrayList.add(data);
                }
                rvUser.setAdapter(ChatAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            mProgressDialog.dismiss();
            }

        });

    }
    private void  handleSingleData(){
        mDatabaseReference = mFirebaseDatabase.getReference().child(Constants.KEY_GROUP).child(mAppPreference.getKEY_UID()).child(Constants.GROUPID);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    Message data = Snapshot.getValue(Message.class);
                    userArrayList.add(data);
                }
                rvUser.setAdapter(ChatAdapter);
                if(ChatAdapter.getItemCount() == 0){
                    Toast.makeText(getApplicationContext(), "Please Search User Or Create New Group", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.dismiss();

            }

        });

        mProgressDialog.dismiss();

    }

    private void handleSearchActivityPress() {
        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleChatActivity.this,SearchActivity.class));
            }
        });
    }

    private void handleGroupActivityPress() {
        a_c_createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleChatActivity.this,GroupChatActivity.class));
            }
        });
    }


}


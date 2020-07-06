package com.example.chatapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.example.chatapplication.R;
import com.example.chatapplication.adapter.SearchChatAdapter;
import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.helper.Constants;
import com.example.chatapplication.model.Message;
import com.example.chatapplication.model.User;
import com.example.chatapplication.viewHolder.MessageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserReference;
    private TextView editTextSearch;
    private RecyclerView searchUser;
    private ArrayList<User> userArrayList;
    private SearchChatAdapter searchChatAdapter;
    private AppPreference mAppPreference;


    void definition(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserReference = mFirebaseDatabase.getReference().child(Constants.KEY_USER);
        editTextSearch = (TextView) findViewById(R.id.editSearchBOx);
        userArrayList = new ArrayList<User>();
        searchChatAdapter = new SearchChatAdapter(userArrayList);
        mAppPreference = new AppPreference(this);

        searchUser = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        searchUser.setHasFixedSize(true);
        searchUser.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        definition();

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    User data = Snapshot.getValue(User.class);
                    if(!data.getUid().equals(mAppPreference.getKEY_UID())){
                        userArrayList.add(data);
                    }
                }
                searchUser.setAdapter(searchChatAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User s : userArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(s);
            }
        }
        searchChatAdapter.filterList(filteredList);
    }
}
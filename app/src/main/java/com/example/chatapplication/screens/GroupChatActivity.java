package com.example.chatapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;
import com.example.chatapplication.adapter.GroupChatAdapter;
import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.helper.Constants;
import com.example.chatapplication.model.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Group> groupArrayList = new ArrayList<>();
    private GroupChatAdapter adapter;
    private Button btnGetSelected;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserReference;
    private ArrayList<Group>  groupSelectedArrayList  = new ArrayList<>();
    private final Context context = this;
    private AppPreference mAppPreference;

    void definition(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserReference = mFirebaseDatabase.getReference();
        btnGetSelected = (Button) findViewById(R.id.btnGetSelected);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        groupArrayList = new ArrayList<>();
        groupSelectedArrayList = new ArrayList<>();
        mAppPreference = new AppPreference(this);

        adapter = new GroupChatAdapter(this, groupArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        //Defination
        definition();
        //Create Group List
        createGroupList();
        //handle click
        handleClick();

    }

    void handleClick(){
        btnGetSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getSelected().size() > 0) {
                    Group group = new Group();
                    for (int i = 0; i < adapter.getSelected().size(); i++) {
                        group = adapter.getSelected().get(i);
                        groupSelectedArrayList.add(group);
                    }
                    showAleartBox();
                } else {
                    showToast(Constants.NOGROUP);
                }
            }
        });
    }

    private void showAleartBox() {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.group_aleart_box, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(Constants.ALEART_CREATEGROUPBUTTON,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (!String.valueOf(userInput.getText()).equals("")){
                                    uploadData(String.valueOf(userInput.getText()));
                                }else{
                                    Toast.makeText(context, Constants.GROUP_EMPTYSTRING, Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                .setNegativeButton(Constants.ALEART_CANCELBUTTON,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void uploadData(String s) {
        String pushID = mUserReference.push().getKey();
        creatingThreads(pushID, s, mAppPreference.getKEY_UID());
        for (int i = 0; i< this.groupSelectedArrayList.size();i++) {
            creatingThreads(pushID, s, this.groupSelectedArrayList.get(i).getUid());

        }
        startMainActivitytransition(pushID);


    }
    private void creatingThreads(String pushID, String s,String uid){
        DatabaseReference ref = mUserReference.child(Constants.KEY_GROUP).child(uid).child(Constants.KEY_GROUP_ID).child(pushID);
        ref.child(Constants.CHATID).setValue(pushID);
        ref.child(Constants.NOM).setValue(this.groupSelectedArrayList.size());
        ref.child(Constants.CHATNAME).setValue(s);
    }

    private void startMainActivitytransition(String pushID) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(Constants.GROUPID, pushID);
        showToast(Constants.GROUPCREATEDMESSAGE);
        this.groupSelectedArrayList.clear();
        startActivity(i);
        finish();
    }

    void createGroupList(){
        mUserReference.child(Constants.KEY_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    Group data = Snapshot.getValue(Group.class);
                    if(!data.getUid().equals(mAppPreference.getKEY_UID())){
                        groupArrayList.add(data);
                    }
                }
                adapter.setEmployees(groupArrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
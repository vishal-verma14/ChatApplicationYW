package com.example.chatapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.R;
import com.example.chatapplication.helper.Constants;
import com.example.chatapplication.model.Chat;
import com.example.chatapplication.model.Message;
import com.example.chatapplication.model.User;
import com.example.chatapplication.viewHolder.ChatViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnSend;
    private EditText edtMessage;
    private RecyclerView rvMessage;
    private AppPreference mAppPreference;
    private FirebaseDatabase mFirebaseDatabase;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mDatabaseReference, mqueryReference;
    private FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter;
   //newUser
    private boolean checkFlag = false;
    private String groupId ,newUserName,newUserUID;

    void definition(){
        btnSend = (Button) findViewById(R.id.btn_send);
        edtMessage = (EditText) findViewById(R.id.edt_message);
        rvMessage = (RecyclerView) findViewById(R.id.rv_chat);
        rvMessage.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvMessage.setLayoutManager(linearLayoutManager);
        mAppPreference = new AppPreference(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mqueryReference = mFirebaseDatabase.getReference();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        definition();
        btnSend.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
             groupId = null;
            } else {
                groupId = extras.getString(Constants.GROUPID);
                    if(groupId.equals(Constants.NEWUSER)){
                        checkFlag = true;
                        newUserName = extras.getString(Constants.NEWUSERNAME);
                        newUserUID = extras.getString(Constants.NEWUSERUID);
                    }else{
                        checkFlag = false;
                        renderData(groupId);
                      }
            }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send){
            String message = edtMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message)){
                Map<String, Object> param = new HashMap<>();
                param.put(Constants.KEY_SENDER, mAppPreference.getEmail());
                param.put(Constants.KEY_MESSAGE, message);
                param.put(Constants.F_KEY_UID, mAppPreference.getKEY_UID());

                if (!checkFlag){
                    mDatabaseReference.child(Constants.KEY_CHAT).child(groupId)
                            .push()
                            .setValue(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            edtMessage.setText("");
                            rvMessage.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    });

                }else {
                    Toast.makeText(this, "new User initiated", Toast.LENGTH_SHORT).show();

                    //after 18 hours f debugging i found new local database instance need to be created otherwise it it crashes and try to get previous screen database may be due to same name
                        mqueryReference = mFirebaseDatabase.getReference();
                        final String pushID = mqueryReference.push().getKey();

                        createChatThread(pushID,mAppPreference.getKEY_UID());
                        createChatThread(pushID,newUserUID);
                        mDatabaseReference.child(Constants.KEY_CHAT).child(pushID)
                                .push()
                                .setValue(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                edtMessage.setText("");
                                groupId = pushID;
                                checkFlag = false;

                            }
                        });
                }
            }
        }
    }

    private void createChatThread(final String pushID, String key_uid) {
        DatabaseReference ref = mqueryReference.child(Constants.KEY_GROUP).child(key_uid).child(Constants.KEY_SINGLE).child(pushID);
        ref.child(Constants.CHATID).setValue(pushID);
        ref.child(Constants.CHATUID).setValue(newUserUID);
        ref.child(Constants.CHATNAME).setValue(newUserName);
    }


    private void renderData(String groupId ){
        adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.item_row_chat,
                ChatViewHolder.class,
                mDatabaseReference.child(Constants.KEY_CHAT).child(groupId)
        ) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, int position) {
                if(model.uid.equals(mAppPreference.getKEY_UID())){
                    viewHolder.rightLayout.setVisibility(View.VISIBLE);
                    viewHolder.leftLayout.setVisibility(View.INVISIBLE);
                    viewHolder.rvMessage.setText(model.message);
                    viewHolder.rvEmail.setText(model.sender);
                }else if(!model.sender.equals(mAppPreference.getEmail())){
                    viewHolder.rightLayout.setVisibility(View.INVISIBLE);
                    viewHolder.leftLayout.setVisibility(View.VISIBLE);
                    viewHolder.lvMessage.setText(model.message);
                    viewHolder.lvEmail.setText(model.sender);
                }
            }
        };
        rvMessage.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //    private void fetchName(final String key){
//         mDatabaseReference.child(Constants.KEY_USER).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
//                     User data = Snapshot.getValue(User.class);
//                    if (key.equals(mAppPreference.getKEY_UID()))
//                         data.getName();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }


    @Override
    public boolean onSupportNavigateUp() {
       startActivity(new Intent(this,SingleChatActivity.class));
       return true;
    }
}

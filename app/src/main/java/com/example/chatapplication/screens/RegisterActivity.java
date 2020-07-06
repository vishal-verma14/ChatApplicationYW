package com.example.chatapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.R;
import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener  {

    private EditText edtName, edtPassword, edtEmail;
    private Button btnRegister;
    private TextView tvLogin;
    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ProgressDialog mProgressDialog;
    boolean isEmptyField = false;
    //String
    private String name,password,email;
    //Shared Pref
    AppPreference mAppPreference;

    void definition(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Register");
        mProgressDialog.setMessage("Please wait....");
        edtName = (EditText) findViewById(R.id.edt_name);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mAppPreference = new AppPreference(this);
    }

//    void redirect(){
//        AppPreference mAppPreference = new AppPreference(this);
//        if (!TextUtils.isEmpty(mAppPreference.getEmail())){
//            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        definition();
//        redirect();
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register){
            name  = edtName.getText().toString().trim();
            email  = edtEmail.getText().toString().trim();
            password  = edtPassword.getText().toString().trim();
            validation(name,email,password);
            if (!isEmptyField){
                mProgressDialog.show();
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mProgressDialog.dismiss();

                                    Map<String, Object> param = new HashMap<>();
                                    param.put(Constants.KEY_NAME, name);
                                    param.put(Constants.KEY_EMAIL, email);
                                    param.put(Constants.F_KEY_UID, mFirebaseAuth.getCurrentUser().getUid());

                                    mAppPreference.setEmail(email);
                                    mAppPreference.setKEY_UID(mFirebaseAuth.getCurrentUser().getUid());

                                    mDatabaseReference.child(Constants.KEY_USER)
                                            .push()
                                            .setValue(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RegisterActivity.this, "Registration Successful",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, SingleChatActivity.class));
                                            finish();
                                        }
                                    });


                                }else {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Registration Failed! please provide all the details or email already present",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
    void validation(String name,String email, String password){
        if (TextUtils.isEmpty(name)) {
            isEmptyField = true;
            edtName.setError("required");
        }
        if (TextUtils.isEmpty(email)){
            isEmptyField = true;
            edtEmail.setError("required");
        }
        if (TextUtils.isEmpty(password)){
            isEmptyField = true;
            edtPassword.setError("required");
        }
    }
}

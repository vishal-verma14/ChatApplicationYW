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

import com.example.chatapplication.helper.AppPreference;
import com.example.chatapplication.R;
import com.example.chatapplication.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog mProgressDialog;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private FirebaseAuth mFirebaseAuth;
    private String email, password;
    private boolean isEmptyField = false;
    private AppPreference mAppPreference;

    void definition(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(Constants.LOGIN);
        mProgressDialog.setMessage(Constants.PLEASEWAIT);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtEmail.setText("vikram@gmail.con");
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtPassword.setText("123456");
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (TextView) findViewById(R.id.btn_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAppPreference = new AppPreference(LoginActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_login);
        definition();
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login){
            email  = edtEmail.getText().toString().trim();
            password  = edtPassword.getText().toString().trim();
            //Check and set error
            checkValidation(email,password);
            //
            if (!isEmptyField){
                mProgressDialog.show();
                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mProgressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Success",Toast.LENGTH_SHORT).show();
                                    mAppPreference.setEmail(email);
                                    mAppPreference.setKEY_UID(mFirebaseAuth.getCurrentUser().getUid());

                                    startActivity(new Intent(LoginActivity.this, SingleChatActivity.class));
                                    finish();
                                }else {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }else
            if (v.getId() == R.id.btn_register){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        }
    }

    void checkValidation(String email, String password){
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

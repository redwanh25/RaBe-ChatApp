package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button fpass;
    private TextView email;
    private FirebaseAuth mFirebaseAuth;
    private Toolbar toolbar;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        fpass = findViewById(R.id.fPass_Button);
        email = findViewById(R.id.pass_editText);
        mFirebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar_changePass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forget Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = new ProgressDialog(this);

        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();

                if(!userEmail.equals("")) {

                    mProgressBar.setMessage("Please wait while we sending password reset email...");
                    mProgressBar.setCanceledOnTouchOutside(false);
                    mProgressBar.show();

                    mFirebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                mProgressBar.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this, "Email has been send", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                Toast.makeText(ForgetPasswordActivity.this, "Error", Toast.LENGTH_LONG).show();
                                mProgressBar.dismiss();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(ForgetPasswordActivity.this, "Email is missing", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

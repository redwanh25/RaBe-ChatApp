package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeStatus extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout status;
    private Button change;
    private ProgressDialog progress;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference onlineDatabase;
    private FirebaseUser current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        toolbar = findViewById(R.id.toolbarId_status);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mFirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uId);

        status = findViewById(R.id.ch_status);
        change = findViewById(R.id.ch_statusButton);

        current = FirebaseAuth.getInstance().getCurrentUser();
        onlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("Online");

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(ChangeStatus.this);
                progress.setTitle("Saving change");
                progress.setMessage("Please wait...");
                progress.show();
                String s_tatus = status.getEditText().getText().toString();
                mDatabaseReference.child("Status").setValue(s_tatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progress.dismiss();
                            Toast.makeText(ChangeStatus.this, "Status updated", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(ChangeStatus.this, "something is wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        onlineDatabase.setValue(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        onlineDatabase.setValue(false);
    }
}

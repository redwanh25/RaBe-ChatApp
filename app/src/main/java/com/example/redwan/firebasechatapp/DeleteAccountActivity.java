package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button deleteButton;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private RadioButton radio;
    private RadioGroup radioGroup;
    private DatabaseReference databaseReference;

    private DatabaseReference onlineDatabase;
    private FirebaseUser current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        toolbar = findViewById(R.id.toolbarId_delete);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delete Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deleteButton = findViewById(R.id.delete_Button);
        radioGroup = findViewById(R.id.radio_group);
        progressDialog = new ProgressDialog(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        current = FirebaseAuth.getInstance().getCurrentUser();
        onlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("Online");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = "";
                try {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    radio = findViewById(radioId);
                    selectedItem = radio.getText().toString();
                }
                catch (Exception e) {
                    Toast.makeText(DeleteAccountActivity.this, "you don't have selected any item", Toast.LENGTH_LONG).show();
                }
                if(selectedItem.equals("Yes")) {
                    progressDialog.setMessage("Deleting your account...");
                    progressDialog.show();
                    databaseReference.child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(DeleteAccountActivity.this, "Account has been deleted", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(DeleteAccountActivity.this, StartActivity.class));
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(DeleteAccountActivity.this, "something is wrong", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(DeleteAccountActivity.this, "something is wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(selectedItem.equals("No")){
                    finish();
                }
//                else {
//                    Toast.makeText(DeleteAccountActivity.this, "you don't have selected any item", Toast.LENGTH_LONG).show();
//                }
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

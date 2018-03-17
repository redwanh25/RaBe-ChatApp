package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private TextView display_name, display_status, total_frined;
    private ImageView profileImage_view;
    private Button sent_cancel_button;

    private DatabaseReference user_database, friendList_database, friendRequest_database;
    private FirebaseUser currentUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userKey = getIntent().getStringExtra("key");

        profileImage_view = findViewById(R.id.profilePic_view);
        display_name = findViewById(R.id.profileName_view);
        display_status = findViewById(R.id.profileStatus_view);
        sent_cancel_button = findViewById(R.id.sent_cancel);
        total_frined = findViewById(R.id.totalFriend);

        user_database = FirebaseDatabase.getInstance().getReference().child("users").child(userKey);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        user_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String proPic = dataSnapshot.child("Thumb_image").getValue().toString();

                display_name.setText(name);
                display_status.setText(status);

                Picasso.with(Profile.this).load(proPic).placeholder(R.drawable.avatar_default).into(profileImage_view);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

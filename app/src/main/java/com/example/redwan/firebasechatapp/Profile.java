package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    private String currentStatus;

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
        friendRequest_database = FirebaseDatabase.getInstance().getReference().child("friend_request");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uId = currentUser.getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        currentStatus = "notFriend";

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

        sent_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sent_cancel_button.setEnabled(false);

                //---------------------- notFriend -------------------//

                if(currentStatus.equals("notFriend")){
                    friendRequest_database.child(uId).child(userKey).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                friendRequest_database.child(userKey).child(uId).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        currentStatus = "requestSent";
                                        sent_cancel_button.setText("Cancel Friend Request");
                                        Toast.makeText(Profile.this, "Request Sent Successfully", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(Profile.this, "Request Sent Failed", Toast.LENGTH_LONG).show();
                            }
                            sent_cancel_button.setEnabled(true);
                        }
                    });
                }

                //---------------------- Cancel Friend Request -------------------//

                if(currentStatus.equals("requestSent")) {

                    friendRequest_database.child(uId).child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                friendRequest_database.child(userKey).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        sent_cancel_button.setEnabled(true);
                                        currentStatus = "notFriend";
                                        sent_cancel_button.setText("Send Friend Request");
                                        Toast.makeText(Profile.this, "Request Cancel", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(Profile.this, "Request Cancel Failed", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }

            }
        });
    }
}

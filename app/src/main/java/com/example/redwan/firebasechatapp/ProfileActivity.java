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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private TextView display_name, display_status, total_frined;
    private ImageView profileImage_view;
    private Button sent_cancel_button, cancel_request_button;

    private DatabaseReference user_database, friendList_database, friendRequest_database;
    private FirebaseUser currentUser;

    private String currentStatus;

    private ProgressDialog progressDialog;

    private DatabaseReference onlineDatabase;
    private FirebaseUser current;

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
        cancel_request_button = findViewById(R.id.cancel_frnd);

        user_database = FirebaseDatabase.getInstance().getReference().child("users").child(userKey);
//        user_database.keepSynced(true);
        friendRequest_database = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendList_database = FirebaseDatabase.getInstance().getReference().child("friends");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uId = currentUser.getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        currentStatus = "notFriend";

        cancel_request_button.setVisibility(View.INVISIBLE);
        cancel_request_button.setEnabled(false);

        current = FirebaseAuth.getInstance().getCurrentUser();
        onlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("Online");

        user_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                final String proPic = dataSnapshot.child("Thumb_image").getValue().toString();

                display_name.setText(name);
                display_status.setText(status);

                if(!proPic.equals("default")) {
//                    Picasso.with(ProfileActivity.this).load(proPic).placeholder(R.drawable.avatar_default).into(profileImage_view);
                    Picasso.with(ProfileActivity.this).load(proPic).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(profileImage_view, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(proPic).placeholder(R.drawable.avatar_default).into(profileImage_view);
                        }
                    });

                }
                if(uId.equals(userKey)) {
                    sent_cancel_button.setVisibility(View.INVISIBLE);
                    sent_cancel_button.setEnabled(false);

                    cancel_request_button.setVisibility(View.INVISIBLE);
                    cancel_request_button.setEnabled(false);
                }

                //--------------- Friend List and request feature -----------------//

                friendRequest_database.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userKey)){

                            String requestType = dataSnapshot.child(userKey).child("request_type").getValue().toString();
                            if(requestType.equals("received")) {
                                currentStatus = "requestReceived";
                                sent_cancel_button.setText("Accept Friend Request");

                                cancel_request_button.setVisibility(View.VISIBLE);
                                cancel_request_button.setEnabled(true);

                            }
                            else if(requestType.equals("sent")) {
                                currentStatus = "requestSent";
                                sent_cancel_button.setText("Cancel Friend Request");

                                cancel_request_button.setVisibility(View.INVISIBLE);
                                cancel_request_button.setEnabled(false);

                            }
                        }
                        else {
                            friendList_database.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userKey)){

                                        currentStatus = "Friend";
                                        sent_cancel_button.setText("unfriend this Person");

                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                        cancel_request_button.setEnabled(false);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cancel_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequest_database.child(uId).child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            friendRequest_database.child(userKey).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    currentStatus = "notFriend";
                                    sent_cancel_button.setText("Send Friend Request");
                                    Toast.makeText(ProfileActivity.this, "Request Cancel", Toast.LENGTH_LONG).show();

                                    cancel_request_button.setVisibility(View.INVISIBLE);
                                    cancel_request_button.setEnabled(false);
                                }
                            });
                        }
                        else {
                            Toast.makeText(ProfileActivity.this, "Something is wrong", Toast.LENGTH_LONG).show();
                        }
                        sent_cancel_button.setEnabled(true);
                    }
                });
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
                                        Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_LONG).show();

                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                        cancel_request_button.setEnabled(false);
                                    }
                                });

                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Request Sent Failed", Toast.LENGTH_LONG).show();
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

                                        currentStatus = "notFriend";
                                        sent_cancel_button.setText("Send Friend Request");
                                        Toast.makeText(ProfileActivity.this, "Request Cancel", Toast.LENGTH_LONG).show();

                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                        cancel_request_button.setEnabled(false);
                                    }
                                });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Request Cancel Failed", Toast.LENGTH_LONG).show();
                            }

                            sent_cancel_button.setEnabled(true);

                        }
                    });


                }

                //---------------------- Friend Request Receive -------------------//

                if(currentStatus.equals("requestReceived")) {

                    final String date = DateFormat.getDateTimeInstance().format(new Date());
                    friendList_database.child(uId).child(userKey).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendList_database.child(userKey).child(uId).child("date").setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        friendRequest_database.child(userKey).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                friendRequest_database.child(uId).child(userKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        currentStatus = "Friend";
                                                        sent_cancel_button.setText("unfriend this Person");
                                                        Toast.makeText(ProfileActivity.this, "Request has been Received", Toast.LENGTH_LONG).show();

                                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                                        cancel_request_button.setEnabled(false);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Something is wrong", Toast.LENGTH_LONG).show();
                            }
                            sent_cancel_button.setEnabled(true);
                        }
                    });

                }

                // ------------------- Unfriend --------------- //

                if(currentStatus.equals("Friend")) {

                    friendList_database.child(uId).child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                friendList_database.child(userKey).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        currentStatus = "notFriend";
                                        sent_cancel_button.setText("Send Friend Request");
                                        Toast.makeText(ProfileActivity.this, "Unfriend successfully", Toast.LENGTH_LONG).show();

                                        cancel_request_button.setVisibility(View.INVISIBLE);
                                        cancel_request_button.setEnabled(false);

                                    }
                                });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Something is wrong", Toast.LENGTH_LONG).show();
                            }
                            sent_cancel_button.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        onlineDatabase.setValue("true");
    }

    @Override
    public void onPause() {
        super.onPause();
        onlineDatabase.setValue(ServerValue.TIMESTAMP);
    }
}

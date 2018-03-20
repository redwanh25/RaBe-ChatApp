package com.example.redwan.firebasechatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String user_key, chatWithUserName;
    private DatabaseReference databaseReference;
    private TextView chatUserName, chatOnlineStatus;
    private CircleImageView chatProfilePic;

    private DatabaseReference onlineDatabase;
    private FirebaseUser current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user_key = getIntent().getStringExtra("key");
        chatWithUserName = getIntent().getStringExtra("user_name");

        toolbar = findViewById(R.id.toolbarId_chat);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(null);
 //       actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        chatUserName = findViewById(R.id.chat_user_name);
        chatProfilePic = findViewById(R.id.chat_profile_pic);
        chatOnlineStatus = findViewById(R.id.chat_online_status);

        chatUserName.setText(chatWithUserName);

        current = FirebaseAuth.getInstance().getCurrentUser();
        onlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("Online");

        databaseReference.child("users").child(user_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean online = (Boolean) dataSnapshot.child("Online").getValue();
                String image = dataSnapshot.child("Thumb_image").getValue().toString();

                if(online.equals(true)) {
                    chatOnlineStatus.setText("Online");
                }
                else {
                    chatOnlineStatus.setText("Offline");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        return true;
    }

    // online -------------------------

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

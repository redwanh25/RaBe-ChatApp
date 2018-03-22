package com.example.redwan.firebasechatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference onlineDatabase;
    private FirebaseUser current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = findViewById(R.id.toolbarId_allUsers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
//        mDatabaseReference.keepSynced(true);

        recyclerView = findViewById(R.id.all_Users_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        current = FirebaseAuth.getInstance().getCurrentUser();
        onlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("Online");


    }

    // realtime  niye kdatabaseaj korle "onStart()" method likhte hobe.
    // Firebase UI
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter <Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class, R.layout.user_single_layout_alluser, UsersViewHolder.class, mDatabaseReference) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.set_thumbImage(model.getThumb_image(), getApplicationContext());

                final String userKey = getRef(position).getKey();

                mDatabaseReference.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Online")) {
                            String pic = dataSnapshot.child("Online").getValue().toString();
                            viewHolder.setOnlineStatus(pic);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("key", userKey);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView mTextView = mView.findViewById(R.id.display_name_allUsers);
            mTextView.setText(name);
        }
        public void setUserStatus(String status){
            TextView mTextView = mView.findViewById(R.id.status_allUsers);
            mTextView.setText(status);
        }
        public void set_thumbImage(final String thumb, final Context cntx) {
            final CircleImageView img = mView.findViewById(R.id.proPic_allUsers);
//          Picasso.with(cntx).load(thumb).placeholder(R.drawable.avatar_default).into(img);

            Picasso.with(cntx).load(thumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(img, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(cntx).load(thumb).placeholder(R.drawable.avatar_default).into(img);
                }
            });
        }

        public void setOnlineStatus(String pic) {
            ImageView image = mView.findViewById(R.id.onLine);

            if(pic.equals("true")) {
                image.setVisibility(View.VISIBLE);
            }
            else {
                image.setVisibility(View.INVISIBLE);
            }

        }
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

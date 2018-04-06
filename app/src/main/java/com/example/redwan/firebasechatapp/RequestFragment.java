package com.example.redwan.firebasechatapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private View mainView;
    private DatabaseReference requestDatabase, friendList_database, friendRequest_database;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;
    private String uId;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainView = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = mainView.findViewById(R.id.friendsRequest_section);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = currentUser.getUid();

        requestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_request").child(uId);
        requestDatabase.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.keepSynced(true);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        friendRequest_database = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendList_database = FirebaseDatabase.getInstance().getReference().child("friends");

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Request, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                Request.class, R.layout.user_single_layout_request, RequestFragment.RequestViewHolder.class, requestDatabase) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, final Request model, int position) {

                final String userKey = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                            String requestType = dataSnapshot.getValue().toString();

                            if(requestType.equals("received")) {

                                Button acceptRequestButton = viewHolder.mView.findViewById(R.id.accept);
                                acceptRequestButton.setText("accept");
                                acceptRequestButton.setTextColor(Color.parseColor("#ff669900"));

                                Button cancelButton = viewHolder.mView.findViewById(R.id.cancel);
                                cancelButton.setVisibility(View.VISIBLE);
                                cancelButton.setEnabled(true);

                                acceptRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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
                                                                            Toast.makeText(getContext(), "Request has been Received", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        friendRequest_database.child(uId).child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {
                                                    friendRequest_database.child(userKey).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Request Cancel", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "Request Cancel Failed", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                });

                            }
                            else if(requestType.equals("sent")) {

                                Button cancelFriendRequestButton = viewHolder.mView.findViewById(R.id.accept);
                                cancelFriendRequestButton.setText("cancel  friend  request");
                                cancelFriendRequestButton.setTextColor(Color.parseColor("#ffcc0000"));

                                Button cancelButton = viewHolder.mView.findViewById(R.id.cancel);
                                cancelButton.setVisibility(View.INVISIBLE);
                                cancelButton.setEnabled(false);

                                cancelFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        friendRequest_database.child(uId).child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {
                                                    friendRequest_database.child(userKey).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Request Cancel", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "Request Cancel Failed", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                userDatabase.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("Name").getValue().toString();
                        String thumbPic = dataSnapshot.child("Thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("Online")) {
                            String pic = dataSnapshot.child("Online").getValue().toString();
                            viewHolder.setOnlineStatus(pic);
                        }
                        viewHolder.setImage(thumbPic, getContext());
                        viewHolder.setDisplayName(name);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("key", userKey);
                                startActivity(profileIntent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView mTextView = mView.findViewById(R.id.request_displayName);
            mTextView.setText(name);
        }

        public void setImage(final String thumb, final Context cntx) {
            final CircleImageView img = mView.findViewById(R.id.request_profilePic);
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
            ImageView image = mView.findViewById(R.id.onLineChat);

            if(pic.equals("true")) {
                image.setVisibility(View.VISIBLE);
            }
            else {
                image.setVisibility(View.INVISIBLE);
            }

        }
    }

}

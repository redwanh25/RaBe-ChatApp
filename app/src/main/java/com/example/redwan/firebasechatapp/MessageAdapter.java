package com.example.redwan.firebasechatapp;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redwan on 22-Mar-18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private String currentUser;
    private DatabaseReference mUserDatabase;
    private String sms_id, sms, fromUser, smsType;
    private int position;

    public void setSms_id(String sms_id) {
        this.sms_id = sms_id;
    }
    public String getSms_id() {
        return sms_id;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }
    public String getSms() {
        return sms;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
    public String getFromUser() {
        return fromUser;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }
    public String getSmsType() {
        return smsType;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }


    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }
    // 2nd

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout_chat_me, parent, false);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout_chat, parent, false);
                break;
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout_chat_image_me, parent, false);
                break;
            case 4:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout_chat_image, parent, false);
                break;
        }
        return new MessageViewHolder(v);

    }

    // 1st

    @Override
    public int getItemViewType(int position) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Messages c = mMessageList.get(position);
        String fromUser = c.getFrom();
        String message_type = c.getType();

        if (fromUser.equals(currentUser)) {
            if (message_type.equals("text")) {
                return 1;
            } else if (message_type.equals("image")) {
                return 3;
            }

        } else {
            if (message_type.equals("text")) {
                return 2;
            } else if (message_type.equals("image")) {
                return 4;
            }
        }
        return position;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, final int position) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Messages c = mMessageList.get(position);
        final String fromUser = c.getFrom();
        final String sms = c.getMessage();
        String message_type = c.getType();
        final String sms_id = c.getSms_id();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(fromUser);

        if (fromUser.equals(currentUser)) {

            if (message_type.equals("text")) {

                viewHolder.messageTextMe.setBackgroundResource(R.drawable.sms_background_myself);
                viewHolder.messageTextMe.setTextColor(Color.BLACK);
                viewHolder.messageTextMe.setText(c.getMessage());
                viewHolder.timeForMe.setText(c.getTime());

                viewHolder.messageTextMe.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setSms_id(sms_id);
                        setPosition(position);
                        setSms(sms);
                        setFromUser(fromUser);
                        setSmsType("text");
                        return false;
                    }

                });

            } else if (message_type.equals("image")) {

                viewHolder.timeForMeImage.setText(c.getTime());

                Picasso.with(viewHolder.messageImageMe.getContext()).load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar).into(viewHolder.messageImageMe, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(viewHolder.messageImageMe.getContext()).load(c.getMessage()).placeholder(R.drawable.avatar).into(viewHolder.messageImageMe);
                    }
                });

                viewHolder.messageImageMe.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setSms_id(sms_id);
                        setPosition(position);
                        setSmsType("image");
                        return false;
                    }

                });

            }
        } else {

            if (message_type.equals("text")) {

                viewHolder.messageText.setBackgroundResource(R.drawable.sms_background_others);
                viewHolder.messageText.setTextColor(Color.WHITE);
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.timeForOthers.setText(c.getTime());

                mUserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String image = dataSnapshot.child("Thumb_image").getValue().toString();

                        Picasso.with(viewHolder.profileImage.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(viewHolder.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(viewHolder.profileImage.getContext()).load(image).placeholder(R.drawable.avatar_default).into(viewHolder.profileImage);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setSms_id(sms_id);
                        setPosition(position);
                        setSms(sms);
                        setFromUser(fromUser);
                        setSmsType("text");
                        return false;
                    }

                });

            } else if (message_type.equals("image")) {

                viewHolder.timeForOthersImage.setText(c.getTime());

                mUserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String image = dataSnapshot.child("Thumb_image").getValue().toString();

                        Picasso.with(viewHolder.profileImage_pic.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(viewHolder.profileImage_pic, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(viewHolder.profileImage_pic.getContext()).load(image).placeholder(R.drawable.avatar_default).into(viewHolder.profileImage_pic);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar).into(viewHolder.messageImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).placeholder(R.drawable.avatar).into(viewHolder.messageImage);
                    }
                });

                viewHolder.messageImage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setSms_id(sms_id);
                        setPosition(position);
                        setSmsType("image");
                        return false;
                    }

                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        public TextView messageText, messageTextMe, timeForOthers, timeForMe, timeForOthersImage, timeForMeImage;
        public CircleImageView profileImage, profileImage_pic;
        public ImageView messageImage, messageImageMe;
        public int COPY = 0;
        public int DELETE= 1;


        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);
            messageTextMe = view.findViewById(R.id.message_text_layout_me);
            timeForMe = view.findViewById(R.id.time_For_Me);
            timeForOthers = view.findViewById(R.id.time_For_Others);
            timeForMeImage = view.findViewById(R.id.time_For_Me_image);
            timeForOthersImage = view.findViewById(R.id.time_For_Others_image);
            messageImage = view.findViewById(R.id.message_Image_layout);
            messageImageMe = view.findViewById(R.id.message_Image_layout_Me);
            profileImage_pic = view.findViewById(R.id.message_profile_layout_pic);
            view.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, COPY, 0, "Copy sms");
            menu.add(0, DELETE, 0, "Delete sms from both side");
        }
    }
}
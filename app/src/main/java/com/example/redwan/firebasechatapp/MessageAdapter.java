package com.example.redwan.firebasechatapp;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redwan on 22-Mar-18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private String currentUser;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout_chat ,parent, false);

        return new MessageViewHolder(v);

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Messages c = mMessageList.get(i);
        String fromUser = c.getFrom();

        if(fromUser.equals(currentUser)) {
            viewHolder.messageText.setBackgroundResource(R.drawable.sms_background_myself);
            viewHolder.messageText.setTextColor(Color.BLACK);

        }
        else {
            viewHolder.messageText.setBackgroundResource(R.drawable.sms_background_others);
            viewHolder.messageText.setTextColor(Color.WHITE);
        }

        viewHolder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}

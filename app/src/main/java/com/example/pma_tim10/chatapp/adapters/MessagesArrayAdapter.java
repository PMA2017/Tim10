package com.example.pma_tim10.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by Daniel on 6/7/2017.
 */

public class MessagesArrayAdapter extends RecyclerView.Adapter<MessagesArrayAdapter.MessageViewHolder> {

    private List<Message> messages;
    private FirebaseUser currentUser;

    private final int MSG_SENT = 1;
    private final int MSG_RECEIVED = 2;

    private Context context;

    Activity activity;

    public MessagesArrayAdapter(List<Message> msgs, Activity activity){
        this.messages = msgs;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.activity = activity;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivSenderPhoto;
        public TextView txtMessageText;
        public TextView txtMessageDateTime;

        public MessageViewHolder(View view) {
            super(view);
            ivSenderPhoto = (ImageView) view.findViewById(R.id.sender_photo);
            txtMessageText = (TextView) view.findViewById(R.id.message_text);
            txtMessageDateTime = (TextView) view.findViewById(R.id.message_datetime);
            txtMessageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d("","aasd");
                    ((MessagesActivity)activity).showMapDialog(45.267135,19.833550);
                    return false;
                }
            });
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_SENT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);

        return new MessageViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        if(currentUser.getUid().equals(msg.getSender()))
            return MSG_SENT;
        else
            return MSG_RECEIVED;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.txtMessageText.setText(message.getContent());
        holder.txtMessageDateTime.setText(message.getDateTimeFormatted());
        User u = MessagesActivity.usersInChat.get(message.getSender());
        Bitmap bitmap = u != null ? u.getUserProfilePhoto() : null;
        if(u != null && bitmap != null)
            holder.ivSenderPhoto.setImageBitmap(Utility.getCircleBitmap(bitmap));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}

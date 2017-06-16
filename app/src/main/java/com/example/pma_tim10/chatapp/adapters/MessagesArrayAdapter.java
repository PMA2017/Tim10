package com.example.pma_tim10.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.activities.UserDetailsActivity;
import com.example.pma_tim10.chatapp.callback.IFirebaseFileUploadCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.Utility;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 6/7/2017.
 */

public class MessagesArrayAdapter extends RecyclerView.Adapter<MessagesArrayAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Map<String, User> usersInChat;
    private FirebaseUser currentUser;

    private final int MSG_SENT = 1;
    private final int MSG_RECEIVED = 2;

    private Context context;

    Activity activity;
    IFirebaseFileUploadCallback downloadCallback;

    public MessagesArrayAdapter(List<Message> msgs, Map<String,User> usersInChat, Activity activity, IFirebaseFileUploadCallback downloadCallback){
        this.messages = msgs;
        this.usersInChat = usersInChat;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.downloadCallback = downloadCallback;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivSenderPhoto;
        public TextView txtMessageText;
        public TextView txtMessageDateTime;
        public ImageView ivAttachedImage;

        public MessageViewHolder(View view) {
            super(view);
            ivSenderPhoto = (ImageView) view.findViewById(R.id.sender_photo);
            txtMessageText = (TextView) view.findViewById(R.id.message_text);
            txtMessageDateTime = (TextView) view.findViewById(R.id.message_datetime);
            ivAttachedImage = (ImageView) view.findViewById(R.id.attached_image);
        }

        public void addMessageListenerLocation(final double longitude, final double latitude){
            txtMessageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((MessagesActivity)activity).showMapDialog(latitude,longitude);
                    return false;
                }
            });
        }

        public void addDownloadFileListener(final String... args){
            txtMessageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadCallback.notify(args);
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

        // location
        if(message.isLocationSet())
            holder.addMessageListenerLocation(message.getLongitude(),message.getLatitude());

        // picture
        final User u = usersInChat.get(message.getSender());
        Bitmap bitmap = u != null ? u.getUserProfilePhoto() : null;
        if(u != null && bitmap != null)
            holder.ivSenderPhoto.setImageBitmap(Utility.getCircleBitmap(bitmap));

        if(u != null)
            holder.ivSenderPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToUserDetailsActivity(u.getUid());
                }
            });

        // file upload
        if(message.isFileAttached()){
            if(message.isFileImage()) {
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(FirebaseStorage.getInstance().getReference().child(Constants.CHAT_FILES).child(message.getConversationId()).child(message.getFileName()))
                        .into(holder.ivAttachedImage);
                holder.ivAttachedImage.setVisibility(View.VISIBLE);
            }
            holder.addDownloadFileListener(message.getFileName(),message.getContent());
            holder.txtMessageText.setPaintFlags(holder.txtMessageText.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void goToUserDetailsActivity(String uid){
        Intent intent = new Intent(activity,UserDetailsActivity.class);
        intent.putExtra(Constants.IE_USER_ID_KEY, uid);
        activity.startActivity(intent);
        activity.finish();
    }

}

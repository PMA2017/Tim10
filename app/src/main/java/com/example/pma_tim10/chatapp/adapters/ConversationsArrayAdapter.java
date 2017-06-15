package com.example.pma_tim10.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dorian on 5/15/2017.
 */

public class ConversationsArrayAdapter extends ArrayAdapter<Conversation> {

    Context context;
    private FirebaseUser currentUser;

    public ConversationsArrayAdapter(Context context, int textViewResourceId, List<Conversation> objects){
        super(context,textViewResourceId,objects);
        this.context = context;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtConversationName;
        TextView txtLastMessage;
        TextView txtLastChattingDate;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ConversationsArrayAdapter.ViewHolder holder = null;
        Conversation rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.messages_list_item, null);
            holder = new ConversationsArrayAdapter.ViewHolder();
            holder.txtConversationName = (TextView) convertView.findViewById(R.id.conversation_name);
            holder.txtLastMessage = (TextView) convertView.findViewById(R.id.last_message);
            holder.txtLastChattingDate = (TextView) convertView.findViewById(R.id.last_chatting_date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);

        } else
            holder = (ConversationsArrayAdapter.ViewHolder) convertView.getTag();

        //
        if(rowItem.getMembers().size() == 1){
            // alone in chat
            holder.txtConversationName.setText("___" + rowItem.getName().replace(currentUser.getDisplayName(),"") + "___");
        }else{
            holder.txtConversationName.setText(rowItem.getName().replace(currentUser.getDisplayName(),""));
        }

        holder.txtLastMessage.setText(rowItem.getLastMessage());
        holder.txtLastChattingDate.setText(rowItem.getDateTimeFormatted());
        holder.imageView.setImageResource(R.drawable.testing_image);

        SharedPrefUtil pref = new SharedPrefUtil(context.getApplicationContext());
        Set<String> unseenConversations = pref.getStringSet(Constants.CONVERSATION_NEW_MESSAGES_SET, new HashSet<String>());
        if(unseenConversations.contains(rowItem.getId())){
            convertView.setBackgroundColor(Color.parseColor("#A1BFFF"));
        }

        return convertView;
    }


}

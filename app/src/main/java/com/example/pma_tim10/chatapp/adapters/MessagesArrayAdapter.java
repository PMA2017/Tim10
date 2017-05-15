package com.example.pma_tim10.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.model.Person;

import java.util.List;

/**
 * Created by Dorian on 5/15/2017.
 */

public class MessagesArrayAdapter extends ArrayAdapter<Person> {

    private Context context;

    public MessagesArrayAdapter(Context context, int textViewResourceId, List<Person> objects){
        super(context,textViewResourceId,objects);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtConversationName;
        TextView txtLastMessage;
        TextView txtLastChattingDate;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MessagesArrayAdapter.ViewHolder holder = null;
        Person rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.messages_list_item, null);
            holder = new MessagesArrayAdapter.ViewHolder();
            holder.txtConversationName = (TextView) convertView.findViewById(R.id.conversation_name);
            holder.txtLastMessage = (TextView) convertView.findViewById(R.id.last_message);
            holder.txtLastChattingDate = (TextView) convertView.findViewById(R.id.last_chatting_date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);
        } else
            holder = (MessagesArrayAdapter.ViewHolder) convertView.getTag();

        holder.txtConversationName.setText(rowItem.getName());
        holder.txtLastMessage.setText(rowItem.getSurname());
        holder.txtLastChattingDate.setText("Online: " + rowItem.getAge() + " mins");
        holder.imageView.setImageResource(R.drawable.testing_image);


        return convertView;
    }

}

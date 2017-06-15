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
import com.example.pma_tim10.chatapp.model.User;

import java.util.List;

/**
 * Created by Dorian on 4/25/2017.
 */

public class FriendsArrayAdapter extends ArrayAdapter<User> {

    Context context;

    public FriendsArrayAdapter(Context context, int textViewResourceId, List<User> objects) {
        super(context,textViewResourceId,objects);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtFriendName;
        TextView txtFriendEmail;
        TextView txtFriendStatus;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        User friend = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friends_list_item, null);
            holder = new ViewHolder();
            holder.txtFriendName = (TextView) convertView.findViewById(R.id.friend_name);
            holder.txtFriendEmail = (TextView) convertView.findViewById(R.id.friend_email);
            holder.txtFriendStatus = (TextView) convertView.findViewById(R.id.friend_status);
            holder.imageView = (ImageView) convertView.findViewById(R.id.friend_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtFriendEmail.setText(friend.getEmail());
        holder.txtFriendName.setText(friend.getFullName());
        holder.txtFriendStatus.setText(friend.getStatus());
        int statusColor = friend.getStatus() == "online" ? Color.GREEN : Color.RED;
        holder.txtFriendStatus.setTextColor(statusColor);
        holder.imageView.setImageResource(R.drawable.testing_image);


        return convertView;
    }

}

package com.example.pma_tim10.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.model.User;

import java.util.List;

/**
 * Created by Dorian on 5/16/2017.
 */

public class PeopleArrayAdapter extends ArrayAdapter<User> {

    Context context;

    public PeopleArrayAdapter(Context context, int textViewResourceId, List<User> objects){
        super(context,textViewResourceId,objects);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtName;
        TextView txtEmail;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        PeopleArrayAdapter.ViewHolder holder = null;
        User rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.people_list_item, null);
            holder = new PeopleArrayAdapter.ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.person_name);
            holder.txtEmail = (TextView) convertView.findViewById(R.id.person_email);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);
        } else
            holder = (PeopleArrayAdapter.ViewHolder) convertView.getTag();

        holder.txtName.setText(rowItem.getFullName());
        holder.txtEmail.setText(rowItem.getEmail());
        holder.imageView.setImageResource(R.drawable.testing_image);

        return convertView;
    }

}

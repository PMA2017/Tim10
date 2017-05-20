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
 * Created by Dorian on 4/25/2017.
 */

public class FriendsArrayAdapter extends ArrayAdapter<Person> {

    Context context;

    public FriendsArrayAdapter(Context context, int textViewResourceId, List<Person> objects) {
        super(context,textViewResourceId,objects);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtMenuName;
        TextView txtMenuDesc;
        TextView txtPrice;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Person rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friends_list_item, null);
            holder = new ViewHolder();
            holder.txtMenuName = (TextView) convertView.findViewById(R.id.menu_name);
            holder.txtMenuDesc = (TextView) convertView.findViewById(R.id.description);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.price);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtMenuDesc.setText(rowItem.getSurname());
        holder.txtMenuName.setText(rowItem.getName());
        holder.txtPrice.setText("Age: " + rowItem.getAge());
        holder.imageView.setImageResource(R.drawable.testing_image);


        return convertView;
    }

}

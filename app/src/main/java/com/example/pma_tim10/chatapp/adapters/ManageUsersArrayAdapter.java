package com.example.pma_tim10.chatapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.fragments.ManageUsersDialogFragment;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.IConversationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aleksandar on 6/10/2017.
 */

public class ManageUsersArrayAdapter extends RecyclerView.Adapter<ManageUsersArrayAdapter.UserViewHolder> {

    private List<User> people;
    private HashMap<String,Boolean> members;

    public ManageUsersArrayAdapter(List<User> people,HashMap<String,Boolean> mem) {
        this.people = people;
        this.members = mem;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User user = people.get(position);
        holder.txtName.setText(user.getFullName());
        holder.txtEmail.setText(user.getEmail());
        int imageId = members.containsKey(user.getUid()) ? R.drawable.ic_remove_from_chat : R.drawable.ic_add_to_chat;
        holder.ibAddRemove.setImageResource(imageId);

        holder.ibAddRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(members.containsKey(user.getUid())){
                    members.remove(user.getUid());
                }else{
                    members.put(user.getUid(),true);
                }
                int imageId = members.containsKey(user.getUid()) ? R.drawable.ic_remove_from_chat : R.drawable.ic_add_to_chat;
                holder.ibAddRemove.setImageResource(imageId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtEmail;
        public ImageButton ibAddRemove;

        public UserViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.friend_name);
            txtEmail = (TextView) view.findViewById(R.id.friend_email);
            ibAddRemove = (ImageButton) view.findViewById(R.id.friend_add_remove);
        }
    }
}

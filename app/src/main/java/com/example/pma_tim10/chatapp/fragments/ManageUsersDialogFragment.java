package com.example.pma_tim10.chatapp.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.ManageUsersArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 6/10/2017.
 */

public class ManageUsersDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private List<User> friends;
    private ManageUsersArrayAdapter adapter;
    private IUserService userService;
    private Button btnClose;

    private String conversationId;

    public ManageUsersDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_users, container, false);
        setCancelable(false);

        friends = new ArrayList<>();
        userService = new UserService();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.chat_users_list);
        adapter = new ManageUsersArrayAdapter(friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(adapter);

        btnClose = (Button) rootView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        populateFriends();

        return rootView;
    }

    private void populateFriends() {
        userService.getFriends(new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                updateUI((List<User>)data);
            }
        });
    }

    private void updateUI(List<User> data) {
        friends.clear();
        friends.addAll(data);
        adapter.notifyDataSetChanged();
    }


}

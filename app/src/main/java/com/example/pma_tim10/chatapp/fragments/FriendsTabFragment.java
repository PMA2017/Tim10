package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.FriendsArrayAdapter;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.service.UserServiceImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FriendsTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private UserService friendsService;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        friendsService = new UserServiceImpl();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FriendsArrayAdapter friendsArrayAdapter = new FriendsArrayAdapter(getActivity(),android.R.id.list, friendsService.getMyFriends());

        setListAdapter(friendsArrayAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getActivity(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
    }
}

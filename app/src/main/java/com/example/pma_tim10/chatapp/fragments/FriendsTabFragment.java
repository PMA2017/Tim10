package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.UserDetailsActivity;
import com.example.pma_tim10.chatapp.adapters.FriendsArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private List<User> friends;
    private FriendsArrayAdapter friendsArrayAdapter;

    private IUserService userService;

    EditText searchField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userService = new UserService();

        searchField = (EditText) getActivity().findViewById(R.id.search_friends);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    updateUI(filterFriendsByCriteria(charSequence.toString(),friends));
                else
                    populateFriends();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        friends = new ArrayList<>();
        friendsArrayAdapter = new FriendsArrayAdapter(getActivity(), android.R.id.list, friends);
        setListAdapter(friendsArrayAdapter);
        getListView().setOnItemClickListener(this);

        populateFriends();
    }


    private void updateUI(List<User> data){
        friends.removeAll(friends);
        friends.addAll(data);
        friendsArrayAdapter.notifyDataSetChanged();;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) parent.getItemAtPosition(position);
        goToUserDetailsActivity(user.getUid());
    }

    private void goToUserDetailsActivity(String uid){
        Intent intent = new Intent(getActivity(),UserDetailsActivity.class);
        intent.putExtra(Constants.IE_USER_ID_KEY, uid);
        startActivity(intent);
        getActivity().finish();
    }


    // GET DATA FROM FIREBASE -- MUST BE IN ACTIVITY/FRAGMENT CLASSES -- UPDATE UI
    private void populateFriends() {
        userService.getFriends(new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                updateUI((List<User>)data);
            }
        });
    }

    private List<User> filterFriendsByCriteria(String criteria, List<User> friends) {
        ArrayList<User> result = new ArrayList<>();
        for (User u : friends) {
            String userFullName = u.getFullName().toLowerCase();
            if (userFullName.startsWith(criteria.toLowerCase()))
                result.add(u);
        }
        return result;
    }

}

package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.UserDetailsActivity;
import com.example.pma_tim10.chatapp.adapters.FriendsArrayAdapter;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.service.UserServiceImpl;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        friends = new ArrayList<>();
        friendsArrayAdapter = new FriendsArrayAdapter(getActivity(), android.R.id.list, friends);
        setListAdapter(friendsArrayAdapter);
        getListView().setOnItemClickListener(this);

        populateFriends();
    }


    private void updateUI(){
        friendsArrayAdapter.notifyDataSetChanged();
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
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // get uids of current user friends
        databaseReference.child(Constants.FRIENDSHIPS)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear adapter list if friendships are changed
                friends.clear();

                Map<String, Boolean> objects = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (objects == null)
                    return;

                // get details for friends
                // or update friend if changed
                for(final String friendId : objects.keySet()){
                    databaseReference.child(Constants.USERS).child(friendId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            // friend updated
                            if(friends.contains(user)){
                                int idx = friends.indexOf(user);
                                friends.remove(idx);
                                friends.add(idx,user);
                            }else{
                                friends.add(user);
                            }
                            updateUI();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                //update UI
                updateUI();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

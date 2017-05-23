package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Daniel on 5/22/2017.
 */

public class UserServiceImpl implements UserService {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    public UserServiceImpl() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void addFriend(String friendsUid) {
        // friendships:
        //logged_in_id:
        // friendsUid : true
        //friendsUid:
        // logged_in_id : true

        // add friend to logged in user
        databaseReference.child(Constants.FRIENDSHIPS).child(currentUser.getUid()).child(friendsUid).setValue(true);

        // add logged in user to friend
        databaseReference.child(Constants.FRIENDSHIPS).child(friendsUid).child(currentUser.getUid()).setValue(true);

    }

    @Override
    public void removeFriend(String friendsUid) {
        // friendships:
        //logged_in_id:
        // friendsUid : true
        //friendsUid:
        // logged_in_id : true

        // remove friend from logged in user
        databaseReference.child(Constants.FRIENDSHIPS).child(currentUser.getUid()).child(friendsUid).removeValue();

        // remove logged in user from friend
        databaseReference.child(Constants.FRIENDSHIPS).child(friendsUid).child(currentUser.getUid()).removeValue();
    }
}

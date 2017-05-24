package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 5/22/2017.
 */

public class UserService implements IUserService {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    public UserService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void getPeople(final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.USERS).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> people = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    // exclude current user
                    if(currentUser != null & user.getUid().equals(currentUser.getUid()))
                        continue;
                    // TO-DO : find and exclude friends
                    people.add(user);
                }

                //update ui
                callback.notifyUI(people);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getFriends(final IFirebaseCallback callback) {
        // get uids of current user friends
        databaseReference.child(Constants.FRIENDSHIPS)
                .child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear adapter list if friendships are changed
                final ArrayList<User> friends = new ArrayList<>();

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
                            callback.notifyUI(friends);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                //update UI
                callback.notifyUI(friends);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public void setOnline() {
        databaseReference.child(Constants.USERS).child(currentUser.getUid()).child(Constants.USER_ONLINE_FIELD).setValue(true);
    }

    @Override
    public void setOffline() {
        databaseReference.child(Constants.USERS).child(currentUser.getUid()).child(Constants.USER_ONLINE_FIELD).setValue(false);
    }

}

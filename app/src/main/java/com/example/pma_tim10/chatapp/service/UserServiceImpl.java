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
    public List<User> getAllUsers() {
        //
        // metoda treba da vraca 10 po 10 usera, sortiranih po broju zajednickih prijatelja
        // za sad nek stoji ovako


        final List<User> allUsers = new ArrayList<>();

        databaseReference.child(Constants.USER_TABLE).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    // exclude current user
                    if(user.getEmail().equals(currentUser.getEmail()))
                        continue;
                    // TO-DO : find and exclude friends

                    allUsers.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return allUsers;
    }

    @Override
    public List<User> getMyFriends() {

        final List<User> friends = new ArrayList<>();

        databaseReference.child(Constants.FRIENDSHIPS).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Boolean> objects = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (objects == null)
                    return;

                for(String friendId : objects.keySet()){
                    databaseReference.child(Constants.USER_TABLE).child(friendId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            friends.add(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return friends;
    }

    @Override
    public List<User> filterUsers() {
        return null;
    }


    @Override
    public void addFriend() {

    }
}

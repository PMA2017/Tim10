package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Daniel on 5/22/2017.
 */

public class AuthServiceImpl implements AuthService {

    private DatabaseReference databaseReference;

    public AuthServiceImpl() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean registerUser(String uid, User user) {
        try{
            databaseReference.child(Constants.USER_TABLE).child(uid).setValue(user);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
}

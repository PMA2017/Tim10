package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Daniel on 5/22/2017.
 */

public class AuthService implements IAuthService {

    private DatabaseReference databaseReference;

    public AuthService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean registerUser(String uid, User user) {
        try{
            databaseReference.child(Constants.USERS).child(uid).setValue(user);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user.getName() + " " + user.getSurname()).build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
}

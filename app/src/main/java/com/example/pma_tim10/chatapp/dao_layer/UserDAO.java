package com.example.pma_tim10.chatapp.dao_layer;

import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Dorian on 5/20/2017.
 */


public class UserDAO {

    private DatabaseReference databaseReference;

    public UserDAO(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void writeToDatabase(User user){
//        String username = new String(user.getUsername());
//        user.setUsername("");
//        databaseReference.child(Constants.USER_TABLE).child(username).setValue(user);
    }

}

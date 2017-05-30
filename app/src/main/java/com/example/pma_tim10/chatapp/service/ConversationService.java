package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
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
import java.util.List;

/**
 * Created by Dorian on 5/29/2017.
 */

public class ConversationService implements IConversationService {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    public ConversationService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public List<Conversation> getConversations(final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Conversation> conversations = new ArrayList<Conversation>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Conversation conversation = ds.getValue(Conversation.class);
                    // exclude current user
                    if (conversation.getMembers().containsKey(currentUser.getUid()))
                        conversations.add(conversation);
                }

                //update ui
                callback.notifyUI(conversations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }
}

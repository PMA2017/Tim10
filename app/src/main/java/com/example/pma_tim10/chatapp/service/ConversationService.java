package com.example.pma_tim10.chatapp.service;

import android.widget.ArrayAdapter;

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
import java.util.Map;

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
    public void getConversations(final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Conversation> conversations = new ArrayList<Conversation>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    Conversation conversation = ds.getValue(Conversation.class);

                    //current member conversations
                    if (conversation.getMembers() != null)
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
    }

    @Override
    public void addConversation(Conversation conversation, IFirebaseCallback callback) {
        DatabaseReference tempRef = databaseReference.child(Constants.CHATS).push();
        tempRef.child(Constants.CONVERSATION_FIELD_ID).setValue(tempRef.getKey());
        tempRef.child(Constants.CONVERSATION_FIELD_LAST_MESSAGE).setValue(conversation.getLastMessage());
        tempRef.child(Constants.CONVERSATION_FIELD_NAME).setValue(conversation.getName());
        tempRef.child(Constants.CONVERSATION_FIELD_TIMESTAMP).setValue(conversation.getTimestamp());
        for (Map.Entry<String,Object> entry : conversation.getMembers().entrySet()){
            tempRef.child(Constants.CONVERSATION_FIELD_MEMBERS).child(entry.getKey()).setValue(true);
        }
        conversation.setId(tempRef.getKey());
        ArrayList<Conversation> conversations = new ArrayList<>();
        conversations.add(conversation);
        callback.notifyUI(conversations);

    }
}

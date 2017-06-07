package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Dorian on 6/2/2017.
 */

public class MessageService implements IMessageService {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private IConversationService conversationService;

    public MessageService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.conversationService = new ConversationService();
    }

    @Override
    public void getMessages(String conversationId, final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES).child(conversationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<Message>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }
                callback.notifyUI(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendMessage(final Message message, final Collection<String> usersInChat, final String conversationId, final IFirebaseCallback callback) {
        conversationService.addOrUpdateConversation(conversationId,message,usersInChat, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES).child(conversationId).push();
                message.setId(tempRef.getKey());
                tempRef.setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.notifyUI(null);
                    }
                });
            }
        });
    }
}

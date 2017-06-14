package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.notifications.FcmNotificationBuilder;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;

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
        databaseReference.child(Constants.MESSAGES).child(conversationId).orderByChild(Constants.MESSAGE_FIELD_TIMESTAMP).addValueEventListener(new ValueEventListener() {
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
    public void sendMessage(final String conversationId, final Message message, final Map<String,User> usersInChat, final IFirebaseCallback callback) {
        final User current = usersInChat.get(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Task<Void> task = databaseReference.child(Constants.MESSAGES).child(conversationId).push().setValue(message);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // update conversation timestamp and last message
                conversationService.updateConversationLastMessageAndTimestamp(conversationId, message, new IFirebaseCallback() {
                    @Override
                    public void notifyUI(List data) {
                        // send notifications to all users
                        for(User u : usersInChat.values())
                            if(!u.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                FcmNotificationBuilder.initialize()
                                        .title(current.getFullName())
                                        .message(message.getContent())
                                        .username(current.getFullName())
                                        .uid(u.getUid())
                                        .firebaseToken(current.getFcmtoken())
                                        .conversationId(conversationId)
                                        .receiverFirebaseToken(u.getFcmtoken())
                                        .send();
                        callback.notifyUI(null);
                    }
                });
            }
        });
    }
}

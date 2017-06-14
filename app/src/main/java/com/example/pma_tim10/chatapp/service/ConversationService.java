package com.example.pma_tim10.chatapp.service;

import android.widget.ArrayAdapter;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dorian on 5/29/2017.
 */

public class ConversationService implements IConversationService {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private IUserService userService;

    public ConversationService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userService = new UserService();
    }

    @Override
    public void getConversations(final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).orderByChild(Constants.CONVERSATION_FIELD_TIMESTAMP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Conversation> conversations = new ArrayList<Conversation>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    Conversation conversation = ds.getValue(Conversation.class);

                    //current member conversations
                    if (conversation.getMembers() != null)
                        if (conversation.getMembers().containsKey(currentUser.getUid()) &&
                                conversation.getMembers().get(currentUser.getUid()) == true)
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
    public void getConversationIdForUserId(final String userId, final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Conversation> conversations = new ArrayList<Conversation>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    Conversation conversation = ds.getValue(Conversation.class);

                    //current member conversations
                    if (conversation.getMembers() != null)
                        if(conversation.getMembers().size() == 2)
                            if (conversation.getMembers().containsKey(currentUser.getUid())&&
                                    conversation.getMembers().containsKey(userId)) {
                                conversations.add(conversation);
                                break;
                            }
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
    public void getConversationUsers(String conversationId, final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).child(conversationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> users = new ArrayList<>();
                final Conversation conversation = dataSnapshot.getValue(Conversation.class);
                for(String userId : conversation.getMembers().keySet()){
                    userService.getUserDetails(userId, new IFirebaseCallback() {
                        @Override
                        public void notifyUI(List data) {
                            users.add((User)data.get(0));
                            if(users.size() == conversation.getMembers().size())
                                callback.notifyUI(users);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void createConversation(final Conversation conversation, final IFirebaseCallback callback) {
        Task<Void> task = databaseReference.child(Constants.CHATS).child(conversation.getId()).setValue(conversation);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.notifyUI(null);
            }
        });
    }

    @Override
    public void deleteConversation(final String conversationId, final IFirebaseCallback callback) {
        databaseReference.child(Constants.CHATS).child(conversationId).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(Constants.MESSAGES).child(conversationId).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.notifyUI(null);
                    }
                });
            }
        });
    }

    @Override
    public void updateConversationLastMessageAndTimestamp(final String conversationId, final Message message, final IFirebaseCallback callback) {
        Map updateConversation = new HashMap();
        String lastMessage = message.getSenderName() + ": " + message.getContent();
        updateConversation.put("lastMessage", lastMessage);
        Map timestampNow = new HashMap();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        updateConversation.put("timestampCreated", timestampNow);
        databaseReference.child(Constants.CHATS).child(conversationId).updateChildren(updateConversation, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                callback.notifyUI(null);
            }
        });
    }

    @Override
    public void updateUserStatusInConversation(String conversationId, String userId, final IFirebaseCallback callback) {
        databaseReference.child(Constants.CHATS).child(conversationId).child(Constants.CONVERSATION_FIELD_MEMBERS)
                .child(userId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.notifyUI(null);
            }
        });
    }

    @Override
    public void getConversation(final String conversationId, final IFirebaseCallback callback){
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).child(conversationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Conversation> conversations = new ArrayList<Conversation>();
                final Conversation conversation = dataSnapshot.getValue(Conversation.class);
                conversations.add(conversation);
                callback.notifyUI(conversations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateConversationUsers(final String conversationId,final Map<String, User> usersInChat,final IFirebaseCallback callback) {
        databaseReference.child(Constants.CHATS).child(conversationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //Key does not exist
                    Map<String,Boolean> convUsers = new HashMap<>();
                    for(String uid : usersInChat.keySet())
                        convUsers.put(uid,true);
                    Task<Void> task = databaseReference.child(Constants.CHATS).child(conversationId).child(Constants.CONVERSATION_FIELD_MEMBERS).setValue(convUsers);
                    task.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            callback.notifyUI(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateConversationName(String conversationId, String conversationName,final IFirebaseCallback callback) {
        Task<Void> task = databaseReference.child(Constants.CHATS).child(conversationId).child(Constants.CONVERSATION_FIELD_NAME).setValue(conversationName);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.notifyUI(null);
            }
        });
    }
}

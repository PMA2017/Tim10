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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void getConversationIdForUserId(final String userId, final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void getConversationUsers(String conversationId,final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.CHATS).child(conversationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> usersInChat = new ArrayList<>();
                final Conversation conversation = dataSnapshot.getValue(Conversation.class);
                for (String memberId : conversation.getMembers().keySet()){
                    userService.getUserDetails(memberId, new IFirebaseCallback() {
                        @Override
                        public void notifyUI(List data) {
                            usersInChat.add((User)data.get(0));
                            if(usersInChat.size() == conversation.getMembers().keySet().size())
                                callback.notifyUI(usersInChat);
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
    public void addOrUpdateConversation(String conversationId, final Message message, final Collection<String> usersInChatIds, final IFirebaseCallback callback) {
        final Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        String lastMessage = message.getSenderName() + ": " + message.getContent();
        conversation.setLastMessage(lastMessage);
        conversation.setTimestamp(System.currentTimeMillis());
        Map<String,Object> users = new HashMap<>();
        for(String userId : usersInChatIds)
            users.put(userId,true);
        conversation.setMembers(users);

        Task<Void> task = databaseReference.child(Constants.CHATS).child(conversationId).setValue(conversation);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.notifyUI(new ArrayList(){{add(conversation);}});
            }
        });
    }

}

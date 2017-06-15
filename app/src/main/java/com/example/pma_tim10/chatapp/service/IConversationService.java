package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Dorian on 5/29/2017.
 */

public interface IConversationService {

    void getConversations(final IFirebaseCallback callback);
    void getConversationIdForUserId(String userId, final IFirebaseCallback callback);


    void createConversation(final Conversation conversation, final IFirebaseCallback callback);
    void deleteConversation(final Conversation conversation, final IFirebaseCallback callback);

    void getConversation(final String conversationId, final IFirebaseCallback callback);

    void updateConversationLastMessageAndTimestamp(final String conversationId, final Message message, final IFirebaseCallback callback);
    void updateUserStatusInConversation(final String conversationId, final String userId, final IFirebaseCallback callback);

    void updateConversationUsers(String conversationId, final Map<String, User> usersInChat, final IFirebaseCallback callback);
    void updateConversationName(String conversationId, String conversationName, final IFirebaseCallback callback);

    void getConversationUsers(String conversationId, final IFirebaseCallback callback);
}

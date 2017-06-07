package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Message;

import java.util.Collection;

/**
 * Created by Dorian on 5/29/2017.
 */

public interface IConversationService {

    void getConversations(final IFirebaseCallback callback);
    void getConversationIdForUserId(String userId, final IFirebaseCallback callback);

    void addOrUpdateConversation(String conversationId, final Message message, final Collection<String> usersInChatIds, final IFirebaseCallback callback);

    void getConversationUsers(String conversationId,final IFirebaseCallback callback);
}

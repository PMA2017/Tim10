package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;

import java.util.Collection;
import java.util.List;

/**
 * Created by Dorian on 6/2/2017.
 */

public interface IMessageService {

    void getMessages(String conversationId, IFirebaseCallback callback);
    void sendMessage(final Message message, final Collection<String> usersInChat, final String conversationId, final IFirebaseCallback callback);

}

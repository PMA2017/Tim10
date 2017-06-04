package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;

/**
 * Created by Dorian on 6/2/2017.
 */

public interface IMessageService {

    public void getMessages(String conversationId, IFirebaseCallback callback);
    public void addMessage(Message message, String id);

}

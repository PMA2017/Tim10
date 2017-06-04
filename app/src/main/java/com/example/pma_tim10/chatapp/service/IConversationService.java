package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;

import java.util.List;

/**
 * Created by Dorian on 5/29/2017.
 */

public interface IConversationService {

    public void getConversations(final IFirebaseCallback callback);
    public void addConversation(Conversation conversation, IFirebaseCallback callback);

}

package com.example.pma_tim10.chatapp.service;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.callback.IFirebaseFileUploadCallback;
import com.example.pma_tim10.chatapp.callback.IFirebaseProgressCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Dorian on 6/2/2017.
 */

public interface IMessageService {

    void getMessages(String conversationId, IFirebaseCallback callback);
    void sendMessage(final String conversationId, final Message message, final Map<String,User> usersInChat, final IFirebaseCallback callback);

    void uploadFile(final Conversation conversation, final Uri uri, final IFirebaseProgressCallback progressCallback, final IFirebaseFileUploadCallback successCallback, final IFirebaseCallback errorCallback);

    void downloadFile(final Conversation conversation, final String fileName, final String realName, final IFirebaseProgressCallback progressCallback, final IFirebaseFileUploadCallback successCallback, final IFirebaseCallback errorCallback);
}

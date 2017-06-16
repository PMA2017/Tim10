package com.example.pma_tim10.chatapp.service;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.callback.IFirebaseFileUploadCallback;
import com.example.pma_tim10.chatapp.callback.IFirebaseProgressCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.notifications.FcmNotificationBuilder;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dorian on 6/2/2017.
 */

public class MessageService implements IMessageService {

    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private IConversationService conversationService;

    public MessageService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.conversationService = new ConversationService();
    }

    @Override
    public void getMessages(String conversationId, final IFirebaseCallback callback) {
        databaseReference.child(Constants.MESSAGES).child(conversationId).orderByChild(Constants.MESSAGE_FIELD_TIMESTAMP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<Message>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
    public void sendMessage(final String conversationId, final Message message, final Map<String, User> usersInChat, final IFirebaseCallback callback) {
        final User current = usersInChat.get(FirebaseAuth.getInstance().getCurrentUser().getUid());

        message.setSenderName(currentUser.getDisplayName());
        message.setSender(currentUser.getUid());
        message.setConversationId(conversationId);

        Task<Void> task = databaseReference.child(Constants.MESSAGES).child(conversationId).push().setValue(message);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // update conversation timestamp and last message
                conversationService.updateConversationLastMessageAndTimestamp(conversationId, message, new IFirebaseCallback() {
                    @Override
                    public void notifyUI(List data) {
                        // send notifications to all users
                        for (User u : usersInChat.values())
                            if (!u.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
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

    @Override
    public void uploadFile(final Conversation conversation, final Uri uri,
                           final IFirebaseProgressCallback progressCallback,
                           final IFirebaseFileUploadCallback successCallback,
                           final IFirebaseCallback errorCallback) {

        String fileName = UUID.randomUUID().toString();
        String path = Constants.CHAT_FILES + "/" + conversation.getId() + "/" + fileName;

//            StorageMetadata metadata = new StorageMetadata.Builder()
//                    .setContentType("image/jpg")
//                    .build();

        UploadTask uploadTask = storageReference.child(path).putFile(uri);
        uploadTask
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressCallback.changeProgressBarStatus(progress);
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String fileName = taskSnapshot.getMetadata().getName();
                    String fileExtension = taskSnapshot.getMetadata().getContentType().split("/")[1];
                    successCallback.notify(fileName,fileExtension);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    errorCallback.notifyUI(null);
                }
            });
    }

    @Override
    public void downloadFile(final Conversation conversation, final String fileName,final String realName, final IFirebaseProgressCallback progressCallback, final IFirebaseFileUploadCallback successCallback, final IFirebaseCallback errorCallback) {
        String path = Constants.CHAT_FILES + "/" + conversation.getId() + "/" + fileName;

        File downloadsFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File localFile = new File(downloadsFilePath,realName);

        storageReference.child(path).getFile(localFile)
            .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressCallback.changeProgressBarStatus(progress);
                }
            })
            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    successCallback.notify(null);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    errorCallback.notifyUI(null);
                }
            });

    }
}

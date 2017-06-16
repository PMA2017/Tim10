package com.example.pma_tim10.chatapp.service;

import android.graphics.Bitmap;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.callback.IFirebaseProgressCallback;
import com.example.pma_tim10.chatapp.model.User;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Daniel on 5/22/2017.
 */

public interface IUserService {

    void getPeople(IFirebaseCallback callback);
    void getFriends(IFirebaseCallback callback);
    void getUserDetails(String userId, IFirebaseCallback callback);

    void addFriend(String friendsUid);
    void removeFriend(String friendsUid);

    void setOnline();
    void setOffline();

    void uploadPhoto(Bitmap bitmap, final IFirebaseProgressCallback progressCallback, final IFirebaseCallback successCallback, final IFirebaseCallback errorCallback);


    void checkUserByUid(String uid, User user, IFirebaseCallback callback);

    void setFcmToken(final String token);

}

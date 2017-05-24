package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;

/**
 * Created by Daniel on 5/22/2017.
 */

public interface IUserService {

    void getPeople(IFirebaseCallback callback);
    void getFriends(IFirebaseCallback callback);

    void addFriend(String friendsUid);
    void removeFriend(String friendsUid);

    void setOnline();
    void setOffline();

}

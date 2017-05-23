package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.model.User;

import java.util.List;

/**
 * Created by Daniel on 5/22/2017.
 */

public interface UserService {

    List<User> getAllUsers();
    List<User> getMyFriends();
    List<User> filterUsers();

    void addFriend(String friendsUid);
    void removeFriend(String friendsUid);
}

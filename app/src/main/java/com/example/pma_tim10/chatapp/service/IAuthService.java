package com.example.pma_tim10.chatapp.service;

import com.example.pma_tim10.chatapp.model.User;

/**
 * Created by Daniel on 5/22/2017.
 */

public interface IAuthService {

    boolean registerUser(String uid, User user);

}

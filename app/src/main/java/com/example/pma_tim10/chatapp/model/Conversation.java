package com.example.pma_tim10.chatapp.model;

import java.util.Set;

/**
 * Created by Dorian on 5/20/2017.
 */

public class Conversation {
    private Integer id;
    private Set<User> users;
    private Set<Message> messages;

    public Conversation() {

    }

    public Conversation(Integer id, Set<User> users, Set<Message> messages) {
        this.id = id;
        this.users = users;
        this.messages = messages;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
}

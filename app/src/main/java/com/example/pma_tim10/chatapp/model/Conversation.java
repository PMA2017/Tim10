package com.example.pma_tim10.chatapp.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dorian on 5/20/2017.
 */

public class Conversation {
    private Integer id;
    private String name;
    private Map<String,Boolean> members;
    private String lastMessage;
    private Timestamp timestamp;

    public Conversation() {

    }

    public Conversation(Integer id, String name, Map<String, Boolean> members, String lastMessage, Timestamp timestamp) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

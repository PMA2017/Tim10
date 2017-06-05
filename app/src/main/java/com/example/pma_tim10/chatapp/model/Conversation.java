package com.example.pma_tim10.chatapp.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Dorian on 5/20/2017.
 */

public class Conversation {
    private String id;
    private String name;
    private Map<String,Object> members;
    private String lastMessage;
    private Long timestamp;

    public Conversation() {

    }

    public Conversation(String id, String name, Map<String, Object> members, String lastMessage, Long timestamp) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getDateTimeFormatted(){
        Date date = new Date(this.timestamp);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Object> members) {
        this.members = members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

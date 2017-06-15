package com.example.pma_tim10.chatapp.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Dorian on 5/20/2017.
 */

public class Conversation {
    private String id;
    private String name;
    private Map<String,Boolean> members;
    private String lastMessage;
    private boolean isGroup;

    private HashMap<String, Object> timestampCreated;

    public Conversation() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    @Exclude
    public String getDateTimeFormatted(){
        Date date = new Date(getTimestampCreatedLong());
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
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

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    //    public Long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Long timestamp) {
//        this.timestamp = timestamp;
//    }
}

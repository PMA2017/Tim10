package com.example.pma_tim10.chatapp.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Dorian on 5/20/2017.
 */

public class Message {
    private String id;
    private Long timestamp;
    private String content;
    private Double longitude;
    private Double latitude;
    private String sender;
    private String senderName;

    private HashMap<String, Object> timestampCreated;

    public Message() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    public Message(String id, Long timestamp, String content, Double longitude, Double latitude, String sender, String senderName) {
        this.id = id;
        this.timestamp = timestamp;
        this.content = content;
        this.longitude = longitude;
        this.latitude = latitude;
        this.sender = sender;
        this.senderName = senderName;

        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    @Exclude
    public String getDateTimeFormatted(){
        Date date = new Date(getTimestampCreatedLong());
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }


    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}

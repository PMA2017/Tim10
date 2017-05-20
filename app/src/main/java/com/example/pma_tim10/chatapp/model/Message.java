package com.example.pma_tim10.chatapp.model;

import java.util.Date;

/**
 * Created by Dorian on 5/20/2017.
 */

public class Message {
    private Integer id;
    private Date date;
    private String content;
    private Double longitude;
    private Double latitude;
    private User sender;

    public Message() {

    }

    public Message(Integer id, Date date, String content, Double longitude, Double latitude, User sender) {
        this.id = id;
        this.date = date;
        this.content = content;
        this.longitude = longitude;
        this.latitude = latitude;
        this.sender = sender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}

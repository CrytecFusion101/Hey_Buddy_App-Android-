package com.heybuddy.Model;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    private String id;
    private String title;
    private String body;
    private String receiverDeviceToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiverDeviceToken() {
        return receiverDeviceToken;
    }

    public void setReceiverDeviceToken(String receiverDeviceToken) {
        this.receiverDeviceToken = receiverDeviceToken;
    }
}

package com.heybuddy.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class sendMessage {

    @SerializedName(" text_type")
    @Expose
    private String textType;
    @SerializedName(" receiver_id")
    @Expose
    private String receiverId;
    @SerializedName(" msg_status")
    @Expose
    private String msgStatus;
    @SerializedName(" receiver_name")
    @Expose
    private String receiverName;
    @SerializedName(" sender_name")
    @Expose
    private String senderName;
    @SerializedName(" text")
    @Expose
    private String text;
    @SerializedName(" sender_id")
    @Expose
    private String senderId;
    @SerializedName(" timestamp")
    @Expose
    private String timestamp;

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}

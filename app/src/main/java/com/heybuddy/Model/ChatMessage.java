package com.heybuddy.Model;

import com.heybuddy.enumeration.ChatType;
import com.heybuddy.enumeration.MessageType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.heybuddy.utility.Util.parseLong;

public class ChatMessage {


    private static SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    private ChatType type;
//    private MessageState messageState;

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
    private String message;
    @SerializedName(" sender_id")
    @Expose
    private String senderId;
    @SerializedName(" timestamp")
    @Expose
    private String timestamp;

    private String formatted_time = null;
    private MessageType messageType;


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

    public String getSenderId() {
        if (senderId == null) return "";
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


    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getFormattedTime() {
        if (formatted_time != null && !formatted_time.isEmpty()) return formatted_time;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(parseLong(timestamp));
        formatted_time = formatter.format(calendar.getTime());
        return formatted_time;
    }

}

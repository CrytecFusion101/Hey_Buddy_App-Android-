package com.heybuddy.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ChatList {


        public String sender_id;
        public String sender_name;
        public String receiver_id;
        public String receiver_name;
        public String text;
        public String text_type;
        public String timestamp;
        public String msg_status;
        public int starCount = 0;
        public Map<String, Boolean> stars = new HashMap<>();

        public ChatList() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

    public ChatList(String sender_id, String sender_name, String receiver_id, String receiver_name, String text, String text_type, String timestamp, String msg_status, int starCount, Map<String, Boolean> stars) {
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.receiver_id = receiver_id;
        this.receiver_name = receiver_name;
        this.text = text;
        this.text_type = text_type;
        this.timestamp = timestamp;
        this.msg_status = msg_status;
        this.starCount = starCount;
        this.stars = stars;
    }

    @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();

            result.put("sender_id", sender_id);
            result.put("sender_name",sender_name);
            result.put("receiver_id",receiver_id);
            result.put("receiver_name", receiver_name);
            result.put("text", text);
            result.put("text_type", "text");
            result.put("timestamp", ServerValue.TIMESTAMP);
            result.put("msg_status", "read");
            return result;
        }

}

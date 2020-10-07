package com.heybuddy.enumeration;


public enum MessageState {

    //Make sure UNREAD is always 0
    PENDING(0), SENT(1), RECEIVED(2), READ(3), ERROR(4);

    public int getId() {
        return id;
    }

    private int id;

    MessageState(int id) {
        this.id = id;
    }
}

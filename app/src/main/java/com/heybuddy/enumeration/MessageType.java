package com.heybuddy.enumeration;

public enum MessageType {
    SENDER(1), RECEIVER(2), HEADER(3), NOTE(4);

    public int getId() {
        return id;
    }
    private int id;

    MessageType(int id) {
        this.id = id;
    }
}

package com.heybuddy.enumeration;


public enum ChatType {
    CHAT(0), GROUP(1), CAST(2);

    public int getId() {
        return id;
    }

    int id;

    ChatType(int i) {
        this.id = i;
    }
}

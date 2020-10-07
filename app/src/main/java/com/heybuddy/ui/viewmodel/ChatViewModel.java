/*
package com.heybuddy.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.Immerch.model.xmpp.Contact;
import com.app.Immerch.model.xmpp.Item;
import com.heybuddy.Model.ChatMessage;
import com.heybuddy.enumeration.MessageType;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private LiveData<List<ChatMessage>> chats;

    public ChatViewModel(Application application) {
        super(application);
    }

    private List<ChatMessage> getDummyChatList() {
        List<ChatMessage> chats = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            ChatMessage message = new ChatMessage();
            message.setMessage("Hello, How are you?");
            if (i % 2 == 0) {
                message.setMessageType(MessageType.RECEIVER);
            } else {
                message.setMessageType(MessageType.SENDER);
            }
            chats.add(message);
        }
        return chats;
    }


    public LiveData<List<ChatMessage>> getChats() {
        return getDummyChatList();
    }

    public LiveData<List<ChatMessage>> getSortedChat(String room_id) {
        return mRepository.getSortedChats(room_id);
    }


    public void insert(ChatMessage chat) {
        mRepository.insert(chat);
    }

    public void insertAll(ArrayList<ChatMessage> chats) {
        mRepository.insertAll(chats);
    }

}
*/

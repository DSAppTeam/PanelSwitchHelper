package com.example.demo.scene.chat.Adapter;

/**
 * the information of chatting item
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatInfo {

    public String message;
    public boolean owner;
    public static boolean sBoolean = true;

    public ChatInfo(String message, boolean owner) {
        this.message = message;
        this.owner = owner;
    }

    public static ChatInfo CREATE(String message) {
        ChatInfo chatInfo = new ChatInfo(message, sBoolean);
        sBoolean = !sBoolean;
        return chatInfo;
    }
}

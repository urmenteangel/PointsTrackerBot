package com.SoleraBootcamp4.PointsTrackerBot.model;

public class TelegramMessage {

    private MessageSender sender;
    private MessageChat chat;
    private String text;

    public TelegramMessage(MessageSender sender, MessageChat chat, String text) {
        this.sender = sender;
        this.chat = chat;
        this.text = text;
    }

    public MessageSender getSender() {
        return sender;
    }

    public MessageChat getChat() {
        return chat;
    }

    public String getText() {
        return text;
    }

}

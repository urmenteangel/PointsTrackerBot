package com.SoleraBootcamp4.PointsTrackerBot.model;

public class MessageSender {

    private String id;
    private boolean isBot;
    private String username;
    private String firstName;
    private String lastName;

    public MessageSender(String id, boolean isBot, String firstName, String lastName, String username) {
        this.id = id;
        this.isBot = isBot;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public boolean isBot() {
        return isBot;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    


}

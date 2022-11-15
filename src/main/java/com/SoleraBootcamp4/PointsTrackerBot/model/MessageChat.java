package com.SoleraBootcamp4.PointsTrackerBot.model;

public class MessageChat {

    private String id;
    private String title;
    private boolean isGroupOrSuperGroup;

    public MessageChat(String id, String title, boolean isGroupOrSuperGroup) {
        this.id = id;
        this.title = title;
        this.isGroupOrSuperGroup = isGroupOrSuperGroup;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isGroupOrSuperGroup() {
        return isGroupOrSuperGroup;
    }

}

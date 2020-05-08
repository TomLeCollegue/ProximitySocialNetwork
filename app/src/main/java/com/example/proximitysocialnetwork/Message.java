package com.example.proximitysocialnetwork;

public class Message {
    private String text;
    private String contact;
    private boolean self;
    private String time;

    public Message(String text, String contact, boolean self, String time) {
        this.text = text;
        this.contact = contact;
        this.self = self;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

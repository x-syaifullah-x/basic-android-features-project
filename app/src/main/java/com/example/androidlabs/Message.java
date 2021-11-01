package com.example.androidlabs;

public class Message {
    private Long id;
    private boolean isSend;
    private String text;

    public Message(Long id, boolean isSend, String text) {
        this.id = id;
        this.isSend = isSend;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

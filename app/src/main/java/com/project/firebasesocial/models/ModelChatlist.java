package com.project.firebasesocial.models;

public class ModelChatlist {

    String id; //we need this id to get chat list, sender/receiver uid;

    public ModelChatlist() {
    }

    public ModelChatlist(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

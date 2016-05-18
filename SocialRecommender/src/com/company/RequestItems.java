package com.company;

import jade.content.AgentAction;

public class RequestItems implements AgentAction {
    private int user_id;

    public RequestItems() { }

    public RequestItems(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }
}

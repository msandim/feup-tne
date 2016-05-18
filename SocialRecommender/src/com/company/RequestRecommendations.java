package com.company;

import jade.content.AgentAction;

public class RequestRecommendations implements AgentAction {
    private int user_id;

    public RequestRecommendations() { }

    public RequestRecommendations(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }
}

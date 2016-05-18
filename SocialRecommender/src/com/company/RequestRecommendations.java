package com.company;

import jade.content.AgentAction;

public class RequestRecommendations implements AgentAction {
    public int user_id;

    public RequestRecommendations() { }

    public RequestRecommendations(int user_id) {
        this.user_id = user_id;
    }
}

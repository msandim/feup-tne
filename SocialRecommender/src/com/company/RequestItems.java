package com.company;

import jade.content.AgentAction;

public class RequestItems implements AgentAction {
    public int user_id;

    public RequestItems() {

    }

    public RequestItems(int user_id) {
        this.user_id = user_id;
    }
}

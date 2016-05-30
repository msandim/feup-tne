package platform.services;

import jade.content.AgentAction;

public class RequestRecommendation implements AgentAction {
    private int user_id;

    public RequestRecommendation() { }

    public RequestRecommendation(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }
}

package platform.services;

import jade.content.AgentAction;

public class RequestRecommendation implements AgentAction {
    private int user_id;
    private int size;

    public RequestRecommendation() { }

    public RequestRecommendation(int user_id, int size) {
        this.user_id = user_id;
        this.size = size;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }
}

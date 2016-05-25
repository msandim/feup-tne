package platform.services;

import jade.content.AgentAction;

public class ChangeTrust implements AgentAction {
    private int user_id1;
    private int user_id2;
    private int value;

    public ChangeTrust() { }

    public ChangeTrust(int user_id1, int user_id2, int value) {
        this.user_id1 = user_id1;
        this.user_id2 = user_id2;
        this.value = value;
    }

    public int getUser_id1() { return user_id1; }

    public void setUser_id1(int user_id1) { this.user_id1 = user_id1; }

    public int getUser_id2() { return user_id2; }

    public void setUser_id2(int user_id2) { this.user_id2 = user_id2; }

    public int getValue() { return value; }

    public void setValue(int value) { this.value = value; }
}

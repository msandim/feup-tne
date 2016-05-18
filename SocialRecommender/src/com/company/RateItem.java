package com.company;

import jade.content.AgentAction;

public class RateItem implements AgentAction {
    public int user_id;
    public int item_id;
    public double rating;

    public RateItem() { }

    public RateItem(int user_id, int item_id, double rating) {
        this.user_id = user_id;
        this.item_id = item_id;
        this.rating = rating;
    }
}

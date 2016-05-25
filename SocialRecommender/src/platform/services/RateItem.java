package platform.services;

import jade.content.AgentAction;

public class RateItem implements AgentAction {
    private int user_id;
    private int item_id;
    private double rating;

    public RateItem() { }

    public RateItem(int user_id, int item_id, double rating) {
        this.user_id = user_id;
        this.item_id = item_id;
        this.rating = rating;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getItem_id() { return item_id; }

    public void setItem_id(int item_id) { this.item_id = item_id; }

    public double getRating() { return rating; }

    public void setRating(double rating) { this.rating = rating; }
}

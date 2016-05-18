package com.company;

public class Recommendation {
    public int user_id;
    public int item_id;
    public double rating;

    public Recommendation() { }

    public Recommendation(int user_id, int item_id, double rating) {
        this.user_id = user_id;
        this.item_id = item_id;
        this.rating = rating;
    }
}

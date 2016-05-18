package com.company;

import jade.content.Predicate;

import java.util.List;

public class Recommendations implements  Predicate {
    public List<Recommendation> recommendations;

    public Recommendations() { }

    public Recommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}

package com.company;

import jade.content.Predicate;
import java.util.List;

public class Items implements Predicate {
    List<Integer> items;

    public Items() { }

    public Items(List<Integer> items) {
        this.items = items;
    }

    public List<Integer> getItems() {
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }

}

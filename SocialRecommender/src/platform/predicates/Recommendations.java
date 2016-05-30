package platform.predicates;

import jade.content.Predicate;

import java.util.List;
import java.util.Map;

public class Recommendations implements  Predicate {
    private int user_id;
    private List<Map.Entry<Integer, Double>> recommendations;

    public Recommendations() { }

    public Recommendations(int user_id, List<Map.Entry<Integer, Double>> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }

    public List<Map.Entry<Integer, Double>> getRecommendations() { return recommendations; }

    public void setRecommendations(List<Map.Entry<Integer, Double>> recommendations) { this.recommendations = recommendations; }
}

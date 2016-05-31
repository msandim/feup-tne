package platform.predicates;

import jade.content.Predicate;

import java.util.List;

public class Recommendations implements  Predicate {
    private int user_id;
    private List<Recommendation> recommendations;

    public Recommendations() { }

    public Recommendations(int user_id, List<Recommendation> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }

    public List<Recommendation> getRecommendations() { return recommendations; }

    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
}

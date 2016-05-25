package jade.predicates;

import jade.predicates.Recommendation;
import jade.content.Predicate;

import java.util.List;

public class Recommendations implements  Predicate {
    private List<Recommendation> recommendations;

    public Recommendations() { }

    public Recommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public List<Recommendation> getRecommendations() { return recommendations; }

    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
}

package platform.ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class RecommendationOntology extends BeanOntology {

    public static final String ONTOLOGY_NAME = "recommendation";

    // Singleton instance of this ontology
    private static Ontology theInstance = new RecommendationOntology();

    // Method to access the singleton ontology object
    public static Ontology getInstance() {
        return theInstance;
    }

    // Private constructor
    private RecommendationOntology() {
        super(ONTOLOGY_NAME);

        try {
            // Predicate
            add(platform.predicates.Items.class);
            add(platform.predicates.Recommendations.class);
            // AgentAction
            add(platform.services.ChangeTrust.class);
            add(platform.services.RateItem.class);
            add(platform.services.RequestItems.class);
            add(platform.services.RequestRecommendation.class);
        } catch(BeanOntologyException boe) {
            boe.printStackTrace();
        }
    }

}
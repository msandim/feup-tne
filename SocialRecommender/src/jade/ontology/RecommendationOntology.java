package jade.ontology;

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
            // add all Concept, Predicate and AgentAction in the current package
            add(this.getClass().getPackage().getName());

        } catch(BeanOntologyException boe) {
            boe.printStackTrace();
        }
    }

}
package platform;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import platform.ontology.RecommendationOntology;
import platform.predicates.Items;
import platform.predicates.Recommendation;
import platform.predicates.Recommendations;
import jade.proto.AchieveREInitiator;
import platform.services.ChangeTrust;

import java.util.Vector;

public class User extends Agent {
    private Codec codec;
    private Ontology recommendationOntology;

    public void setup() {
        System.out.println("User: Setup");

        this.codec = new SLCodec();
        getContentManager().registerLanguage(codec);

        this.recommendationOntology = RecommendationOntology.getInstance();
        getContentManager().registerOntology(recommendationOntology);

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("recommendation");
        template.addServices(sd);

        AID recommender = null;
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                recommender = result[0].getName();
            } else {
                System.out.println("No recommender registered");
                return;
            }
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new UserBehaviour(this, new ACLMessage(ACLMessage.REQUEST), recommender));
    }

    class UserBehaviour extends AchieveREInitiator {
        private AID recommender;

        public UserBehaviour(Agent a, ACLMessage msg, AID recommender) {
            super(a, msg);
            this.recommender = recommender;
        }

        protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
            System.out.println("User: prepareRequests");

            Vector<ACLMessage> v = new Vector<ACLMessage>();

            msg.setLanguage(codec.getName());
            msg.setOntology(recommendationOntology.getName());
            msg.addReceiver(recommender);

            ChangeTrust sae = new ChangeTrust(1, 2, 1);
            //RateItem sae = new RateItem(1, 1, 1);
            //RequestItems sae = new RequestItems(1);
            //RequestRecommendation sae = new RequestRecommendation(1);

            Action action = new Action(recommender, sae);

            try {
                getContentManager().fillContent(msg, action);
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }

            v.add(msg);
            return v;
        }

        protected void handleFailure(ACLMessage failure) {
            System.out.println("User: handleFailure");

        }

        protected void handleInform(ACLMessage inform) {
            System.out.println("User: handleInform");

            // Action with no result executed successfully
            if (inform.getContent() == null)
                return;

            try {
                ContentElement ce = getContentManager().extractContent(inform);
                if(ce instanceof Recommendations) {
                    Recommendations recommendations = (Recommendations) ce;

                    // Print Recommendations
                    System.out.println("Recommendations:");

                    for (Recommendation recommendation: recommendations.getRecommendations()) {
                        System.out.println("user_id: " + recommendation.getUser_id() + ", item_id: "
                                + recommendation.getItem_id() + ", rating: " + recommendation.getRating());
                    }
                } else if(ce instanceof Items) {
                    Items items = (Items) ce;

                    // Print Items
                    System.out.println("Items:");
                    for(int i = 0; i < items.getItems().size(); i++) {
                        System.out.println(items.getItems().get(i));
                    }
                }
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
        }
    }
}

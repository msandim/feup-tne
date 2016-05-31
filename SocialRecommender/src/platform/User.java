package platform;

import jade.content.AgentAction;
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
import platform.services.*;
import java.util.Vector;

public class User extends Agent {
    private Codec codec;
    private Ontology recommendationOntology;
    private AID recommender;
    private UserGUI userGUI;
    private int user_id;

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

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                this.recommender = result[0].getName();
            } else {
                System.out.println("No recommender registered");
                return;
            }
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        this.user_id = 1;
        this.userGUI = new UserGUI(this, user_id);
        this.userGUI.loadGUI();
    }

    public void changeTrust(int user_id1, int user_id2, String value) {
        ChangeTrust sae = new ChangeTrust(1, 2, "1");
        addBehaviour(new UserBehaviour(this, new ACLMessage(ACLMessage.REQUEST), recommender, sae));
    }

    public void rateItem(int user_id, int item_id, String value) {
        RateItem sae = new RateItem(1, 1, "1");
        addBehaviour(new UserBehaviour(this, new ACLMessage(ACLMessage.REQUEST), recommender, sae));
    }

    public void requestItems(int user_id) {
        RequestItems sae = new RequestItems(1);
        addBehaviour(new UserBehaviour(this, new ACLMessage(ACLMessage.REQUEST), recommender, sae));
    }

    public void requestRecommendation(int user_id) {
        RequestRecommendation sae = new RequestRecommendation(1);
        addBehaviour(new UserBehaviour(this, new ACLMessage(ACLMessage.REQUEST), recommender, sae));
    }

    class UserBehaviour extends AchieveREInitiator {
        private AID recommender;
        private AgentAction aa;

        public UserBehaviour(Agent a, ACLMessage msg, AID recommender, AgentAction aa) {
            super(a, msg);
            this.recommender = recommender;
            this.aa = aa;
        }

        protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
            System.out.println("User: prepareRequests");

            Vector<ACLMessage> v = new Vector<ACLMessage>();

            msg.setLanguage(codec.getName());
            msg.setOntology(recommendationOntology.getName());
            msg.addReceiver(recommender);

            Action action = new Action(recommender, aa);

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

                    for (Recommendation item : recommendations.getRecommendations()) {
                        Integer item_id = item.getItem_id();
                        Double rating = item.getRating();
                        System.out.println("item_id: " + item_id + ", rating: " + rating);
                    }
                    userGUI.load_recommendations(recommendations);
                } else if(ce instanceof Items) {
                    Items items = (Items) ce;

                    // Print Items
                    userGUI.load_items(items.getItems());
                }
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
        }
    }
}

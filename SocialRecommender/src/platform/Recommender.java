package platform;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import platform.ontology.RecommendationOntology;
import platform.predicates.Items;
import platform.predicates.Recommendation;
import platform.predicates.Recommendations;
import jade.proto.AchieveREResponder;
import platform.services.*;
import recommender.RecommenderSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recommender extends Agent {
    private Codec codec;
    private Ontology recommendationOntology;
    private RecommenderSystem recommenderSystem;
    private static final int NUM_USERS = 1642;
    private static final int NUM_ITEMS = 2071;
    
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            ContainerController cc = rt.createMainContainer(p);
            AgentController recommender = cc.createNewAgent("Recommender", "platform.Recommender", args);
            recommender.start();
            AID aid = new AID();
            aid.setLocalName("Recommender");
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            return;
        }
    }

    public void setup() {
        System.out.println("Recommender: Setup");

        // Parse input
        Object[] args = getArguments();
        if (args == null || args.length != 2) {
            System.err.println("Error parsing args: expected config filename and algorithm name");
            return;
        }
        String config_filename = args[0].toString(); // Config_filename
        String algorithm_name = args[1].toString(); // Algorithm_name
        this.recommenderSystem = new RecommenderSystem(NUM_USERS, NUM_ITEMS, config_filename, algorithm_name);

        // Load codec
        this.codec = new SLCodec();
        getContentManager().registerLanguage(codec);

        // Load ontology
        this.recommendationOntology = RecommendationOntology.getInstance();
        getContentManager().registerOntology(recommendationOntology);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("recommendation");
        sd.setName(getLocalName());

        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(recommendationOntology.getName()),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        addBehaviour(new RecommenderBehaviour(this, mt));
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    class RecommenderBehaviour extends AchieveREResponder {

        public RecommenderBehaviour(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage prepareResponse(ACLMessage request) {
            return null;
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            System.out.println("Recommender: prepareResultNotification");

            ACLMessage result = request.createReply();

            // If an exception is thrown
            result.setPerformative(ACLMessage.FAILURE);

            try {
                ContentElement ce = getContentManager().extractContent(request);

                if(ce instanceof Action) {
                    AgentAction action = (AgentAction) ((Action) ce).getAction();
                    if(action instanceof ChangeTrust) {
                        ChangeTrust ch = (ChangeTrust) action;
                        changeTrust(ch.getUser_id1(), ch.getUser_id2(), ch.getValue());
                        result.setPerformative(ACLMessage.INFORM);
                    } else if (action instanceof RateItem) {
                        RateItem ri = (RateItem) action;
                        rate(ri.getUser_id(), ri.getItem_id(), ri.getRating());
                        result.setPerformative(ACLMessage.INFORM);
                    } else if (action instanceof UpdateModel) {
                        UpdateModel ri = (UpdateModel) action;
                        try {
                            recommenderSystem.runAlgorithm();
                        } catch (Exception e) {
                            System.err.println(e.toString());
                        }
                        result.setPerformative(ACLMessage.INFORM);
                    } else if (action instanceof RequestItems) {
                        RequestItems ri = (RequestItems) action;
                        Items items = getItemsNotRated(ri.getUser_id());
                        result.setPerformative(ACLMessage.INFORM);
                        try {
                            getContentManager().fillContent(result, items);
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    } else if(action instanceof RequestRecommendation) {
                        RequestRecommendation rr = (RequestRecommendation) action;
                        Recommendations recommendations = getRecommendations(rr.getUser_id(), rr.getSize());
                        result.setPerformative(ACLMessage.INFORM);
                        try {
                            getContentManager().fillContent(result, recommendations);
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
            return result;
        }

        public void changeTrust(int user_id1, int user_id2, String value) {
            try {
                recommenderSystem.changeTrust(user_id1, user_id2, value);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

        public void rate(int user_id, int item_id, String rating) {
            try {
                recommenderSystem.rateItem(user_id, item_id, rating);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

        public Recommendations getRecommendations(int user_id, int size) {
            List<Recommendation> recommendations_list = new ArrayList<>();
            List<Map.Entry<Integer, Double>> recommendations = recommenderSystem.requestRecommendation(user_id, size);

            for (Map.Entry<Integer, Double> entry: recommendations)
                recommendations_list.add(new Recommendation(user_id, entry.getKey(), entry.getValue()));

            return new Recommendations(user_id, recommendations_list);
        }

        public Items getItemsNotRated(int user_id) {
            return new Items(recommenderSystem.requestItemsNotRated(user_id));
        }
    }
}

package com.company;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.ArrayList;
import java.util.List;

public class Recommender extends Agent {
    private Codec codec;
    private Ontology recommendationOntology;

    public void setup() {
        System.out.println("Recommender: Setup");

        this.codec = new SLCodec();
        getContentManager().registerLanguage(codec);

        this.recommendationOntology = RecommendationOntology.getInstance();
        getContentManager().registerOntology(recommendationOntology);

        Object[] args = getArguments();
        if(args != null && args.length > 0) {
            // Parse args
        }

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
                    if(action instanceof RequestRecommendations) {
                        RequestRecommendations rr = (RequestRecommendations) action;
                        List<Recommendation> listRecommendations = getRecommendations(rr.getUser_id());
                        Recommendations recommendations = new Recommendations(listRecommendations);

                        result.setPerformative(ACLMessage.INFORM);

                        try {
                            getContentManager().fillContent(result, recommendations);
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    } else if (action instanceof RequestItems) {
                        RequestItems ri = (RequestItems) action;
                        List<Integer> listItems = getItemsNotRated(ri.getUser_id());
                        Items items = new Items(listItems);
                        items.getItems().add(1);

                        result.setPerformative(ACLMessage.INFORM);

                        try {
                            getContentManager().fillContent(result, items);
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    } else if (action instanceof RateItem) {
                        RateItem ri = (RateItem) action;
                        rate(ri.getUser_id(), ri.getItem_id(), ri.getRating());

                        result.setPerformative(ACLMessage.INFORM);

                        try {
                            getContentManager().fillContent(result, null);
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

        public void rate(int user_id, int item_id, double rating) {

        }

        public List<Recommendation> getRecommendations(int user_id) {
            List<Recommendation> recommendations = new ArrayList<Recommendation>();
            Recommendation recommendation = new Recommendation(1, 1, 1.4);
            recommendations.add(recommendation);
            return recommendations;
        }

        public List<Integer> getItemsNotRated(int user_id) {
            List<Integer> items = new ArrayList<Integer>();
            items.add(1);
            return items;
        }

    }
}

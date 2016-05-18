package com.company;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

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

        addBehaviour(new UserBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));
    }

    class UserBehaviour extends AchieveREInitiator {

        public UserBehaviour(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
            System.out.println("User: prepareRequests");

            Vector<ACLMessage> v = new Vector<ACLMessage>();

            msg.setLanguage(codec.getName());
            msg.setOntology(recommendationOntology.getName());

            AID to = new AID("recommender", false);
            msg.addReceiver(to);

            RequestItems sae = new RequestItems(1);
            Action action = new Action(to, sae);

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

            try {
                ContentElement ce = getContentManager().extractContent(inform);
                if(ce instanceof Recommendations) {
                    Recommendations recommendations = (Recommendations) ce;

                    // Print Recommendations
                    System.out.println("Recommendations:");

                    for (Recommendation recommendation: recommendations.recommendations) {
                        System.out.println("user_id: " + recommendation.user_id + ", item_id: "
                                + recommendation.item_id + ", rating: " + recommendation.rating);
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

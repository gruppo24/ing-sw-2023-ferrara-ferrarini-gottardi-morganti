package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCardFactory;


public class Server {

    //array containing the instantiation of CommonCards
    public static final CommonCard[] commonCards = new CommonCard[12];

    private static void createCommonCards(){
        for(int i=0; i<commonCards.length; i++){
            commonCards[i] = CommonCardFactory.cardBuilder(i);
        }
    }


    public static void main(String[] args) {

    }
}

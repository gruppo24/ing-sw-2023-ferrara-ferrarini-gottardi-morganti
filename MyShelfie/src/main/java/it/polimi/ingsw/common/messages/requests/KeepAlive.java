package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.server.controller.Contextable;

public class KeepAlive extends PacketContent {


    @Override
    public boolean performRequestedAction(Contextable context) {
        // No particular action is required...
        //System.out.println("ECHO received for player '" + context.getPlayer() + "'");  // DEBUG ONLY

        return false;
    }
}

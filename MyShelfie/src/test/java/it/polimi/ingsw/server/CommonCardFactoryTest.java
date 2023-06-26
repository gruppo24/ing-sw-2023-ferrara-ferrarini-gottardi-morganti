package it.polimi.ingsw.server;


import it.polimi.ingsw.server.model.CommonCard;
import org.junit.Test;

import static it.polimi.ingsw.server.CommonCardFactory.cardBuilder;
import static org.junit.Assert.assertEquals;


public class CommonCardFactoryTest {

    @Test
    public void cardBuilder_cardBetween0and11_returnsCommonCard() {
        for (int i=0; i <= 11; i++) {
            CommonCard c = cardBuilder(i);
            assertEquals(c.identifier, (i+1) + ".jpg");
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void cardBuilder_cardLessThan0_throwsIllegalArgumentException() {
        cardBuilder(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void cardBuilder_cardGreaterThen11_throwsIllegalArgumentException() {
        cardBuilder(12);
    }

}

package it.polimi.ingsw.client;


import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;


public class ReconnectionHandlerTest {

    private ReconnectionHandler rh;

    @Before
    public void setUp() {
        rh = new ReconnectionHandler();
    }

    @Test
    public void setParameters_storeGameIdUsername_storesGameIdUsername() throws IOException, ClassNotFoundException {
        rh.setParameters("abc", "123");

        String[] parameters = rh.getParameters();

        assertEquals(parameters[0], "abc");
        assertEquals(parameters[1], "123");
    }

}

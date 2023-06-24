package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.messages.requests.ContentType;
import it.polimi.ingsw.common.messages.requests.KeepAlive;
import it.polimi.ingsw.common.messages.responses.RequestPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;


@Deprecated  // Use the concrete implementation of the Connection class
public class AsyncKeepAliveEcho implements Runnable {

    private final ObjectOutputStream channelUplink;
    private final SocketConnection delegate;
    private final int echoMillisDelay;

    /**
     * Class constructor
     *
     * @param channelUplink ObjectOutputStream to be used to send "pings"
     * @param echoMillisDelay number of milliseconds between each "ping"
     */
    public AsyncKeepAliveEcho(ObjectOutputStream channelUplink, SocketConnection delegate, int echoMillisDelay) {
        this.channelUplink = channelUplink;
        this.delegate = delegate;
        this.echoMillisDelay = echoMillisDelay;
    }

    /**
     * Class constructor
     *
     * @param channelUplink ObjectOutputStream to be used to send "pings"
     */
    public AsyncKeepAliveEcho(ObjectOutputStream channelUplink, SocketConnection delegate) {
        // We call the full constructor with a default echoMillisDelay value
        this(channelUplink, delegate, 5_000);
    }

    @Override
    public void run() {
        // As long as the uplink channel is open, we send keep-alive echoes
        // to the server. When (at the end of a game for instance) the channel
        // closes, we break out of the following loop
        while (true) {
            RequestPacket packet = new RequestPacket(ContentType.KEEP_ALIVE, new KeepAlive());
            try {
                this.channelUplink.writeObject(packet);
                this.channelUplink.flush();
                //System.out.println("[DEBUG]: ping sent!");  // DEBUG ONLY

                Thread.sleep(echoMillisDelay);
            } catch (IOException e) {
                //System.out.println("WARNING: AsyncKeepAliveEcho detected disconnection (or game end...)");  // DEBUG ONLY
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

}

package com.zdx.test;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;


/** This example demonstrates how to create a websocket connection to a server. 
 * Only the most important callbacks are overloaded. */
public class WebSocketLocalClient extends WebSocketClient {
	private static Logger logger = Logger.getLogger(WebSocketLocalClient.class);
	public WebSocketLocalClient( URI serverUri , Draft draft ) {
		super( serverUri, draft );
	}

	public WebSocketLocalClient( URI serverURI ) {
		super( serverURI );
	}

	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		logger.info( "opened connection" );
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage( String message ) {
		logger.info( "received: " + message );
	}

	@Override
	public void onFragment( Framedata fragment ) {
		logger.info( "received fragment: " + new String( fragment.getPayloadData().array() ) );
	}

	@Override
	public void onClose( int code, String reason, boolean remote ) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		logger.info( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
	}

	@Override
	public void onError( Exception ex ) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	public static void main( String[] args ) throws URISyntaxException {
		WebSocketLocalClient c = new WebSocketLocalClient( new URI( "ws://182.92.150.57:8001" ), new Draft_6455() ); 
		try {
			c.connectBlocking();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.send("handshake");
	}
}
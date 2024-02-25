package space.itoncek;


import cz.iqlandia.iqplanetarium.chatreciever.ChatReciever;
import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;
import cz.iqlandia.iqplanetarium.chatreciever.utils.Listener;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class Main {
	static ChatReciever reciever;

	public static void main(String[] args) {
		StringBuilder addr = new StringBuilder();
		for (String arg : args) {
			addr.append(arg);
		}

		if (addr.isEmpty()) addr.append("mhJRzQsLZGg");

		reciever = new ChatReciever(addr.toString());
		reciever.handler.addListener(listener);
		reciever.start();

		wss.start();

		Runtime.getRuntime().addShutdownHook(new Thread(reciever::stop));
		//System.out.println();
	}	static Listener listener = new Listener() {
		@Override
		public void startup(String liveID) {
			System.out.println("[SYSTEM] Startup with id " + liveID);
		}

		@Override
		public void shutdown() {
			System.out.println("[SYSTEM] Shutdown");
		}

		@Override
		public void error(String reason) {
			System.out.println("[SYSTEM] Fired error for reason " + reason);
		}

		@Override
		public void message(ChatItem message) {
			if (message.message().startsWith("?")) {
				wss.broadcast(message.serialize().toString(4));
			}
		}
	};

	static WebSocketServer wss = new WebSocketServer(InetSocketAddress.createUnresolved("localhost", 7777)) {

		@Override
		public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

		}

		@Override
		public void onClose(WebSocket webSocket, int i, String s, boolean b) {

		}

		@Override
		public void onMessage(WebSocket webSocket, String s) {
			if (reciever != null) {
				reciever.stop();
			}
			reciever = new ChatReciever(s);
			reciever.handler.addListener(listener);
			reciever.start();
		}

		@Override
		public void onError(WebSocket webSocket, Exception e) {
			System.out.println("[WSS] WSS error:");
			e.printStackTrace();
		}

		@Override
		public void onStart() {
			System.out.println("[WSS] WSS started!");
		}
	};


}
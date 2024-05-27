package space.itoncek;


import cz.iqlandia.iqplanetarium.chatreciever.ChatReciever;
import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;
import cz.iqlandia.iqplanetarium.chatreciever.utils.Listener;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

public class Main {
	static ChatReciever reciever;
	static TreeSet<ChatItem> items = new TreeSet<>();
	public static void main(String[] args) {
		StringBuilder addr = new StringBuilder();
		for (String arg : args) {
			addr.append(arg);
		}

		if (addr.isEmpty()) addr.append("FS95VOiylTs");

		reciever = new ChatReciever(addr.toString());
		reciever.handler.addListener(listener);
		reciever.start();

		wss.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				reciever.stop();
				wss.stop();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}));
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
			wss.broadcast(message.serialize().toString().getBytes(StandardCharsets.UTF_8));
			items.add(message);
		}
	};

	static WebSocketServer wss = new WebSocketServer(new InetSocketAddress("localhost", 7777)) {

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
				reciever.handler.removeListener(listener);
				listener = new Listener() {
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
						System.out.println(new String(message.message().getBytes(StandardCharsets.UTF_8)));
						wss.broadcast(message.serialize().toString());
						items.add(message);
					}
				};
			}
			items.clear();
			reciever = new ChatReciever(s);
			reciever.handler.addListener(listener);
			reciever.start();
		}

		@Override
		public void onError(WebSocket webSocket, Exception e) {
			System.out.println("[WSS] WSS error:");
			e.printStackTrace();
			System.exit(1);
		}

		@Override
		public void onStart() {
			System.out.println("[WSS] WSS started!");
		}
	};


}
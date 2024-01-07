package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;
import cz.iqlandia.iqplanetarium.chatreciever.utils.Listener;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
//        LivePageData data = Requests.fetchLivePage("mhJRzQsLZGg");
//
//        for (ChatItem item : Requests.fetchChat(data).data()) {
//            System.out.println(item.message());
//        }

        Listener listener = new Listener() {
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
                System.out.println(message.author().name() + " >> " + message.message());
            }
        };

        ChatReciever reciever = new ChatReciever("mhJRzQsLZGg");
        reciever.handler.addListener(listener);
        reciever.start();

        Runtime.getRuntime().addShutdownHook(new Thread(reciever::stop));
        //System.out.println();
    }
}
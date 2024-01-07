package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;
import cz.iqlandia.iqplanetarium.chatreciever.utils.Listener;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void fireStartupEvent(String liveID) {
        for (Listener listener : listeners) {
            listener.startup(liveID);
        }
    }

    public void fireShutdownEvent() {
        for (Listener listener : listeners) {
            listener.shutdown();
        }
    }

    public void fireMessageEvent(ChatItem message) {
        for (Listener listener : listeners) {
            listener.message(message);
        }
    }

    public void fireErrorEvent(String reason) {
        for (Listener listener : listeners) {
            listener.error(reason);
        }
    }
}


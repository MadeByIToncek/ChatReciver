package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;

public class EventHandler {
}

interface Events {
    void startup(String liveID);
    void shutdown(String reason);
    void message(ChatItem message);
}
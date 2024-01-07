package cz.iqlandia.iqplanetarium.chatreciever.utils;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;

public interface Listener {
    void startup(String liveID);
    void shutdown();
    void error(String reason);
    void message(ChatItem message);
}

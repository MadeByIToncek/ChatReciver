package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatData;
import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;

import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class ChatReciever {
    public EventHandler handler;

    private String liveID;
    private final Timer timer;
    private LivePageData data;
    private final int interval;
    public ChatReciever(String liveID) {
        handler = new EventHandler();
        this.liveID = liveID;
        timer = new Timer();
        this.interval = 1000;
    }

    public Thread start() {
        Thread t = new Thread(()-> {
            try {
                data = Requests.fetchLivePage(liveID);
                liveID = data.liveID;

                timer.scheduleAtFixedRate(executor(),0,interval);
                handler.fireStartupEvent(liveID);
            } catch (IOException | ParseException e) {
                handler.fireErrorEvent(e.getLocalizedMessage());
            }
        });
        t.start();
        return t;
    }

    public void stop() {
        timer.cancel();
        timer.purge();
        handler.fireShutdownEvent();
        handler.listeners.clear();
    }

    private TimerTask executor() {
        return new TimerTask() {
            @Override
            public void run() {
                if(data == null) {
                    handler.fireErrorEvent("Not found any options");
                    stop();
                } else {
                    try {
                        ChatData chat = Requests.fetchChat(data);
                        for (ChatItem item : chat.data()) {
                            handler.fireMessageEvent(item);
                        }
                        data.continuation = chat.continuation();
                    } catch (IOException e) {
                        handler.fireErrorEvent(e.getLocalizedMessage());
                    }
                }
            }
        };
    }
}
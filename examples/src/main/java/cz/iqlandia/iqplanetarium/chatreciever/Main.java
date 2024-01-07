package cz.iqlandia.iqplanetarium.chatreciever;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        LivePageData data = Requests.fetchLivePage("mhJRzQsLZGg");
        Requests.fetchChat(data);
        //System.out.println();
    }
}
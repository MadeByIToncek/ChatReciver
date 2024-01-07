package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatData;
import cz.iqlandia.iqplanetarium.chatreciever.utils.HTTPManager;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

/**
 * Analog to requests.ts
 */
public class Requests {

    public static LivePageData fetchLivePage(String id) throws IOException, ParseException {
        return Parser.getLivePageData(HTTPManager.getHTTPString("https://www.youtube.com/watch?v="+id));
    }
    public static ChatData fetchChat(LivePageData data) throws IOException {
        //System.out.println(data);
        String url = "https://www.youtube.com/youtubei/v1/live_chat/get_live_chat?key=" + data.apiKey;
        JSONObject body = new JSONObject();
        body.put("context", new JSONObject()
                .put("client", new JSONObject()
                        .put("clientVersion", data.clientVersion)
                        .put("clientName", "WEB")));
        body.put("continuation", data.continuation);

        JSONObject res = HTTPManager.postHTTPJSONObject(url, body);
        //System.out.println(res.toString(4));
        return Parser.parseChatData(res);
    }
}

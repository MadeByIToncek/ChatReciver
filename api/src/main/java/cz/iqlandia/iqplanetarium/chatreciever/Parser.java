package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatData;
import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;
import org.json.JSONObject;

import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static LivePageData getLivePageData(String data) throws ParseException {
        String liveID, apiKey, clientVersion, continuation;

        Pattern pattern = Pattern.compile("<link rel=\"canonical\" href=\"https://www.youtube.com/watch\\?v=(.+?)\">");
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            liveID = matcher.group(1);
        } else {
            throw new ParseException("Unable to find liveID!", 0);
        }

        pattern = Pattern.compile("['\"]isReplay['\"]:\\s*(true)");
        matcher = pattern.matcher(data);

        if(matcher.find()) {
            throw new ParseException("This video is a replay!", matcher.start(0));
        }

        pattern = Pattern.compile("['\"]INNERTUBE_API_KEY['\"]:\\s*['\"](.+?)['\"]");
        matcher = pattern.matcher(data);

        if (matcher.find()) {
            apiKey = matcher.group(1);
        } else {
            throw new ParseException("Unable to find apiKey!", 0);
        }

        pattern = Pattern.compile("['\"]clientVersion['\"]:\\s*['\"]([\\d.]+?)['\"]");
        matcher = pattern.matcher(data);

        if (matcher.find()) {
            clientVersion = matcher.group(1);
        } else {
            throw new ParseException("Unable to find clientVersion!", 0);
        }

        pattern = Pattern.compile("['\"]continuation['\"]:\\s*['\"](.+?)['\"]");
        matcher = pattern.matcher(data);

        if (matcher.find()) {
            continuation = matcher.group(1);
        } else {
            throw new ParseException("Unable to find continuation!", 0);
        }

        return new LivePageData(liveID,apiKey,clientVersion,continuation);
        //return null;
    }

    public static ChatData parseChatData(JSONObject data) {
        List<ChatItem> chatItems = new ArrayList<>();
        if(data.getJSONObject("continuationContents")
                .getJSONObject("liveChatContinuation")
                .getJSONArray("actions") != null) {
            chatItems = data.getJSONObject("continuationContents").getJSONObject("liveChatContinuation").getJSONArray("actions").toList().stream()
                    .map(o -> (JSONObject) o)
                    .map(Parser::parseActionToChatItem)
                    .filter(Objects::nonNull).toList();
        }

        JSONObject continuationData = data.getJSONObject("continuationContents")
                .getJSONObject("liveChatContinuation")
                .getJSONArray("continuations")
                .getJSONObject(0);

        String continuation = "";

        if(continuationData.has("invalidationContinuationData")) {
            continuation = continuationData
                    .getJSONObject("invalidationContinuationData")
                    .getString("continuation");
        } else if (continuationData.has("timedContinuationData")) {
            continuation = continuationData
                    .getJSONObject("timedContinuationData")
                    .getString("continuation");
        }

        return new ChatData(chatItems, continuation);
    }

    public ChatItem.ImageItem parseThumbnailToImageItem(String[] data, String alt) {
        String url = data[data.length - 1];
        if(url != null) {
            return new ChatItem.ImageItem(url, alt);
        } else return new ChatItem.ImageItem("","");
    }

    public Color convertColorToHex6(int colorNum) {
        return new Color(colorNum,true);
    }

    private static ChatItem parseActionToChatItem(JSONObject action) {
        return null;
    }
}

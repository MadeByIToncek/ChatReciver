package cz.iqlandia.iqplanetarium.chatreciever;

import cz.iqlandia.iqplanetarium.chatreciever.records.ChatData;
import cz.iqlandia.iqplanetarium.chatreciever.records.ChatItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
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
        if (data.getJSONObject("continuationContents").getJSONObject("liveChatContinuation").has("actions")) {
            if (data.getJSONObject("continuationContents")
                    .getJSONObject("liveChatContinuation")
                    .getJSONArray("actions") != null) {
                chatItems = JSONToList(data.getJSONObject("continuationContents").getJSONObject("liveChatContinuation").getJSONArray("actions")).stream()
                        .map(o -> (JSONObject) o)
                        .map(Parser::parseActionToChatItem)
                        .filter(Objects::nonNull).toList();
            }
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

    private static List<Object> JSONToList(JSONArray list) {
        List<Object> objects = new ArrayList<>();
        for (Object o : list) {
            objects.add(o);
        }
        return objects;
    }

    public static ChatItem.ImageItem parseThumbnailToImageItem(List<String> data, String alt) {
        String url = data.get(data.size() - 1);
        if(url != null) {
            return new ChatItem.ImageItem(url, alt);
        } else return new ChatItem.ImageItem("","");
    }

    public static Color convertColorToHex6(int colorNum) {
        return new Color(colorNum,true);
    }

    public static String parseMessages(List<String> runs) {
        StringJoiner js = new StringJoiner("");
        for (String run : runs) {
            js.add(run);
        }
        return js.toString();
    }

    private static ChatItem parseActionToChatItem(JSONObject action) {
        if(!action.has("addChatItemAction")) return null;

        JSONObject metaItem = action.getJSONObject("addChatItemAction").getJSONObject("item");

        if(!metaItem.has("liveChatTextMessageRenderer")) {
            return null;
        }

        JSONObject item = metaItem.getJSONObject("liveChatTextMessageRenderer");

        JSONArray messages = item.getJSONObject("message").getJSONArray("runs");
        String message = parseMessages(JSONToList(messages).stream()
                .map(o -> (JSONObject) o)
                .map(Parser::convertToString)
                .filter(Objects::nonNull)
                .toList());

        String authorName = item.has("authorName")?item.getJSONObject("authorName").getString("simpleText") : "<unable to parse>";
        String authorID = item.getString("authorExternalChannelId");

        ChatItem.ImageItem thumbnail = parseThumbnailToImageItem(JSONToList(item.getJSONObject("authorPhoto").getJSONArray("thumbnails")).stream()
                .map((o) -> (JSONObject) o)
                .map(o -> o.getString("url"))
                .filter(Objects::nonNull)
                .toList(),authorName);

        Optional<ChatItem.Author.Badge> badge = Optional.empty();
        boolean isVerified = false, isOwner = false, isModerator = false;

        if(item.has("authorBadges")) {
            for (JSONObject o : JSONToList(item.getJSONArray("authorBadges")).stream().map(o -> (JSONObject) o).toList()) {
                if (o.has("customThumbnail")) {
                    badge = Optional.of(new ChatItem.Author.Badge(
                            parseThumbnailToImageItem(JSONToList(o.getJSONObject("customThumbnail").getJSONArray("thumbnails")).stream()
                                    .map((ob) -> (JSONObject) ob)
                                    .map(ob -> ob.getString("url"))
                                    .filter(Objects::nonNull)
                                    .toList(),o.getString("tooltip")),
                            o.getString("tooltip")
                    ));
                } else if (o.has("icon")){
                    switch (o.getJSONObject("icon").getString("iconType")) {
                        case "OWNER" -> isOwner = true;
                        case "VERIFIED" -> isVerified = true;
                        case "MODERATOR" -> isModerator = true;
                    }
                }
            }
        }


        ChatItem.Author author = new ChatItem.Author(authorName, Optional.of(thumbnail), authorID, badge, isVerified, isOwner, isModerator);
        return new ChatItem(item.getString("id"), author, message, ZonedDateTime.ofInstant(Instant.ofEpochSecond(item.getLong("timestampUsec")/1000000), ZoneId.of("Europe/Prague")));
    }

    private static String convertToString(JSONObject o) {
        if(o.has("text")) {
            return o.getString("text");
        } else if (o.has("emoji")) {
            if(o.getJSONObject("emoji").has("isCustomEmoji")) {
                return ":"+o.getJSONObject("emoji")
                        .getJSONObject("image")
                        .getJSONObject("accessibility")
                        .getJSONObject("accessibilityData")
                        .getString("label") + ":";
            } else {
                return o.getJSONObject("emoji")
                        .getJSONObject("image")
                        .getJSONObject("accessibility")
                        .getJSONObject("accessibilityData")
                        .getString("label");
            }
        }
        else return null;
    }
}

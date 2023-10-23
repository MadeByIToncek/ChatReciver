using ChatReciver.utils;
using Newtonsoft.Json.Linq;
using System.Text.RegularExpressions;

namespace ChatReciver
{
    internal class Parser
    {
        //Done
        public static LivePageData getOptionsFromLivePage(string resp, string id)
        {
#pragma warning disable CS8604 // Possible null reference argument.
            LivePageData data = new();

            data.liveId = id;

            string api = @"['""]INNERTUBE_API_KEY['""]:\s*['""](.+?)['""]";
            string cliver = @"['""]clientVersion['""]:\s*['""]([\d.]+?)['""]";
            string cont = @"['""]continuation['""]:\s*['""](.+?)['""]";

            Match mApi = Regex.Match(resp, api);
            Match mCliVer = Regex.Match(resp, cliver);
            Match mCont = Regex.Match(resp, cont);

            if (!mApi.Success)
            {
                throw new Exception("Unable to find api key");
            }
            else
            {
                data.apiKey = JObject.Parse("{" + mApi.Value + "}")
                                     .GetValue("INNERTUBE_API_KEY")
                                     .Value<string>();
            }

            if (!mCliVer.Success)
            {
                throw new Exception("Unable to find client version");
            }
            else
            {
                data.clientVersion = JObject.Parse("{" + mCliVer.Value + "}")
                                     .GetValue("clientVersion")
                                     .Value<string>();
            }

            if (!mCont.Success)
            {
                throw new Exception("Unable to find client version");
            }
            else
            {
                data.continuation = JObject.Parse("{" + mCont.Value + "}")
                                     .GetValue("continuation")
                                     .Value<string>();
            }

            return data;
#pragma warning restore CS8604 // Possible null reference argument.
        }

        public static ChatData parseChatData(JObject json)
        {
            if (json == null) throw new Exception("Response unavaliable");

            ChatData data = new();


            JArray actions = json["continuationContents"]["liveChatContinuation"]["actions"].Value<JArray>();
            ChatItem[] objectArray = new ChatItem[actions.Count()];
            if (actions != null)
            {
                int index = 0;
                foreach (JObject item in actions)
                {
                    objectArray[index] = Parser.parseActionToChatItem(item);
                    index++;
                }
            }

            data.chatItems = objectArray;
            JObject continuationData = json["continuationContents"]["liveChatContinuation"]["continuations"][0].Value<JObject>();
            string continuation = "";

            if (continuationData["invalidationContinuationData"] != null)
            {
                continuation = continuationData["invalidationContinuationData"]["continuation"].Value<String>();
            } else if (continuationData["timedContinuationData"] != null)
            {
                continuation = continuationData["timedContinuationData"]["continuation"].Value<String>();
            }

            data.continuation = continuation;

            return data;
        }


        public static string parseThumbnail(JArray data)
        {
            JObject imageData = data[-1].Value<JObject>();
            return imageData["url"].Value<string>();
        }

        public static string converColorToHex6(uint colorNum)
        {
            return "#" + (colorNum.ToString("X")).Substring(0).ToUpper();
        }

        public static string parseMessage(JArray messages)
        {
            string[] strings = new string[messages.Count];
            int i = 0;
            foreach (JObject item in messages)
            {
                if (messages.Contains("texz"))
                {
                    strings[i] = item["text"].Value<string>();
                } else
                {

                    strings[i] = item["emoji"]["emojiId"].Value<string>();
                }
                i++;
            }
            string output = string.Join("; ", strings);
            return output;
        }

        public static JObject renderFromAction(JObject data) 
        {
            if (data["addChatItemAction"] == null) return null;
            JObject item = data["addChatItemAction"]["item"].Value<JObject>();

            if (isNotNull(item["liveChatTextMessageRenderer"])) return item["liveChatTextMessageRenderer"].Value<JObject>();
            else if (isNotNull(item["liveChatPaidMessageRenderer"])) return item["liveChatPaidMessageRenderer"].Value<JObject>();
            else if (isNotNull(item["liveChatPaidStickerRenderer"])) return item["liveChatPaidStickerRenderer"].Value<JObject>();
            else if (isNotNull(item["liveChatMembershipItemRenderer"])) return item["liveChatMembershipItemRenderer"].Value<JObject>();
            else return null;
        }

        private static bool isNotNull(JToken? jToken)
        {
            return jToken != null;
        }

        public static ChatItem parseActionToChatItem(JObject data)
        {
            ChatItem item = new ();
            JObject messageRender = renderFromAction(data);
            if (messageRender == null) return null;

            JArray messages = new JArray();

            if(data.ContainsKey("message"))
            {
                messages = data["message"]["runs"].Value<JArray>();
            } else if (data.ContainsKey("headerSubtext"))
            {
                messages = data["headerSubtext"]["runs"].Value<JArray>();
            }
            string authorNameText = messageRender["authorName"]?["simpleText"].Value<string>() ?? "";

            item.id = messageRender["id"].Value<string>();

            Author author = new Author();
            author.name = authorNameText;
            author.thumbnail = parseThumbnail(messageRender["authorPhoto"]["thumbnails"].Value<JArray>());
            author.channelID = messageRender["authorExternalChannelId"].Value<string>();

            if (messageRender.ContainsKey("authorBadges"))
            {
                foreach (JObject entry in messageRender["authorBadges"].Value<JArray>())
                {
                    if(entry.ContainsKey("customThumbnail"))
                    {

                    }
                }
            }

            item.author = author;
            item.message = parseMessage(messages);

            return null;
        }
    }
}

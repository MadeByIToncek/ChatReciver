using ChatReciver.utils;
using Newtonsoft.Json.Linq;
using System.Text.RegularExpressions;
using System.Linq;

namespace ChatReciver
{
    internal class Parser
    {
        //Done
        public static LivePageData GetOptionsFromLivePage(string resp, string id)
        {
#pragma warning disable CS8604 // Possible null reference argument.
            LivePageData data = new()
            {
                liveId = id
            };

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

        public static ChatData ParseChatData(JObject json)
        {

#pragma warning disable CS8604 // Possible null reference argument.
            if (json == null) throw new Exception("Response unavaliable");

            ChatData data = new();

            JArray actions = json["continuationContents"]?["liveChatContinuation"]?["actions"]?.Value<JArray>();
            ChatItem[] objectArray = new ChatItem[actions.Count];

            if (actions != null)
            {
                int index = 0;
                foreach (JObject item in actions.Cast<JObject>())
                {
                    if (!item.ContainsKey("addChatItemAction")) continue;
                    if (!item["addChatItemAction"].Contains("item")) continue;
                    if (!item["addChatItemAction"]["item"].Contains("liveChatTextMessageRenderer")) continue;
                    objectArray[index] = ParseActionToChatItem(item);
                    index++;
                }
            }

            data.chatItems = objectArray;
            JObject continuationData = json["continuationContents"]["liveChatContinuation"]["continuations"][0].Value<JObject>();
            string continuation = "";

            if (continuationData["invalidationContinuationData"] != null)
            {
                continuation = continuationData["invalidationContinuationData"]["continuation"].Value<String>();
            }
            else if (continuationData["timedContinuationData"] != null)
            {
                continuation = continuationData["timedContinuationData"]["continuation"].Value<String>();
            }

            data.continuation = continuation;

#pragma warning restore CS8604 // Possible null reference argument.
            return data;
        }


        public static string ParseThumbnail(JArray data)
        {
            JObject imageData = data.Last.Value<JObject>();
            return imageData["url"].Value<string>();
        }

        public static string ConverColorToHex6(uint colorNum)
        {
            return "#" + (colorNum.ToString("X"))[..].ToUpper();
        }

        public static string ParseMessage(JArray messages)
        {
            string[] strings = new string[messages.Count];
            int i = 0;
            foreach (JObject item in messages.Cast<JObject>())
            {
                if (messages.Contains("text"))
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

        public static JObject RenderFromAction(JObject data) 
        {
            if (data["addChatItemAction"] == null) return null;
            JObject item = data["addChatItemAction"]["item"].Value<JObject>();

            if (IsNotNull(item["liveChatTextMessageRenderer"])) return item["liveChatTextMessageRenderer"].Value<JObject>();
            else if (IsNotNull(item["liveChatPaidMessageRenderer"])) return item["liveChatPaidMessageRenderer"].Value<JObject>();
            else if (IsNotNull(item["liveChatPaidStickerRenderer"])) return item["liveChatPaidStickerRenderer"].Value<JObject>();
            else if (IsNotNull(item["liveChatMembershipItemRenderer"])) return item["liveChatMembershipItemRenderer"].Value<JObject>();
            else return null;
        }

        private static bool IsNotNull(JToken? jToken)
        {
            return jToken != null;
        }

        public static ChatItem ParseActionToChatItem(JObject data)
        {
            Console.WriteLine(data.ToString());
            ChatItem item = new ();
            JObject messageRender = RenderFromAction(data);
            if (messageRender == null) return null;

            JArray messages = new();

            if(data.ContainsKey("message"))
            {
                messages = data["message"]?["runs"]?.Value<JArray>();
            } else if (data.ContainsKey("headerSubtext"))
            {
                messages = data["headerSubtext"]["runs"].Value<JArray>();
            }
            string authorNameText = messageRender["authorName"]?["simpleText"]?.Value<string>() ?? "";

            item.id = messageRender["id"].Value<string>();

            Author author = new()
            {
                name = authorNameText,
                thumbnail = ParseThumbnail(messageRender["authorPhoto"]["thumbnails"].Value<JArray>()),
                channelID = messageRender["authorExternalChannelId"].Value<string>()
            };
            if (messageRender.ContainsKey("authorBadges"))
            {
                foreach (var entry in from JObject entry in messageRender["authorBadges"].Value<JArray>()
                                      where !entry.ContainsKey("customThumbnail")
                                      select entry)
                {

                    //switch (entry["liveChatAuthorBadgeRenderer"]["icon"]["iconType"].Value<string>())
                    //{
                    //    case "OWNER":
                    //        author.isOwner = true;
                    //        break;
                    //    case "VERIFIED":
                    //        author.isVerified = true;
                    //        break;
                    //    case "MODERATOR":
                    //        author.isModerator = true;
                    //        break;
                    //}
                }
            }

            item.author = author;
            item.message = ParseMessage(messages);
            item.timestamp = DateTime.UnixEpoch.AddMicroseconds(double.Parse(messageRender["timestampUsec"].Value<string>()));
            if(messageRender.ContainsKey("sticker"))
            {
                Superchat superchat = new()
                {
                    amount = messageRender["stickerpurchaseAmountText"]["simpleText"].Value<string>(),
                    color = ConverColorToHex6(uint.Parse(messageRender["backgroundColor"].Value<string>())),
                    sticker = ParseThumbnail(messageRender["sticker"]["thumbnails"].Value<JArray>())
                };
                item.superchat = superchat;
            } else if (messageRender.ContainsKey("purchaseAmountText"))
            {
                Superchat superchat = new()
                {
                    amount = messageRender["purchaseAmountText"]["simpleText"].Value<string>(),
                    color = ConverColorToHex6(uint.Parse(messageRender["bodyBackgroundColor"].Value<string>()))
                };
                item.superchat = superchat;
            }
            return item;
        }
    }
}

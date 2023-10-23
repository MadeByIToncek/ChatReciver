using ChatReciver.utils;
using Newtonsoft.Json.Linq;
using System.Linq;

namespace ChatReciver
{
    internal class Requests
    {
        public static ChatData fetchChat()
        {
            string url = "https://www.youtube.com/youtubei/v1/live_chat/get_live_chat?key=${options.apiKey}";

            JObject res = new HTTPManager(ChatReciver.Program.client).GetAsyncJObject(url).Result;

            return Parser.parseChatData(res);
        }

    }
}

using ChatReciver.utils;
using Newtonsoft.Json.Linq;

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

        //Done
        public static LivePageData fetchLivePage(string id)
        {
            string url = generateLiveUrl(id);
            if (url == null)
            {
                throw new Exception("ID not avaliable");
            }

            string resp = new HTTPManager(Program.client).GetAsyncString(url).Result;

            return Parser.getOptionsFromLivePage(resp, id);
        }

        //Done
        public static string generateLiveUrl(string YoutubeId)
        {
            return "https://www.youtube.com/watch?v=" + YoutubeId;
        }
    }
}

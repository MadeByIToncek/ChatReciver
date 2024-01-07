using ChatReciver.utils;
using Newtonsoft.Json.Linq;

namespace ChatReciver
{
    internal class Requests
    {
        public static async Task<ChatData> FetchChat(LivePageData options)
        {
            string url = "https://www.youtube.com/youtubei/v1/live_chat/get_live_chat?key=" + options.apiKey;
            JObject body = new JObject();
            JObject context = new JObject();
            JObject client = new JObject();

            client["clientVersion"] = options.clientVersion;
            client["clientName"] = "WEB";

            context["client"] = client;
            body["context"] = context;
            body["continuation"] = options.continuation;

            JObject res = await new HTTPManager(ChatReciver.Program.client).PostAsyncJObject(url, body.ToString());
            //Console.WriteLine(res.ToString());
            return Parser.ParseChatData(res);
            //return null;
        }

        //Done
        public static async Task<LivePageData> FetchLivePage(string id)
        {
            string url = GenerateLiveUrl(id) ?? throw new Exception("ID not avaliable");
            string resp = await new HTTPManager(Program.client).GetAsyncString(url);

            return Parser.GetOptionsFromLivePage(resp, id);
        }

        //Done
        public static string GenerateLiveUrl(string YoutubeId)
        {
            return "https://www.youtube.com/watch?v=" + YoutubeId;
        }
    }
}

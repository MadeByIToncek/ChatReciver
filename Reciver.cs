using ChatReciver.utils;

namespace ChatReciver
{
    internal class Reciver
    {
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

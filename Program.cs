using ChatReciver.utils;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace ChatReciver
{
    internal class Program
    {
        public static readonly HttpClient client = new();
        static async Task Main(string[] args)
        {
            //Console.WriteLine("HTTP client ready!");
            // Console.WriteLine(GetAsync("https://www.youtube.com/youtubei/v1/live_chat/get_live_chat?key="));
            //Console.WriteLine(Reciver.fetchLivePage("mhJRzQsLZGg").ToString());
            //Console.WriteLine(Parser.converColorToHex6(3019898879));

            //LivePageData data = await Requests.FetchLivePage("mhJRzQsLZGg");
            //Console.WriteLine(data);
            
            ActiveLiveChat alc = new ActiveLiveChat("mhJRzQsLZGg", 1000);
            alc.Startup();

            Console.ReadKey();
            
            alc.Stop("user asked to do so.");
        }
    }

    internal class ActiveLiveChat : LiveChat
    {
        public ActiveLiveChat(string liveId, double interval) : base(liveId, interval)
        {
        }

        public override void Chat(ChatItem chatItem)
        {
            Console.WriteLine(chatItem.message);
        }

        public override void End(string? reason)
        {
            Console.WriteLine("Shutdown because " + reason);
        }

        public override void Error(string error)
        {
            Console.WriteLine("Error because " + error);
        }

        public override void Start(string liveId)
        {
            Console.WriteLine("Starting with id " + liveId);
        }

    }
}
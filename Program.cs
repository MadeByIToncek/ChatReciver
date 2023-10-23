namespace ChatReciver
{
    internal class Program
    {
        public static readonly HttpClient client = new HttpClient();
        static void Main(string[] args)
        {
            //Console.WriteLine("HTTP client ready!");
            // Console.WriteLine(GetAsync("https://www.youtube.com/youtubei/v1/live_chat/get_live_chat?key="));
            //Console.WriteLine(Reciver.fetchLivePage("mhJRzQsLZGg").ToString());
            Console.WriteLine(Parser.converColorToHex6(3019898879));
        }
    }
}
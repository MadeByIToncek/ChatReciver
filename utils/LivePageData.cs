namespace ChatReciver.utils
{
    //Done
    public class LivePageData
    {
        public string? liveId;
        public string? apiKey;
        public string? clientVersion;
        public string? continuation;

        override public string ToString()
        {
            return "[" + liveId + "," + apiKey + "," + clientVersion + "," + continuation + "]";
        }
    }
}
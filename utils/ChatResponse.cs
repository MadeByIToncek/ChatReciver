namespace ChatReciver.utils
{
    public class ChatData
    {
        public ChatItem[] chatItems;
        public string continuation;
    }

    public class ChatItem
    {
        public string id;
        public Author author;
        public string message;
        public Superchat? superchat;
        public DateTime timestamp;
    }

    public class Author
    {
        public string name;
        public string thumbnail;
        public string channelID;
        public string badgeThumbnail;
        public bool isMembership = false;
        public bool isVerified = false;
        public bool isOwner = false;
        public bool isModerator = false;
    }

    public class Superchat
    {
        public string amount;
        public string color;
        public string? sticker;
    }
}